package WizardGame2;

import WizardGame2.Exceptions.EnemyException;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.Obstacle;
import WizardGame2.GameObjects.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class implements the behaviour associated with a level
 */
public class Level implements Player.PositionObserver {
    ArrayList<Obstacle> obstacles;

    BufferedImage texture;
    LevelData data;

    int playerX, playerY;

    private final Random random = new Random();

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

    public String getId() {
        return "level1"; // TODO: Make this configurable
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

        int count = 2 + random.nextInt(3);

        for (int i = 0; i < count; ++i) {
            try {
                int radius = 155 + random.nextInt(10);
                double angle = random.nextDouble() * Math.PI;

                int x = playerX + (int) (radius * Math.cos(angle));
                int y = playerY + (int) (radius * Math.sin(angle));

                Enemy.Data enemyData = data.pickRandomEnemy(data.waveNumberForTime(seconds));

                enemies.add(Enemy.fromData(Assets.getInstance().getCharacters(), enemyData, x, y));
            } catch (EnemyException e) {
                Utils.logException(this.getClass(), e, "got exception trying to spawn the #%dth enemy", i);
            }
        }

        return enemies;
    }

    @Override
    public void notifyAboutNewPosition(int x, int y, double movementAngle) {
        playerX = x;
        playerY = y;
    }
}
