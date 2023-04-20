package WizardGame2.Items;

import WizardGame2.Game;
import WizardGame2.GameObjects.Bullet;

import java.awt.image.BufferedImage;

public class PistolCarpatiItem extends Item {
    private double attackDamage;

    public PistolCarpatiItem(String name, int id, BufferedImage sprite, int attackSpeed, double attackDamage) {
        super(name, id, sprite, attackSpeed);
        this.attackDamage = attackDamage;
    }

    @Override
    void update(long currentTime) {
        if (!hasCooldownPassed()) {
            return;
        }

        System.out.printf("%s activated\n", getName());
        Game.getInstance().getBullets().add(new Bullet(null, playerX, playerY, 30, 30,
                Bullet.MovementType.RADIAL, 12, playerAngle, attackDamage));
    }
}
