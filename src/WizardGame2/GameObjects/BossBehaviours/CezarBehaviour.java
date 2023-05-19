package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.GameObject;
import WizardGame2.GameObjects.Player;
import WizardGame2.Level;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static WizardGame2.Utils.isClose;

@SuppressWarnings("unused")
public class CezarBehaviour implements Boss.Behaviour {
    private static class IceBullet extends Bullet {
        private static final Color COLOR = new Color(100, 146, 232, 255);

        IceBullet(int x, int y, double angle) {
            super(COLOR, x, y, 20, 20, MovementType.RADIAL, 2, angle, 7, Target.PLAYER);
        }

        boolean shouldBeRemoved = false;

        @Override
        public boolean collidesWith(GameObject other) {
            var hasCollision =  super.collidesWith(other);

            if (hasCollision && other instanceof Player player) {
                shouldBeRemoved = true;

                var stats = player.getStats();

                if (stats.getSpeedBoost() > 0.5) {
                    final var speedBoost = stats.getSpeedBoost();

                    player.addTemporaryStatChange(5_000,
                            stat -> stats.setSpeedBoost(speedBoost / 2),
                            stat -> stats.setSpeedBoost(speedBoost + stats.getSpeedBoost() - speedBoost / 2)
                    );
                }
            }

            return hasCollision;
        }

        @Override
        public boolean shouldBeRemovedAfterThisHit() {
            return shouldBeRemoved;
        }
    }

    enum State {
        FIRES_IN_CIRCLE,
        FIRES_TO_PLAYER,
    }

    State state = State.FIRES_TO_PLAYER;

    private int counter = 0;
    private static final int switchStates = 500;
    private int cooldown = 0;
    private static final int cooldownDecrement = 5;
    private static final int maxCooldown = 300;

    Boss boss;
    private int targetX = -1, targetY = -1;
    private static final int[] OFFSETS = new int[]{-150, -75, -35, 0, 35, 75, 150,};
    private final Random random = new Random();

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
        if (LevelScene.getInstance().getIsPaused()) {
            return;
        }

        if ((targetX == -1 || isClose(targetX, boss.getX())) && playerX != 0) {
            targetX = playerX + OFFSETS[Math.abs(random.nextInt() % OFFSETS.length)];
        }

        if ((targetY == -1 || isClose(targetY, boss.getY())) && playerY != 0) {
            targetY = playerY + OFFSETS[Math.abs(random.nextInt() % OFFSETS.length)];
        }

        boss.moveToTarget(level, targetX, targetY);

        counter++;
        if (counter == switchStates) {
            state = switch (state) {
                case FIRES_IN_CIRCLE -> State.FIRES_TO_PLAYER;
                case FIRES_TO_PLAYER -> State.FIRES_IN_CIRCLE;
            };
            counter = 0;
        }

        cooldown -= cooldownDecrement;
        if (cooldown > 0) {
            return;
        }
        cooldown = maxCooldown;

        var levelScene = LevelScene.getInstance().getBullets();

        switch (state) {
            case FIRES_IN_CIRCLE -> {
                double angle = 0.0;
                final double radius = 64;
                for (int i = 0; i < 10; ++i) {
                    var x = boss.getX() + radius * Math.cos(angle);
                    var y = boss.getY() + radius * Math.sin(angle);
                    levelScene.add(new IceBullet((int) x, (int) y, angle));
                    angle += Math.toRadians(36);
                }
            }
            case FIRES_TO_PLAYER -> {
                double angle;
                if (boss.getX() == 0 || playerX == 0) {
                    angle = Math.toRadians(270);
                } else {
                    angle = Math.PI - Math.atan((boss.getY() - playerY) / (boss.getX() - playerX));
                }

                levelScene.add(new IceBullet(boss.getX(), boss.getY(), angle));
            }
        }
    }
}
