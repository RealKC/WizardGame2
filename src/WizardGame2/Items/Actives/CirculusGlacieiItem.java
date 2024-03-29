package WizardGame2.Items.Actives;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CirculusGlacieiItem extends Item {
    private final double attackDamage;

    private static final Color COLOR = new Color(22, 214, 224, 255);

    public CirculusGlacieiItem(String name, int id, BufferedImage sprite, int attackSpeed, double attackDamage) {
        super(name, id, sprite, attackSpeed);
        this.attackDamage = attackDamage;
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        var levelScene = LevelScene.getInstance();
        assert levelScene != null;
        double angle = 0.0;

        for (int i = 0; i < 6; i++) {
            levelScene.getBullets().add(new Bullet(COLOR, playerX, playerY, 15, 15,
                    Bullet.MovementType.RADIAL, 12, angle, stats.applyAttackModifiers(attackDamage), Bullet.Target.ENEMY));

            angle += Math.toRadians(60.0);
        }
    }
}
