package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Keyboard;
import WizardGame2.Map;

import java.awt.event.KeyEvent;

public class Player extends GameObject {
    private static final int STEP = 2;

    public Player(SpriteSheet spriteSheet, int x, int y) {
        super(spriteSheet.crop(0, 0), x, y, 32, 32);
    }

    @Override
    public void update(Map map) {
        int deltaY = 0, deltaX = 0;

        if (Keyboard.isKeyPressed(KeyEvent.VK_S)) {
            deltaY += STEP;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_W)) {
            deltaY -= STEP;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_A)) {
            deltaX -= STEP;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_D)) {
            deltaX += STEP;
        }

        moveBy(deltaX, deltaY);

        for (Obstacle obstacle : map.getObstacles()) {
            if (this.collidesWith(obstacle)) {
                moveBy(-deltaX, -deltaY);
                break;
            }
        }
    }
}
