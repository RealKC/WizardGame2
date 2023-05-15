package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PistolCarpatiItem extends Item {
    private double attackDamage;

    private static final Color COLOR = new Color(0, 0, 0, 255);

    public PistolCarpatiItem(String name, int id, BufferedImage sprite, int attackSpeed, double attackDamage) {
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
        levelScene.getBullets().add(new Bullet(COLOR, playerX, playerY, 30, 30,
                Bullet.MovementType.RADIAL, 12, playerAngle, stats.applyAttackModifiers(attackDamage)));
    }
}
