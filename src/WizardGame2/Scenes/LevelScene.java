package WizardGame2.Scenes;

import WizardGame2.*;
import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LevelScene implements Scene {
    private final Level level;

    private final Player player;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    /**
     * The number of seconds that have passed since the start of the level
     */
    private int secondsPassed = 0;

    /**
     * Hack alert!
     */
    boolean firstUpdate = true;

    private boolean isPaused = false;

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

        level = Level.fromData(assets.getLevelDatas().get(0));
        player.addPositionObserver(level);

        player.getCamera().setMapWidth(level.getWidth());
        player.getCamera().setMapHeight(level.getHeight());
    }

    @Override
    public SceneUpdate update(long currentTime) {
        if (Keyboard.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            isPaused = true;
            return SceneUpdate.NEXT_SCENE;
        }

        if (firstUpdate) {
            var itemFactories = Assets.getInstance().getItemFactories();
            player.addActiveItem(itemFactories.get(0).makeItem());
            player.addActiveItem(itemFactories.get(1).makeItem());
            firstUpdate = false;
        }

        player.update(level, currentTime);

        for (var enemy : enemies) {
            enemy.update(level, currentTime);
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

        return  SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        level.render(gfx, player.getCamera());

        for (var bullet : bullets) {
            bullet.render(gfx, player.getCamera().getX(), player.getCamera().getY());
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
        return isPaused;
    }

    public void setPaused(boolean b) {
        isPaused = b;
    }

    @Override
    public Scene nextScene() {
        return new PauseMenuScene(this);
    }

    /**
     * Gets all the bullets in the game
     */
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    private synchronized void tickASecond() {
        if (LevelScene.this.isPaused) {
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
}
