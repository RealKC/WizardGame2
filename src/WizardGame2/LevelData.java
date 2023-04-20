package WizardGame2;

import WizardGame2.GameObjects.Obstacle;
import WizardGame2.Graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class LevelData {
    private String name;
    private BufferedImage texture;

    private Map<Point, Obstacle.Data> obstacles;

    // Those warnings are false positives, IDEA just can't figure out that class instances are initialised by GSON
    @SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
    public static class Raw {
        private String name;
        private String[] enemies;
        private Map<Character, String> tiles;
        private String pattern;

        private int tileWidth, tileHeight, tileColumns, tileRows;

        private Map<String, Obstacle.Data> obstacles;
        private Map<String, String> obstaclePositions;

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
}
