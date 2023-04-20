package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;

import java.awt.image.BufferedImage;

public class Enemy extends GameObject implements Player.PositionObserver {
    private int playerX, playerY;

    @SuppressWarnings("unused")
    public static class Data {
        private String name;
        private double health;
        /**
         * Coordinates within the character spritesheet
         */
        private int x, y;

        public Data() {}

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "name='" + name + '\'' +
                    ", health=" + health +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static Enemy fromData(SpriteSheet spriteSheet, Data data, int x, int y) {
        return new Enemy(spriteSheet.crop(data.x, data.y), x, y, 32, 32);
    }

    public Enemy(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
    }

    @Override
    public void update(Level level, long currentTime) {
        int xfactor = 0;
        int yfactor = 0;

        if (playerX < this.getX() + 10) {
            xfactor = -1;
        } else if (playerX > this.getX() + 10) {
            xfactor = 1;
        }

        if (playerY < this.getY() + 10) {
            yfactor = -1;
        } else if (playerY > this.getY() + 10) {
            yfactor = 1;
        }

        int step = 2;

        moveBy(xfactor * step, yfactor * step);
    }

    @Override
    public void notifyAboutNewPosition(int x, int y, double movementAngle) {
        playerX = x;
        playerY = y;
    }
}
