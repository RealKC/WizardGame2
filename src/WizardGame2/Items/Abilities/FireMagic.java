package WizardGame2.Items.Abilities;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;

public class FireMagic extends Item {
    private static final Color COLOR = new Color(215, 82, 10, 255);

    private double angleOffset = 0.0;

    public FireMagic(SpriteSheet spriteSheet) {
        super("Fire Ball", 900, spriteSheet.crop(0, 0), 7);

        setCooldown(140);
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        var bullet = new Bullet(COLOR, playerX, playerY, 16, 16, Bullet.MovementType.RADIAL, 7, bulletAngle(), 10);
        bullet.setPierceLimit(10);

        LevelScene.getInstance().getBullets().add(bullet);
    }

    private double bulletAngle() {
        double bulletAngle = (angleOffset + playerAngle);

        if (angleOffset == 0.0) {
            angleOffset = Math.PI;
        } else {
            angleOffset = 0.0;
        }

        return bulletAngle;
    }
}
