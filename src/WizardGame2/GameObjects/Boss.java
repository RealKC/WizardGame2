package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;
import WizardGame2.Utils;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

public class Boss extends Enemy {
    public interface Behaviour {
        void attachTo(Boss boss);

        void update(Level level, long currentTime);
    }

    private final Behaviour behaviour;

    public static Boss fromData(SpriteSheet spriteSheet, Enemy.Data data, int x, int y) {
        assert data.behaviour != null;

        Behaviour behaviour = null;
        try {
            var behaviourClass = Class.forName(data.behaviour);
            behaviour = (Behaviour) behaviourClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            Utils.logException(Boss.class, e, "failed to load '%s' behaviour class", data.behaviour);
        } catch (NoSuchMethodException e) {
            Utils.logException(Boss.class, e, "'%s' does not have a no-parameter constructor", data.behaviour);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Utils.logException(Boss.class, e, "failed to invoke constructor");
        }
        assert behaviour != null;

        final int size = 64;

        var sprite = Utils.scale(spriteSheet.crop(data.x, data.y), size);

        return new Boss(sprite, x, y, size, size, data.health, data.score, data.damage, behaviour);
    }

    private Boss(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight, double health, int score, int attackDamage, Behaviour behaviour) {
        super(sprite, x, y, hitboxWidth, hitboxHeight, health, score, attackDamage);
        this.behaviour = behaviour;
        behaviour.attachTo(this);
    }

    @Override
    public void update(Level level, long currentTime) {
        behaviour.update(level, currentTime);
    }
}
