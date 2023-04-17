package WizardGame2.GameObjects;

import WizardGame2.Map;

import java.awt.image.BufferedImage;

public class Enemy extends GameObject implements Player.PositionObserver {
    private int playerX, playerY;

    public Enemy(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
    }

    @Override
    public void update(Map map, long currentTime) {
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
    public void notifyAboutNewPosition(int x, int y) {
        playerX = x;
        playerY = y;
    }
}
