package WizardGame2.Items.Abilities;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;

public class FireMagic extends Item {
    final double distance = 48;
    double angle = 0.0;


    private static final Color COLOR = new Color(215, 82, 10, 255);

    public FireMagic(SpriteSheet spriteSheet) {
        super("Fire Ball", 900, spriteSheet.crop(0, 0), 7);

        setCooldown(210);
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        int x = playerX + (int) (distance * Math.cos(angle));
        int y = playerY + (int) (distance * Math.sin(angle));

        angle += Math.PI / 6.0;

        LevelScene.getInstance().getBullets().add(new Bullet(COLOR, x, y, 16, 16, Bullet.MovementType.RADIAL, 7, angle, 10));
    }
}
