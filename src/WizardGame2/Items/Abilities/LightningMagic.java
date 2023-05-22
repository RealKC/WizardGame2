package WizardGame2.Items.Abilities;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;

@SuppressWarnings("unused")
public class LightningMagic extends Item {
    private static final Color COLOR = new Color(241, 241, 10, 255);

    private double angle = 0.0;

    public LightningMagic(SpriteSheet spriteSheet) {
        super("Carpathian Lightning", IDAllocator.nextId(), spriteSheet.crop(1, 2), 3);
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        final int lightningCount = 16;
        final double off = Math.toRadians(360.0 / lightningCount);
        var angleOffset = angle;
        for (int i = 0; i < lightningCount; ++i) {
            var bullet = new Bullet(COLOR, playerX, playerY, 4, 24, Bullet.MovementType.RADIAL, 7, angleOffset, 10, Bullet.Target.ENEMY);
            bullet.setPierceLimit(10);
            LevelScene.getInstance().getBullets().add(bullet);
            angleOffset += off;
        }

        angle += Math.toRadians(13.0);

    }
}
