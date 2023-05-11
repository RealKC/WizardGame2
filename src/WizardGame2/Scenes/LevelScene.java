package WizardGame2.Scenes;

import WizardGame2.*;
import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.ExperienceObject;
import WizardGame2.GameObjects.Player;

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

    /**
     * Hack alert!
     */
    boolean firstUpdate = true;

    private enum NextScene {
        NONE,
        PAUSE_MENU,
        LEVEL_UP,
    }

    private NextScene nextScene = NextScene.NONE;

    private int score = 0;

    private static LevelScene instance;

    public static LevelScene getInstance() {
        if (instance == null) {
            instance = new LevelScene();
        }

        return instance;
    }

    public static void reset() {
        instance = null;
    }

    LevelScene() {
        Timer timeTicker = new Timer();
        timeTicker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               LevelScene.this.tickASecond();
            }
        }, 0, 1000);

        var assets = Assets.getInstance();

        player = new Player(assets.getCharacters(), 800, 600);
        player.getCamera().setCameraWidth(Game.getInstance().getWindowWidth());
        player.getCamera().setCameraHeight(Game.getInstance().getWindowHeight());
        player.setLevelUpObserver(this);

        level = Level.fromData(assets.getLevelDatas().get(0));
        player.addPositionObserver(level);

        player.getCamera().setMapWidth(level.getWidth());
        player.getCamera().setMapHeight(level.getHeight());
    }

    @Override
    public SceneUpdate update(long currentTime) {
        if (Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            nextScene = NextScene.PAUSE_MENU;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_F12)) {
            player.takeDamage(1000000000);
        }

        if (nextScene != NextScene.NONE) {
            return SceneUpdate.NEXT_SCENE;
        }

        if (firstUpdate) {
            var itemFactories = Assets.getInstance().getItemFactories();
            player.addActiveItem(itemFactories.get(0).makeItem());
            player.addActiveItem(itemFactories.get(1).makeItem());
            player.addActiveItem(itemFactories.get(2).makeItem());
            firstUpdate = false;
        }

        player.update(level, currentTime);

        for (var enemy : enemies) {
            enemy.update(level, currentTime);

            if (enemy.collidesWith(player)) {
                player.takeDamage(enemy.getAttackDamage());
            }
        }

        if (player.isDead()) {
            System.out.println("Player died");
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

            case NONE:
                return null;
        }

        return null;
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
        DatabaseManager.getInstance().addNewScoreEntry(level.getId(), date.toString(), score);
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
