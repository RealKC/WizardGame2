package WizardGame2.Items;

import WizardGame2.Game;
import WizardGame2.GameObjects.Bullet;

import java.awt.image.BufferedImage;

public class PistolCarpatiItem extends Item {
    public PistolCarpatiItem(String name, int id, BufferedImage sprite, int attackSpeed) {
        super(name, id, sprite, attackSpeed);
    }

    @Override
    void update(long currentTime) {
        if (!hasCooldownPassed()) {
            return;
        }

        System.out.printf("%s activated\n", getName());
        Game.getInstance().getBullets().add(new Bullet(null, 400, 300, 30, 30, Bullet.MovementType.RADIAL, 3, 0));
    }
}
