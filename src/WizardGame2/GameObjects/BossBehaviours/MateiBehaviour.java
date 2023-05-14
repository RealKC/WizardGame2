package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;
import static WizardGame2.Utils.isClose;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
public class MateiBehaviour implements Boss.Behaviour {
    Boss boss;

    private enum State {
        RUNNING,
        FLYING;

        State toggle() {
            switch (this) {
                case RUNNING: return FLYING;
                case FLYING: return RUNNING;
            }

            return null;
        }
    }

    private State currentState = State.RUNNING;

    /**
     * The length in frames of a given state
     */
    private static final int STATE_LENGTH = 15 * 60;

    private int timeUntilSwitch = STATE_LENGTH;

    private int targetX = -1, targetY = -1;

    private int spriteX, spriteY;

    private static final int[] OFFSETS = new int[] { -150, -75, 0, 75, 150 };

    private final Random random = new Random();

    private static final Color SHADOW_COLOR = new Color(46, 15, 52, 200);

    @Override
    public void attachTo(Boss boss) {
        this.boss = boss;
    }

    @Override
    public boolean shouldIgnoreCollision() {
        return currentState == State.FLYING;
    }

    @Override
    public void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY) {
        switch (currentState) {
            case RUNNING:
                gfx.drawImage(sprite, boss.getX() - centerX, boss.getY() - centerY, null);
                break;

            case FLYING:
                var oldColor = gfx.getColor();
                gfx.setColor(SHADOW_COLOR);
                gfx.fillOval(boss.getX() - centerX, boss.getY() - centerY, 64, 32);
                gfx.setColor(oldColor);
                break;
        }

    }

    @Override
    public void update(Level level, long currentTime, int playerX, int playerY) {
        timeUntilSwitch--;
        if (timeUntilSwitch <= 0) {
            currentState = currentState.toggle();
            System.out.println("Switched states, now am " + currentState);
            timeUntilSwitch = STATE_LENGTH;
        }

        if ((targetX == -1 || isClose(targetX, boss.getX())) && playerX != 0) {
            targetX = playerX + OFFSETS[Math.abs(random.nextInt() % OFFSETS.length)];
        }

        if ((targetY == -1 || isClose(targetY, boss.getY())) && playerY != 0) {
            targetY = playerY + OFFSETS[Math.abs(random.nextInt() % OFFSETS.length)];
        }

        boss.moveToTarget(level, targetX, targetY);
    }
}
