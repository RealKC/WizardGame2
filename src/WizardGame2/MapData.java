package WizardGame2;

import WizardGame2.Graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapData {
    private String name;
    private BufferedImage texture;

    public static class Raw {
        private String name;
        private String[] enemies;
        private java.util.Map<Character, String> tiles;
        private String pattern;

        private int tileWidth, tileHeight, tileColumns, tileRows;

        public Raw() {
        }

        public java.util.Map<Character, BufferedImage> getTiles() {
            var tiles = new java.util.HashMap<Character, BufferedImage>();

            this.tiles.forEach((key, path) -> tiles.put(key, ImageLoader.loadImage(path)));

            return tiles;
        }
    }

    public static MapData fromRaw(Raw rawMapData) {
        var mapData = new MapData();

        mapData.name = rawMapData.name;

        int textureWidth = rawMapData.tileWidth * rawMapData.tileColumns;
        int textureHeight = rawMapData.tileHeight * rawMapData.tileRows;

        var tiles = rawMapData.getTiles();

        mapData.texture = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D gfx = mapData.texture.createGraphics();

        int verticalOffset = 0;
        String[] patternLines = rawMapData.pattern.split("\n");
        for (String line: patternLines) {
            int horizontalOffset = 0;

            for (int i = 0; i < line.length(); ++i) {
                var tile = tiles.get(line.charAt(i));
                gfx.drawImage(tile, horizontalOffset, verticalOffset, null);
                horizontalOffset += rawMapData.tileWidth;
            }

            verticalOffset += rawMapData.tileHeight;
        }

        return mapData;
    }

    private MapData() {}

    BufferedImage getTexture() {
        return texture;
    }
}
