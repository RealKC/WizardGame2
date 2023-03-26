package WizardGame2.GameObjects;

import WizardGame2.Map;

import java.awt.image.BufferedImage;

public class Obstacle extends GameObject {

    public Obstacle(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
    }

    @Override
    public void update(Map map) {
        // Intentionally left as a noop
    }
}
