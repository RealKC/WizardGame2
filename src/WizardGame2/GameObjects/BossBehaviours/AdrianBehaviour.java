package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.GameObjects.Bullet;
import WizardGame2.Level;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@SuppressWarnings("unused")
public class AdrianBehaviour implements Boss.Behaviour {
    private Boss boss;

    private long lastShotAt = 0;

    private static final int COOLDOWN = 1_000_000_000; // seconds

    private static final Color BULLET_COLOR = new Color(255, 0, 0, 255);

    private static final double[] ANGLES = new double[] { 0.0, Math.PI / 2.0, Math.PI, 1.5 * Math.PI - Math.PI / 4.0, 1.5 * Math.PI + Math.PI / 4.0};

    final Random random = new Random();

    @Override
    public void attachTo(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY) {
        gfx.drawImage(sprite, boss.getX() - centerX, boss.getY() - centerY, null);
    }

    @Override
    public void update(Level level, long currentTime, int playerX, int playerY) {
        boss.moveToTarget(level, playerX, playerY);

        if (currentTime - lastShotAt > COOLDOWN) {
            lastShotAt = currentTime;
            System.out.println("Last shot @ " + lastShotAt);

            for (double angle : ANGLES) {
                int x = boss.getX() + boss.getHitboxWidth() / 2 + 128 * (int) Math.cos(angle);
                int y = boss.getY() + boss.getHitboxHeight() / 2 + 128 * (int) Math.sin(angle);

                LevelScene.getInstance().getBullets().add(new Bullet(BULLET_COLOR, x, y, 16, 16,
                        Bullet.MovementType.RADIAL, 7, angle, 5));
            }
        }
    }
}
