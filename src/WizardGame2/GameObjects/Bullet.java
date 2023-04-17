package WizardGame2.GameObjects;

import WizardGame2.Map;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Bullet extends GameObject {
    public enum MovementType {
        RADIAL,
        SPIRAL,
    }

    private final MovementType movementType;
    private final double speed;
    private double distance = 0;
    private final double angle;

    private final int originX, originY;

    public Bullet(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight,
                  MovementType movementType, double speed, double angle) {
        super(sprite, x, y, hitboxWidth, hitboxHeight);
        originX = x;
        originY = y;
        this.movementType = movementType;
        this.speed = speed;
        this.angle = angle;
    }


    @Override
    public void render(Graphics gfx, int centerX, int centerY) {
        // FIXME: Stop doing this!
        gfx.fillOval(getX() - centerX, getY() - centerY, getHitboxWidth(), getHitboxHeight());
    }

    @Override
    public void update(Map map, long currentTime) {
        switch (movementType) {
            case RADIAL -> {
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
}
