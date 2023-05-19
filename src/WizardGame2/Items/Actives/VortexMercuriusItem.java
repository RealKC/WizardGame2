package WizardGame2.Items.Actives;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VortexMercuriusItem extends Item {
    private static final Color COLOR = new Color(114, 215, 56, 255);

    private final double attackDamage;

    private final Random random = new Random();

    VortexMercuriusItem(String name, int id, BufferedImage sprite, int speed, double attackDamage) {
        super(name, id, sprite, speed);

        this.attackDamage = attackDamage;
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        double angle = random.nextDouble(Math.PI * 2);

        LevelScene.getInstance().getBullets().add(new Bullet(COLOR, playerX, playerY, 15, 15,
                Bullet.MovementType.RADIAL, 12, angle, stats.applyAttackModifiers(attackDamage), Bullet.Target.ENEMY));
    }
}
