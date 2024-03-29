package WizardGame2;

import WizardGame2.Exceptions.EnemyException;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.GameObjects.Obstacle;
import WizardGame2.Graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the data that makes up a level, but no behaviour associated with levels
 */
public class LevelData {
    private String name;
    private int id;
    private int nextLevel;
    private String unlocks;
    private BufferedImage texture;

    private Map<Point, Obstacle.Data> obstacles;

    private Enemy.Data[] enemies;
    private Map<Integer, Enemy.Data> minibosses;
    private Enemy.Data[] bosses;
    private Map<Integer, Wave> waves;
    private Integer[] waveNumbers;

    private int bossLevel = 0;

    public int getId() {
        return id;
    }

    public int getNextLevel() {
        return nextLevel;
    }

    public static class Wave {
        EnemyDistribution distribution;
        String[] enemies;

        public Wave() {}
    }


    // Those warnings are false positives, IDEA just can't figure out that class instances are initialised by GSON
    @SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
    public static class Raw {
        private String name;
        private int id;
        private int nextLevel;
        private String unlocks;
        private Map<Character, String> tiles;
        private String pattern;

        private int tileWidth, tileHeight, tileColumns, tileRows;

        private Map<String, Obstacle.Data> obstacles;
        private Map<String, String> obstaclePositions;

        private Enemy.Data[] enemies;
        private Map<String, String> minibosses;
        private Enemy.Data[] bosses;

        private Map<Integer, Wave> waves;

        public Raw() {
        }

        public Map<Character, BufferedImage> getTiles() {
            var tiles = new java.util.HashMap<Character, BufferedImage>();

            this.tiles.forEach((key, path) -> tiles.put(key, ImageLoader.loadImage(path)));

            return tiles;
        }
    }

    public static LevelData fromRaw(Raw rawMapData) {
        var mapData = new LevelData();

        mapData.name = rawMapData.name;
        mapData.id = rawMapData.id;
        mapData.enemies = rawMapData.enemies;
        mapData.bosses = rawMapData.bosses;
        mapData.waves = rawMapData.waves;
        mapData.waveNumbers = new Integer[rawMapData.waves.size()];
        mapData.unlocks = rawMapData.unlocks;
        mapData.nextLevel = rawMapData.nextLevel;
        rawMapData.waves.keySet().toArray(mapData.waveNumbers);

        mapData.minibosses = new HashMap<>();

        for (var entry : rawMapData.minibosses.entrySet()) {
            Enemy.Data data = null;

            for (var enemy : mapData.enemies) {
                if (enemy.getName().equals(entry.getValue())) {
                    data = enemy;
                }
            }

            mapData.minibosses.put(Integer.parseInt(entry.getKey()), data);
        }

        int textureWidth = rawMapData.tileWidth * rawMapData.tileColumns;
        int textureHeight = rawMapData.tileHeight * rawMapData.tileRows;

        var tiles = rawMapData.getTiles();

        mapData.texture = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D gfx = mapData.texture.createGraphics();

        Map<Point, Obstacle.Data> deserializedObstacles = deserializeObstacles(rawMapData);
        HashMap<Point, Obstacle.Data> placedObstacles = new HashMap<>();

        int verticalOffset = 0;
        String[] patternLines = rawMapData.pattern.split("\n");
        for (String line: patternLines) {
            int horizontalOffset = 0;

            for (int i = 0; i < line.length(); ++i) {
                for (var entry : deserializedObstacles.entrySet()) {
                    // It's a "floating position" because it's relative to the start of the map tile, but that
                    // information is not encoded in its value, so a obstacle with a floating position of (32, 64) is
                    // at that position *within* the map tile
                    Point floatingPosition = entry.getKey();
                    // However, position is absolute, or relative to the origin of the entire map
                    var position = new Point(floatingPosition.x + horizontalOffset, floatingPosition.y + verticalOffset);
                    placedObstacles.put(position, entry.getValue());
                }

                var tile = tiles.get(line.charAt(i));
                gfx.drawImage(tile, horizontalOffset, verticalOffset, null);
                horizontalOffset += rawMapData.tileWidth;
            }

            verticalOffset += rawMapData.tileHeight;
        }

        mapData.obstacles = placedObstacles;

        return mapData;
    }

    private static Map<Point, Obstacle.Data> deserializeObstacles(Raw rawMapData) {
        var deserializedObstacles = new HashMap<Point, Obstacle.Data>();

        rawMapData.obstaclePositions.forEach((rawPosition, type) -> {
            String[] positionComponents = rawPosition.split("x");
            var position = new Point(
                    Integer.parseInt(positionComponents[0]),
                    Integer.parseInt(positionComponents[1])
            );

            Obstacle.Data data = rawMapData.obstacles.get(type);

            deserializedObstacles.put(position, data);
        });

        return deserializedObstacles;
    }

    private LevelData() {}

    BufferedImage getTexture() {
        return texture;
    }

    public Map<Point, Obstacle.Data> getObstacles() {
        return obstacles;
    }

    public String getUnlocks() {
        return unlocks;
    }

    public Enemy.Data pickRandomEnemy(int waveNr) throws EnemyException {
        Wave wave = waves.get(waveNr);
        String picked = wave.distribution.pickEnemy(wave.enemies);

        for (Enemy.Data enemy : enemies) {
            if (picked.equals(enemy.getName())) {
                return enemy;
            }
        }

        throw new EnemyException("Invalid enemies definition in the JSON file");
    }

    public Enemy.Data pickBoss(int seconds) {
        final int halfTime = 7 * 60;
        final int endTime = 14 * 60;

        if (seconds < halfTime) {
            return null;
        }

        if (seconds < endTime && bossLevel == 0) {
            bossLevel = 1;
            return bosses[0];
        }

        if (bossLevel == 1 && endTime <= seconds) {
            bossLevel = 2;
            return bosses[1];
        }

        return null;
    }

    public void reset() {
        bossLevel = 0;
    }

    public int minibossIndexForTime(int seconds) {
        int prev = 0;

        for (var key : minibosses.keySet().stream().sorted().toList()) {
            if (seconds < key) {
                return prev;
            }
            prev = key;
        }

        return prev;
    }

    public Enemy.Data getMiniboss(int idx) {
        return minibosses.get(idx);
    }

    public int waveNumberForTime(int seconds) {
        for (int i = 0; i < waveNumbers.length - 1; ++i) {
            if (waveNumbers[i] <= seconds && seconds < waveNumbers[i + 1]) {
                return waveNumbers[i];
            }
        }

        return waveNumbers[waveNumbers.length - 1];
    }

    public String getName() {
        return name;
    }
}
