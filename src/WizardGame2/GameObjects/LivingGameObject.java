package WizardGame2.GameObjects;

import java.awt.image.BufferedImage;

/**
 * A {@link GameObject} that has hit-points and can die
 */
public abstract class LivingGameObject extends GameObject {
    private double hitPoints;
    private double maxHitPoints;

    protected LivingGameObject(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight, double hitPoints) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
        this.hitPoints = hitPoints;
        this.maxHitPoints = hitPoints;
    }

    /**
     * The result of {@link LivingGameObject#takeDamage(double)}, it is a named boolean that tells whether the entity
     * died or not
     */
    public enum Died {
        /**
         * The entity did die
         */
        YES,
        /**
         * The entity did not die
         */
        NO,
    }

    /**
     * Applies damage to the entity
     * @param amount how much damage to take
     * @return whether the entity died or not
     */
    public Died takeDamage(double amount) {
        hitPoints -= amount;

        return hitPoints > 0 ? Died.NO : Died.YES;
    }

    /**
     * Returns whether the entity is dead or not
     */
    public boolean isDead() {
        return hitPoints <= 0;
    }

    protected void increaseMaxHP(double delta) {
        maxHitPoints += delta;
    }

    protected double getCurrentHp() {
        return hitPoints;
    }

    protected double getMaxHp() {
        return maxHitPoints;
    }
}
