package WizardGame2.GameObjects.BossBehaviours;

import WizardGame2.GameObjects.Boss;
import WizardGame2.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class DenisBehaviour implements Boss.Behaviour {
    Boss boss;

    @Override
    public void attachTo(Boss boss) {
        this.boss = boss;
    }

    @Override
    public void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY) {
        gfx.drawImage(sprite, boss.getX() - centerX, boss.getY(), null);
    }

    @Override
    public void update(Level level, long currentTime, int playerX, int playerY) {

    }
}
