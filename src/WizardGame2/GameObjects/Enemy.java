package WizardGame2.GameObjects;

import WizardGame2.Map;

import java.awt.image.BufferedImage;

public class Enemy extends GameObject {
    Player player;

    public Enemy(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight, Player player) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);

        this.player = player;
    }

    @Override
    public void update(Map map, long currentTime) {
        int xfactor = 0;
        int yfactor = 0;

        if (player.getX() < this.getX() + 10) {
            xfactor = -1;
        } else if (player.getX() > this.getX() + 10) {
            xfactor = 1;
        }

        if (player.getY() < this.getY() + 10) {
            yfactor = -1;
        } else if (player.getY() > this.getY() + 10) {
            yfactor = 1;
        }

        int step = 2;

        moveBy(xfactor * step, yfactor * step);
    }
}
