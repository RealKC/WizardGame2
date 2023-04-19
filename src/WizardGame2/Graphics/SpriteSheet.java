package WizardGame2.Graphics;

import java.awt.image.BufferedImage;

/**
 * This class implements a spritesheet where every tile has the same size (but tiles may not necessarily be squares)
 */
public class SpriteSheet {
    private final BufferedImage spriteSheet;
    private final int tileWidth;
    private final int tileHeight;

    /**
     * Creates a spritesheet from an image and the tile size
     * @param buffImg the buffered image to be used as a spritesheet
     * @param tileWidth_ the width of a tile
     * @param tileHeight_ the height of a tile
     */
    public SpriteSheet(BufferedImage buffImg, int tileWidth_, int tileHeight_) {
        spriteSheet = buffImg;
        tileWidth = tileWidth_;
        tileHeight = tileHeight_;
    }

    /**
     * Creates a spritesheet from an image where the tile size is assumed to be 32x32
     * @param buffImg the buffered image to be used as a spritesheet
     */
    public SpriteSheet(BufferedImage buffImg) {
        this(buffImg, 32, 32);
    }

    /**
     * Gets a tile from the spritesheet
     * @param x the x coordinate of the tile in the spritesheet
     * @param y the y coordinate of the tile in the spritesheet
     * @return the tile at those coordinates as a BufferedImage
     */
    public BufferedImage crop(int x, int y) {
        return spriteSheet.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
    }
}
