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
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();
    private final ArrayList<ExperienceObject> experienceObjects = new ArrayList<>();

    /**
     * The number of seconds that have passed since the start of the level
     */
    private int secondsPassed = 0;

    private enum NextScene {
        NONE,
        PAUSE_MENU,
        LEVEL_UP,
        GAME_OVER,
    }

    private NextScene nextScene = NextScene.NONE;

    private int score = 0;

    private static LevelScene instance;

    public static LevelScene initializeInstance(LevelData levelData) {
        assert instance == null;

        instance = new LevelScene(levelData);

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
        instance = null;

        return initializeInstance(data);
    }

    LevelScene(LevelData levelData) {
        Timer timeTicker = new Timer();
        timeTicker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               LevelScene.this.tickASecond();
            }
        }, 0, 1000);

        var assets = Assets.getInstance();

        level = Level.fromData(levelData);

        player = new Player(assets.getCharacters(), level.getWidth() / 2, level.getHeight() / 2, assets.getCharacterStats().get(0));
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

            if (Keyboard.isKeyPressed(KeyEvent.VK_F7)) {
                secondsPassed = 7 * 60 - 5;
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F8)) {
                secondsPassed = 14 * 60 - 5;
            }

            if (Keyboard.isKeyPressed(KeyEvent.VK_F11)) {
                DatabaseManager.getInstance().setLastBeatLevel(level.getId());
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

        for (var enemy : enemies) {
            enemy.update(level, currentTime);

            if (player.collidesWith(enemy)) {
                player.takeDamage(enemy.getAttackDamage());
            }
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
            while (j < enemies.size()) {
                Enemy enemy = enemies.get(j);
                Enemy.Died died = Enemy.Died.NO;
                if (enemy.collidesWith(bullet)) {

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
                        break;
                    }
                }

                if (died == Enemy.Died.NO) {
                    j++;
                }
            }

            i++;
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

        for (var enemy : enemies) {
            enemy.render(gfx, player.getCamera().getX(), player.getCamera().getY());
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
            case LEVEL_UP: {
                nextScene = NextScene.NONE;
                return new LevelUpScene(this);
            }

            case PAUSE_MENU:
                return new PauseMenuScene(this);

            case GAME_OVER:
                return new GameOverScene(this, level.hasBeenWon() ? GameOverScene.GameResult.WON : GameOverScene.GameResult.LOST);

            case NONE:
                return null;
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

    /**
     * Gets all the bullets in the game
     */
    public ArrayList<Bullet> getBullets() {
        return bullets;
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

        if (level != null) {
            ArrayList<Enemy> enemies = level.maybeSpawn(secondsPassed);

            if (enemies != null) {
                this.enemies.addAll(enemies);
                player.addPositionObservers(enemies);
            }
        }
    }

    @Override
    public void notifyAboutLevelUp(int level) {
        nextScene = NextScene.LEVEL_UP;
    }
}
