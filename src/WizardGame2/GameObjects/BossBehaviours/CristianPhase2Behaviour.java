package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class CristianPhase2Behaviour extends CommonCristianBehaviour implements Boss.Behaviour {
    public CristianPhase2Behaviour() {
        super(true);
    }

    @Override
    public void attachTo(Boss boss) {
        super.setBoss(boss);
    }

    @Override
    public void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY) {
        gfx.drawImage(sprite, getBoss().getX() - centerX, getBoss().getY(), null);
    }

    @Override
    public void update(Level level, long currentTime, int playerX, int playerY) {
        super.update(level, currentTime, playerX, playerY);
    }
}
