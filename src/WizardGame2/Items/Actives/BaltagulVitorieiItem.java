package WizardGame2.Items.Actives;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BaltagulVitorieiItem extends Item {
    private final double attackDamage;

    private static final Color COLOR = new Color(204, 225, 217, 255);

    public BaltagulVitorieiItem(String name, int id, BufferedImage sprite, int attackSpeed, double attackDamage) {
        super(name, id, sprite, attackSpeed);
        this.attackDamage = attackDamage;
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        LevelScene.getInstance().getBullets().add(new Bullet(COLOR, playerX, playerY, 15, 15,
                Bullet.MovementType.SPIRAL, 2, 0.0, stats.applyAttackModifiers(attackDamage)));
    }
}
