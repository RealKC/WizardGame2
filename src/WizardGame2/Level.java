package WizardGame2;

import WizardGame2.Exceptions.EnemyException;
import WizardGame2.GameObjects.Boss;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.Obstacle;
import WizardGame2.GameObjects.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * This class implements the behaviour associated with a level
 */
public class Level implements Player.PositionObserver {
    private final ArrayList<Obstacle> obstacles;

    private final BufferedImage texture;
    private final LevelData data;

    Boss currentBoss = null;

    int playerX, playerY;

    boolean forcefulVictory = false;

    private final Random random = new Random();

    private final HashSet<Integer> spawnedMinibosses = new HashSet<>();

    private int minimumEnemyCount = 2;

    public static Level fromData(LevelData levelData) {
        var obstacles = new ArrayList<Obstacle>();

        levelData.getObstacles().forEach((position, data) -> {
            obstacles.add(Obstacle.fromData(Assets.getInstance().getObstacles(), data, position.x, position.y));
        });

        // Add world borders
        obstacles.add(new Obstacle(null, -2, 0, 2, levelData.getTexture().getHeight())); // left border
        obstacles.add(new Obstacle(null, levelData.getTexture().getWidth() + 2, 0, 2, levelData.getTexture().getHeight())); // right border
        obstacles.add(new Obstacle(null, -2, 0, levelData.getTexture().getWidth(), 2)); // top border
        obstacles.add(new Obstacle(null, 0, levelData.getTexture().getHeight(), levelData.getTexture().getWidth() , 2)); // bottom order
        return new Level(obstacles, levelData);
    }

    public Level(ArrayList<Obstacle> obstacles, LevelData levelData) {
        this.obstacles = obstacles;
        this.texture = levelData.getTexture();
        this.data = levelData;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getWidth() {
        return texture.getWidth();
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public String getName() {
        return data.getName();
    }

    public boolean hasBeenWon() {
        return (currentBoss != null && currentBoss.isFinalBoss() && currentBoss.isDead()) || (Main.IS_DEBUG_BUILD && forcefulVictory);
    }

    public int getId() {
        return data.getId();
    }

    public void render(Graphics gfx, Player.Camera camera) {
        int x = Utils.clamp(0, texture.getWidth() - camera.getCameraWidth(), camera.getX());
        int y = Utils.clamp(0, texture.getHeight() - camera.getCameraHeight(), camera.getY());
        var visibleMap = texture.getSubimage(x, y, camera.getCameraWidth(), camera.getCameraHeight());
        gfx.drawImage(visibleMap, 0, 0, null);

        Color oldColor = gfx.getColor();
        gfx.setColor(Color.RED);

        for (var obstacle : obstacles) {
            obstacle.render(gfx, camera.getX(), camera.getY());
        }

        gfx.setColor(oldColor);
    }

    public ArrayList<Enemy> maybeSpawn(int seconds) {
        if (seconds % 5 != 0) {
            return null;
        }

        ArrayList<Enemy> enemies = new ArrayList<>();

        int count = minimumEnemyCount + random.nextInt(3);
        minimumEnemyCount += (seconds % 15 == 0) ? 1 : 0;

        for (int i = 0; i < count; ++i) {
            try {
                int radius = 155 + random.nextInt(10);
                double angle = random.nextDouble() * Math.PI;

                int x = playerX + (int) (radius * Math.cos(angle));
                int y = playerY + (int) (radius * Math.sin(angle));

                Enemy.Data enemyData = data.pickRandomEnemy(data.waveNumberForTime(seconds));

                enemies.add(Enemy.newBuilder(Assets.getInstance().getCharacters(), enemyData)
                        .atCoordinates(x, y)
                        .build());
            } catch (EnemyException e) {
                Utils.logException(this.getClass(), e, "got exception trying to spawn the #%dth enemy", i);
            }
        }

        if (currentBoss == null || currentBoss.isDead()){
            var boss = spawnBoss(seconds);
            if (boss != null) {
                currentBoss = boss;
                enemies.add(boss);
            }
        }

        int minibossIndex = data.minibossIndexForTime(seconds);
        if (!spawnedMinibosses.contains(minibossIndex)) {
            int radius = 155 + random.nextInt(10);
            double angle = random.nextDouble() * Math.PI;

            int x = playerX + (int) (radius * Math.cos(angle));
            int y = playerY + (int) (radius * Math.sin(angle));

            spawnedMinibosses.add(minibossIndex);


            var miniboss =  data.getMiniboss(minibossIndex);
            if (miniboss != null) {
                enemies.add(Enemy.newBuilder(Assets.getInstance().getCharacters(), miniboss)
                        .isMiniBoss(true)
                        .atCoordinates(x, y)
                        .build());
            }
        }

        return enemies;
    }

    public Boss spawnBoss(int seconds) {
        var bossData = data.pickBoss(seconds);

        if (bossData == null) {
            return null;
        }

        var boss = (Boss) Enemy.newBuilder(Assets.getInstance().getCharacters(), bossData)
                .atCoordinates(playerX, playerY - 200)
                .isBoss(true)
                .build();

        currentBoss = boss;

        return boss;
    }

    public LevelData getData() {
        return data;
    }

    @Override
    public void notifyAboutNewPosition(int x, int y, double movementAngle) {
        playerX = x;
        playerY = y;
    }

    public void setHasBeenWon(boolean b) {
        forcefulVictory = b;
    }
}
