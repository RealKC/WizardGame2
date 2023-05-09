package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;

import java.awt.image.BufferedImage;

public class IgnisRespiratioItem extends Item {
    class Flame extends Bullet {
        public Flame(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight, MovementType movementType, double speed, double angle, double attackDamage) {
            super(sprite, x, y, hitboxWidth, hitboxHeight, movementType, speed, angle, attackDamage);
        }
    }

    private final Flame flame = null;

    public IgnisRespiratioItem(String name, int id, BufferedImage sprite) {
        super(name, id, sprite, 0);
    }

    @Override
    void update(long currentTime) {

    }
}
