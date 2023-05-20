package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class MateiBehaviour extends CommonMovement implements Boss.Behaviour {
    private enum State {
        RUNNING,
        FLYING;

        State toggle() {
            return switch (this) {
                case RUNNING -> FLYING;
                case FLYING -> RUNNING;
            };

        }
    }

    private State currentState = State.RUNNING;

    /**
     * The length in frames of a given state
     */
    private static final int STATE_LENGTH = 15 * 60;

    private int timeUntilSwitch = STATE_LENGTH;

    private static final int[] OFFSETS = new int[]{-150, -75, 0, 75, 150};

    private static final Color SHADOW_COLOR = new Color(46, 15, 52, 200);

    public MateiBehaviour() {
        super(OFFSETS);
    }

    @Override
    public void attachTo(Boss boss) {
        super.setBoss(boss);
    }

    @Override
    public boolean shouldIgnoreCollision() {
        return currentState == State.FLYING;
    }

    @Override
    public void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY) {
        var boss = super.getBoss();

        var oldColor = gfx.getColor();
        gfx.setColor(SHADOW_COLOR);
        gfx.fillOval(boss.getX() - centerX, boss.getY() - centerY + 34, 64, 32);
        gfx.setColor(oldColor);

        if (currentState == State.RUNNING) {
            gfx.drawImage(sprite, boss.getX() - centerX, boss.getY() - centerY, null);
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

        super.performMovement(level, playerX, playerY);
    }
}
