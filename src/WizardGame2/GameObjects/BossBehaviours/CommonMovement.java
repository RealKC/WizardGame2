package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;

import java.util.Random;

import static WizardGame2.Utils.isClose;

public class CommonMovement {
    private final int[] verticalOffsets;
    private final int[] horizontalOffsets;

    protected final Random random = new Random();

    private Boss boss;

    private int targetX = -1, targetY = -1;

    protected CommonMovement(int[] offsets) {
        this(offsets, offsets);
    }

    protected CommonMovement(int[] verticalOffsets, int[] horizontalOffsets) {
        this.verticalOffsets = verticalOffsets;
        this.horizontalOffsets = horizontalOffsets;
    }

    protected void setBoss(Boss boss) {
        this.boss = boss;
    }

    protected Boss getBoss() {
        return boss;
    }

    protected void performMovement(Level level, int playerX, int playerY) {
        if ((targetX == -1 || isClose(targetX, boss.getX())) && playerX != 0) {
            targetX = playerX + horizontalOffsets[random.nextInt(0, horizontalOffsets.length)];
        }

        if ((targetY == -1 || isClose(targetY, boss.getY())) && playerY != 0) {
            targetY = playerY + verticalOffsets[random.nextInt(0, verticalOffsets.length)];
        }

        boss.moveToTarget(level, targetX, targetY);
    }
}
