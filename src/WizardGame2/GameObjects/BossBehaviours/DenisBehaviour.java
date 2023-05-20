package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class DenisBehaviour extends CommonMovement implements Boss.Behaviour {
    private static final int[] VERTICAL_OFFSETS = new int[]{-150, -75, 0, 75, 150};
    private static final int[] HORIZONTAL_OFFSETS = new int[]{-135, -65, -35, 0, 35, 65, 135};

    enum State {
        SELECTING_TARGET,
        WAITING_FOR_TELEPORT,
    }

    private State currentState = State.SELECTING_TARGET;

    private int teleportTargetX = -1, teleportTargetY = -1;

    private static final int COOLDOWN = 300;
    private int cooldownUntilTargetSelection = 0;
    private int cooldownUntilTeleport = 0;

    public DenisBehaviour() {
        super(VERTICAL_OFFSETS, HORIZONTAL_OFFSETS);
    }

    @Override
    public void attachTo(Boss boss) {
        super.setBoss(boss);
    }

    @Override
    public void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY) {
        gfx.drawImage(sprite, getBoss().getX() - centerX, getBoss().getY() - centerY, null);

        if (currentState == State.WAITING_FOR_TELEPORT) {
            var oldColor = gfx.getColor();
            gfx.setColor(Color.RED);

            gfx.drawLine(teleportTargetX - 16 - centerX, teleportTargetY - 16 - centerY, teleportTargetX + 16 - centerX, teleportTargetY + 16 - centerY);
            gfx.drawLine(teleportTargetX - 16 - centerX, teleportTargetY + 16 - centerY, teleportTargetX + 16 - centerX, teleportTargetY - 16 - centerY);

            gfx.setColor(oldColor);
        }
    }

    @Override
    public void update(Level level, long currentTime, int playerX, int playerY) {
        super.performMovement(level, playerX, playerY);

        switch (currentState) {
            case SELECTING_TARGET -> {
                System.out.println("selecting target");
                cooldownUntilTargetSelection--;
                if (cooldownUntilTargetSelection <= 0 || teleportTargetX == -1 || teleportTargetY == -1) {
                    final int distance = 256;

                    cooldownUntilTargetSelection = COOLDOWN;

                    var angle = random.nextDouble(0.0, 2 * Math.PI);
                    teleportTargetX = playerX + (int) (distance * Math.cos(angle));
                    teleportTargetY = playerY + (int) (distance * Math.sin(angle));
                    cooldownUntilTeleport = COOLDOWN / 2;
                    currentState = State.WAITING_FOR_TELEPORT;
                }
            }
            case WAITING_FOR_TELEPORT -> {
                cooldownUntilTeleport--;
                System.out.println("waiting to teleport");
                if (cooldownUntilTeleport <= 0) {
                    var boss = getBoss();
                    boss.moveTo(teleportTargetX, teleportTargetY);
                    currentState = State.SELECTING_TARGET;
                }
            }
        }
    }
}
