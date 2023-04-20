package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;

import java.awt.image.BufferedImage;

/**
 * An obstacle is a game object that does nothing but stop the player from advacing in a certain direction
 */
public class Obstacle extends GameObject {
    /**
     * Class used to represent an Obstacle in the JSON file representing a level
     */
    @SuppressWarnings("unused") // IDEA can't figure out GSON initialises the fields for us
    public static class Data {
        /**
         * These are the coordinates within the spritesheet of this obstacle's sprite
         */
        private int x, y;

        public Data() {}
    }

    public static Obstacle fromData(SpriteSheet spriteSheet, Data data, int posX, int posY) {
        // for now we assume all obstacles are 32x32
        return new Obstacle(spriteSheet.crop(data.x, data.y), posX, posY, 32, 32);
    }


    public Obstacle(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
    }

    @Override
    public void update(Level level, long currentTime) {
        // Intentionally left as a noop
    }
}
