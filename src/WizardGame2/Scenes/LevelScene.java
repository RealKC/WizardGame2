package WizardGame2.Scenes;

import WizardGame2.*;
import WizardGame2.GameObjects.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LevelScene implements Scene, Player.LevelUpObserver {
    private final Level level;

    private final Player player;
    private final Player.Data characterData;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<ExperienceObject> experienceObjects = new ArrayList<>();

    /**
     * The number of seconds that have passed since the start of the level
     */
    private int secondsPassed = 0;

    public Player.Data getCharacterData() {
        return characterData;
    }

    private enum NextScene {
        NONE,
        PAUSE_MENU,
        LEVEL_UP,
        GAME_OVER,
    }

    private NextScene nextScene = NextScene.NONE;

    private int score = 0;

    private static LevelScene instance;

    public static LevelScene initializeInstance(LevelData levelData, Player.Data characterData) {
        assert instance == null;

        instance = new LevelScene(levelData, characterData);

        return instance;
    }

    public static LevelScene getInstance() {
        assert instance != null;

        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public static LevelScene restartLevel() {
        var data = instance.level.getData();
        data.reset();
        var characterData = instance.characterData;
        instance = null;

        return initializeInstance(data, characterData);
    }

    LevelScene(LevelData levelData, Player.Data characterData) {
        this.characterData = characterData;

        Timer timeTicker = new Timer();
        timeTicker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               LevelScene.this.tickASecond();
            }
        }, 0, 1000);

        var assets = Assets.getInstance();

        level = Level.fromData(levelData);

        player = new Player(assets.getCharacters(), level.getWidth() / 2, level.getHeight() / 2, characterData);
        player.getCamera().setCameraWidth(Game.getInstance().getWindowWidth());
        player.getCamera().setCameraHeight(Game.getInstance().getWindowHeight());
        player.setLevelUpObserver(this);

        player.addPositionObserver(level);

        player.getCamera().setMapWidth(level.getWidth());
        player.getCamera().setMapHeight(level.getHeight());
    }

    @Override
    public SceneUpdate update(long currentTime) {
        if (Main.IS_DEBUG_BUILD) {
            if (Keyboard.isKeyPressed(KeyEvent.VK_F12)) {
                player.takeDamage(1000000000);
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F6)) {
                secondsPassed += 20;
                System.out.println("seconds passed: " + secondsPassed);
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F7)) {
                secondsPassed = 7 * 60 - 5;
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F8)) {
                secondsPassed = 14 * 60 - 5;
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F11)) {
                DatabaseManager.getInstance().setLastBeatLevel(level.getId());
                level.setHasBeenWon(true);
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F10)) {
                player.addExperience(500);
            }
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            nextScene = NextScene.PAUSE_MENU;
        }

        if (level.hasBeenWon()) {
            nextScene = NextScene.GAME_OVER;
        }

        if (nextScene != NextScene.NONE) {
            return SceneUpdate.NEXT_SCENE;
        }

        player.update(level, currentTime);

        int idx = 0;
        while (idx < enemies.size()) {
            var enemy = enemies.get(idx);
            enemy.update(level, currentTime);

            if (player.collidesWith(enemy)) {
                player.takeDamage(enemy.getAttackDamage());
            }
            idx++;
        }

        if (player.isDead()) {
            nextScene = NextScene.GAME_OVER;
        }

        for (var bullet : bullets) {
            bullet.update(level, currentTime);
        }

        int i = 0;
        while (i < bullets.size()) {
            int j = 0;
            Bullet bullet = bullets.get(i);
            boolean incrementIdx = true;

            if (bullet.getTarget() == Bullet.Target.ENEMY){
                while (j < enemies.size()) {
                    Enemy enemy = enemies.get(j);
                    Enemy.Died died = Enemy.Died.NO;
                    if (bullet.collidesWith(enemy)) {

                        died = enemy.takeDamage(bullet.getAttackDamage());
                        if (died == Enemy.Died.YES) {
                            experienceObjects.add(new ExperienceObject(
                                    Assets.getInstance().getObstacles(),
                                    enemy.getX(),
                                    enemy.getY(),
                                    enemy.getHitboxWidth(),
                                    enemy.getHitboxHeight(),
                                    enemy.getScoreValue()
                            ));

                            score += enemy.getScoreValue();
                            enemies.remove(j);
                        }

                        if (bullet.shouldBeRemovedAfterThisHit()) {
                            bullets.remove(i);
                            incrementIdx = false;
                            break;
                        }
                    }

                    if (died == Enemy.Died.NO) {
                        j++;
                    }
                }
            }

            if (bullet.collidesWith(player) && bullet.getTarget() == Bullet.Target.PLAYER) {
                bullets.remove(i);
                incrementIdx = false;
                player.takeDamage(bullet.getAttackDamage());
            }

            if (incrementIdx) {
                i++;
            }
        }

        return SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        level.render(gfx, player.getCamera());

        for (var bullet : bullets) {
            bullet.render(gfx, player.getCamera().getX(), player.getCamera().getY());
        }

        for (var experienceObject : experienceObjects) {
            experienceObject.render(gfx, player.getCamera().getX(), player.getCamera().getY());
        }

        player.render(gfx, player.getCamera().getX(), player.getCamera().getY());

        // necessary because we concurrently add elements to the enemies list in a timer thread
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).render(gfx, player.getCamera().getX(), player.getCamera().getY());
        }

        gfx.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        var currentTime = String.format("%02d:%02d", secondsPassed / 60, secondsPassed % 60);
        var width = (int) gfx.getFontMetrics().getStringBounds(currentTime, gfx).getWidth();
        Utils.drawTextWithOutline(gfx, currentTime, Game.getInstance().getWindowWidth() / 2 - width / 2, 50);
    }

    public boolean getIsPaused() {
        return nextScene == NextScene.PAUSE_MENU;
    }

    public void setPaused(boolean paused) {
        if (paused) {
            nextScene = NextScene.PAUSE_MENU;
        } else {
            nextScene = NextScene.NONE;
        }
    }

    @Override
    public Scene nextScene() {
        switch (nextScene) {
            case LEVEL_UP -> {
                nextScene = NextScene.NONE;
                return new LevelUpScene(this);
            }
            case PAUSE_MENU -> {
                return new PauseMenuScene(this);
            }
            case GAME_OVER -> {
                if (level.hasBeenWon()) {
                    var unlockedCharacter = level.getData().getUnlocks();
                    if (unlockedCharacter != null) {
                        DatabaseManager.getInstance().unlockCharacter(unlockedCharacter);
                    }
                }

                return new GameOverScene(this, level.hasBeenWon() ? GameOverScene.GameResult.WON : GameOverScene.GameResult.LOST);
            }
            case NONE -> {
                return null;
            }
        }

        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return level.getName();
    }

    public int getNextLevel() {
        return level.getData().getNextLevel();
    }

    /**
     * Gets all the bullets in the game
     */
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        player.addPositionObserver(enemy);
    }

    public ArrayList<ExperienceObject> getExperienceObjects() {
        return experienceObjects;
    }

    public void onLevelLeave() {
        var date = new Date();
        var databaseManager = DatabaseManager.getInstance();
        databaseManager.addNewScoreEntry(level.getName(), date.toString(), score);
        if (level.hasBeenWon()) {
            databaseManager.setLastBeatLevel(level.getId());
        }
    }

    private synchronized void tickASecond() {
        if (LevelScene.this.getIsPaused()) {
            return;
        }

        secondsPassed++;

        try {
            if (level != null) {
                ArrayList<Enemy> enemies = level.maybeSpawn(secondsPassed);

                if (enemies != null) {
                    this.enemies.addAll(enemies);
                    player.addPositionObservers(enemies);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // ignore this specific type of exception, since it kills our timer otherwise
            // and it can only happen when there's no enemies anyway
        }
    }

    @Override
    public void notifyAboutLevelUp(int level) {
        nextScene = NextScene.LEVEL_UP;
    }
}
