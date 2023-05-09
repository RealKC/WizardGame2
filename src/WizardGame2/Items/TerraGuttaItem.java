package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.Scenes.LevelScene;

import java.awt.image.BufferedImage;
import java.util.Random;

public class TerraGuttaItem extends Item {
    double attackDamage;

    final Random random = new Random();

    public TerraGuttaItem(String name, int id, BufferedImage sprite, int attackSpeed, double attackDamage) {
        super(name, id, sprite, attackSpeed);
        this.attackDamage = attackDamage;
    }

    @Override
    void update(long currentTime) {
        if (!hasCooldownPassed() || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        var levelScene = LevelScene.getInstance();
        assert levelScene != null;

        var radius = 130 + random.nextInt(50);
        var angle = Math.PI * random.nextFloat();

        System.out.printf("TerraGutta: %g, %g\n", radius * Math.cos(angle), radius * Math.sin(angle));

        int x = playerX + (int) (radius * Math.cos(angle));
        int y = playerY + (int) (radius * Math.sin(angle)) - 300;

        levelScene.getBullets().add(new Bullet(null, x, y, 30, 30,
                Bullet.MovementType.RADIAL, 5, 0.5 * Math.PI, attackDamage));
    }
}
