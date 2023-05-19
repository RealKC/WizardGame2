package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;
import WizardGame2.OSTManager;
import WizardGame2.Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

public class Boss extends Enemy {
    public interface Behaviour {
        void attachTo(Boss boss);

        void render(Graphics gfx, BufferedImage sprite, int centerX, int centerY);

        void update(Level level, long currentTime, int playerX, int playerY);

        default boolean shouldIgnoreCollision() {
            return false;
        }
    }

    static final int SIZE = 64;

    private final Behaviour behaviour;

    private final boolean isFinalBoss;

    Boss(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight, double health, int score, int attackDamage, Behaviour behaviour, boolean finalBoss) {
        super(sprite, x, y, hitboxWidth, hitboxHeight, health, score, attackDamage, false);

        this.isFinalBoss = finalBoss;
        this.behaviour = behaviour;
        behaviour.attachTo(this);

        OSTManager.getInstance().playBossMusic();
    }

    public boolean isFinalBoss() {
        return isFinalBoss;
    }

    @Override
    public boolean collidesWith(GameObject other) {
        if (behaviour.shouldIgnoreCollision()) {
            return false;
        }

        return super.collidesWith(other);
    }

    @Override
    public Died takeDamage(double amount) {
        Died died = super.takeDamage(amount);

        if (died == Died.YES) {
            OSTManager.getInstance().stopMusic();
        }

        return died;
    }

    @Override
    public void render(Graphics gfx, int centerX, int centerY) {
        behaviour.render(gfx, getSprite(), centerX, centerY);
    }

    @Override
    public void update(Level level, long currentTime) {
        behaviour.update(level, currentTime, playerX, playerY);
    }
}
