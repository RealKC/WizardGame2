package WizardGame2.Items.Abilities;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;

@SuppressWarnings("unused")
public class SwordMagic extends Item {
    private static final Color COLOR = Color.white;

    // Cooldown related state
    private int attackStep = 0;
    private int nextCooldown = 0;
    private static final int switchCooldowns = 3;
    private static final int[] cooldowns = new int[] { 100, 600 };


    private void updateCooldown() {
        if (attackStep == switchCooldowns) {
            setCooldown(cooldowns[nextCooldown % 2]);
            attackStep = 0;
            nextCooldown++;
        } else {
            attackStep++;
        }
    }

    public SwordMagic(SpriteSheet spriteSheet) {
        super("Sword Magic", IDAllocator.nextId(), spriteSheet.crop(2, 2), 10);

        updateCooldown();

    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        updateCooldown();

        var bullet = new Bullet(COLOR, playerX, playerY, 16, 16, Bullet.MovementType.RADIAL, 7, playerAngle, 20);
        bullet.setPierceLimit(15);
        LevelScene.getInstance().getBullets().add(bullet);
    }
}