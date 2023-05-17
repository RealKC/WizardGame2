package WizardGame2.GameObjects;

import WizardGame2.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Bullet extends GameObject {
    public enum MovementType {
        NONE,
        RADIAL,
        SPIRAL,
    }

    private final MovementType movementType;
    private final double speed;
    private double distance = 0;
    private final double angle;

    private final double attackDamage;

    private final int originX, originY;

    private int enemiesToHitUntilRemoval = 1;

    private final Color color;

    public Bullet(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight,
                  MovementType movementType, double speed, double angle, double attackDamage) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
        originX = x;
        originY = y;
        this.movementType = movementType;
        this.speed = speed;
        this.angle = angle;
        this.attackDamage = attackDamage;
        this.color = null;
    }

    public Bullet(Color color, int x, int y, int hitboxWidth, int hitboxHeight,
                  MovementType movementType, double speed, double angle, double attackDamage) {
        super(null, x, y, hitboxWidth, hitboxHeight);
        originX = x;
        originY = y;
        this.movementType = movementType;
        this.speed = speed;
        this.angle = angle;
        this.attackDamage = attackDamage;
        this.color = color;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    @Override
    public void render(Graphics gfx, int centerX, int centerY) {
        if (color != null) {
            var oldColor = gfx.getColor();
            gfx.setColor(color);
            gfx.fillOval(getX() - centerX, getY() - centerY, getHitboxWidth(), getHitboxHeight());
            gfx.setColor(oldColor);
        } else {
            super.render(gfx, centerX, centerY);
        }
    }

    @Override
    public void update(Level level, long currentTime) {
        switch (movementType) {
            case NONE -> {
                // Intentional NOOP
            }
            case RADIAL -> {
                // Based on: https://github.com/RealKC/WizardGame/blob/master/src/Levels/Bullet.cpp#L52-L60
                // This code acts as if the bullet is a stationary point on a growing circle, the angle determined by
                // the intersecting lines (originX, originY) -> (x, y) and the X axis never changes, but the distance
                // to the origin of the circle is ever-increasing.
                var newDistance = speed + distance;
                var newX = originX + newDistance * cos(angle);
                var newY = originY + newDistance * sin(angle);
                distance = newDistance;

                moveTo((int) newX, (int) newY);

            }
            case SPIRAL -> {
            }
        }
    }

    public void setPierceLimit(int value) {
        enemiesToHitUntilRemoval = value;
    }

    /**
     * This method returns whether a bullet should be removed or not, and it allows one to
     * implement piercing bullets.
     */
    public boolean shouldBeRemovedAfterThisHit() {
        enemiesToHitUntilRemoval--;
        return enemiesToHitUntilRemoval == 0;
    }
}
