package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class TerraGuttaItem extends Item {
    private final double attackDamage;

    final Random random = new Random();

    private static final Color COLOR = new Color(131, 71, 14, 255);

    public TerraGuttaItem(String name, int id, BufferedImage sprite, int attackSpeed, double attackDamage) {
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

        var radius = 130 + random.nextInt(50);
        var angle = Math.PI * random.nextFloat();

        System.out.printf("TerraGutta: %g, %g\n", radius * Math.cos(angle), radius * Math.sin(angle));

        int x = playerX + (int) (radius * Math.cos(angle));
        int y = playerY + (int) (radius * Math.sin(angle)) - 300;

        levelScene.getBullets().add(new Bullet(COLOR, x, y, 30, 30,
                Bullet.MovementType.RADIAL, 5, 0.5 * Math.PI, stats.applyAttackModifiers(attackDamage)));
    }
}
