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

    public enum Target {
        PLAYER,
        ENEMY,
    }

    private final MovementType movementType;
    private final Target target;
    private final double speed;
    private double distance = 0;
    private double angle;

    private final double attackDamage;

    private final int originX, originY;

    private int enemiesToHitUntilRemoval = 1;

    protected final Color color;

    public Bullet(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight,
                  MovementType movementType, double speed, double angle, double attackDamage, Target target) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
        this.target = target;
        this.originX = x;
        this.originY = y;
        this.movementType = movementType;
        this.speed = speed;
        this.angle = angle;
        this.attackDamage = attackDamage;
        this.color = null;
    }

    public Bullet(Color color, int x, int y, int hitboxWidth, int hitboxHeight,
                  MovementType movementType, double speed, double angle, double attackDamage, Target target) {
        super(null, x, y, hitboxWidth, hitboxHeight);
        this.target = target;
        this.originX = x;
        this.originY = y;
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
                // Based on the code for the RADIAL case, but slightly modified
                var newDistance = speed + distance;
                var newAngle = angle + Math.toRadians(6.0);
                var newX = originX + newDistance * cos(newAngle);
                var newY = originY + newDistance * sin(newAngle);

                distance  = newDistance;
                angle = newAngle;

                moveTo((int) newX, (int) newY);
            }
        }
    }

    public Target getTarget() {
        return target;
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
