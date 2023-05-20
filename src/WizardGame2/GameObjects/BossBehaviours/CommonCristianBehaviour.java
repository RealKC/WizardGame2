package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.Assets;
import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Enemy;
import WizardGame2.Level;
import WizardGame2.Scenes.LevelScene;
import WizardGame2.Utils;

import java.awt.*;

public class CommonCristianBehaviour extends CommonMovement {
    private static final int[] VERTICAL_OFFSETS = new int[] { -150, 50, 0, 75, 125 };
    private static final int[] HORIZONTAL_OFFSETS = new int[] { -125, -75, 0, 50, 150 };

    private final boolean isPhase2;

    protected CommonCristianBehaviour(boolean isPhase2) {
        super(VERTICAL_OFFSETS, HORIZONTAL_OFFSETS);

        this.isPhase2 = isPhase2;
    }

    protected void update(Level level, long currentTime, int playerX, int playerY) {
        super.performMovement(level, playerX, playerY);

        if (isPhase2) {
            phase2Update(level, currentTime);
        } else {
            phase1Update(level, currentTime);
        }
    }

    enum State {
        CIRCLE_SHOT,
        SPIRAL_SHOT,
    }

    private State currentState = State.CIRCLE_SHOT;

    private int shootTimer = 0;
    private static final int BASIC_COOLDOWN = 300;

    private static final long SUMMON_COOLDOWN = 10_000_000_000L; // 10 seconds
    private long lastSummonedAt = 0;

    private void phase1Update(Level level, long currentTime) {
        shootTimer--;
        if (shootTimer <= 0) {
            shootTimer = BASIC_COOLDOWN;
            switch (currentState) {
                case CIRCLE_SHOT -> {
                    shootBulletsInACircle();
                    currentState = State.SPIRAL_SHOT;
                }
                case SPIRAL_SHOT -> {
                    shootBulletsInSpiral();
                    currentState = State.CIRCLE_SHOT;
                }
            }
        }

        if (currentTime - lastSummonedAt >= SUMMON_COOLDOWN) {
            lastSummonedAt = currentTime;
            summonEnemies(level);
        }
    }

    Enemy currentMiniBoss = null;
    private int spiralTimer = 0, circleTimer = 0;
    private static final int SPIRAL_COOLDOWN = 450, CIRCLE_COOLDOWN = 350;

    private void phase2Update(Level level, long currentTime) {
        if (currentMiniBoss == null || currentMiniBoss.isDead()) {
            currentMiniBoss = Enemy.newBuilder(Assets.getInstance().getCharacters(), new Enemy.Data(50, 5, 0, 100, 5, false))
                    .atCoordinates(getBoss().getX(), getBoss().getY())
                    .isMiniBoss(true)
                    .build();
            LevelScene.getInstance().addEnemy(currentMiniBoss);
        }

        if (currentTime - lastSummonedAt >= SUMMON_COOLDOWN) {
            lastSummonedAt = currentTime;
            summonEnemies(level);
        }

        circleTimer--;
        if (circleTimer <= 0) {
            shootBulletsInACircle();
            circleTimer = CIRCLE_COOLDOWN;
        }

        spiralTimer--;
        if (spiralTimer <= 0) {
            shootBulletsInSpiral();
            spiralTimer = SPIRAL_COOLDOWN;
        }
    }

    private void summonEnemies(Level level) {
        final double distance = 80;
        final int bulletCount = 6;
        final double angleIncrement = Math.toRadians(360 / (double) bulletCount);

        double rightBorder = level.getWidth() - 16;
        double bottomBorder = level.getHeight() - 16;

        double angle = 0.0;
        for (int i = 0; i < bulletCount; i++) {
            var x = Utils.clamp(16.0, rightBorder, getBoss().getX() + distance * Math.cos(angle));
            var y = Utils.clamp(16.0, bottomBorder, getBoss().getY() + distance * Math.sin(angle));

            var enemy = Enemy.newBuilder(Assets.getInstance().getCharacters(), new Enemy.Data(50, 3, 0, 100, 5, true))
                    .atCoordinates(x.intValue(), y.intValue())
                    .build();

            LevelScene.getInstance().addEnemy(enemy);

            angle += angleIncrement;
        }
    }

    private static final Color BULLET_COLOR1 = new Color(120, 4, 120, 255);

    private void shootBulletsInACircle() {
        final double distance = 64;
        final int bulletCount = 8;
        final double angleIncrement = Math.toRadians(360 / (double) bulletCount);

        double angle = 0.0;
        for (int i = 0; i < bulletCount; i++) {
            var x = getBoss().getX() + distance * Math.cos(angle);
            var y = getBoss().getY() + distance * Math.sin(angle);

            LevelScene.getInstance().getBullets().add(new Bullet(BULLET_COLOR1, (int) x, (int) y, 24, 24, Bullet.MovementType.RADIAL, 2.5, angle, 13, Bullet.Target.PLAYER));

            angle += angleIncrement;
        }
    }

    private static final Color BULLET_COLOR2 = new Color(234, 9, 234, 255);

    private void shootBulletsInSpiral() {
        final double distance = 64;
        final int bulletCount = 3;
        final double angleIncrement = Math.toRadians(360 / (double) bulletCount);

        double angle = 0.0;
        for (int i = 0; i < bulletCount; i++) {
            var x = getBoss().getX() + distance * Math.cos(angle);
            var y = getBoss().getY() + distance * Math.sin(angle);

            LevelScene.getInstance().getBullets().add(new Bullet(BULLET_COLOR2, (int) x, (int) y, 32, 32, Bullet.MovementType.SPIRAL, 2.5, angle, 13, Bullet.Target.PLAYER));

            angle += angleIncrement;
        }
    }
}
