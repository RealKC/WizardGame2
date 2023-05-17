package WizardGame2.GameObjects;

import WizardGame2.Level;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Base class of all GameObjects in WizardGame2
 */
public abstract class GameObject {
    private final BufferedImage sprite;
    private int x, y;
    private final int hitboxWidth, hitboxHeight;

    protected GameObject(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
    }

    /**
     * Renders a {@link GameObject} on the screen. The centerX and centerY parameters are to be used to draw objects at
     * the correct position in reference to the camera.
     * @param gfx the {@link Graphics} object used for drawing
     * @param centerX the X coordinate of the center of the screen
     * @param centerY the Y coordinate of the center of the screen
     */
    public void render(Graphics gfx, int centerX, int centerY) {
        gfx.drawImage(sprite, x - centerX, y - centerY, null);
    }

    public abstract void update(Level level, long currentTime);

    /**
     * Moves the GameObject by the specified amounts
     * @param deltaX how much to move on the X axis
     * @param deltaY how much to move on the Y axis
     */
    public void moveBy(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    /**
     * Moves the GameObject to the point (x, y)
     * @param x the new X coordinate
     * @param y the new Y coordinate
     */
    protected void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Implements AABB collision between two GameObjects
     * @param other another game object
     * @return true if the objects collide, false otherwise
     */
    public boolean collidesWith(GameObject other) {
        // A = this, B = other
        // Based on https://github.com/RealKC/WizardGame/blob/master/src/Levels/Collider.cpp#L20-L57

        int leftA = x;
        int rightA = hitboxWidth + x;
        int bottomA = hitboxHeight + y;
        int topA = y;

        int leftB = other.x;
        int rightB = other.hitboxWidth + other.x;
        int bottomB = other.hitboxHeight + other.y;
        int topB = other.y;

        if (bottomA <= topB) {
            // A is below B
            return false;
        }

        if (topA >= bottomB) {
            // A is above B
            return false;
        }

        if (rightA <= leftB) {
            // A is to the left of B
            return false;
        }

        // IMO the logic is easier to follow when it's written like this
        //noinspection RedundantIfStatement
        if (leftA >= rightB) {
            // A is to the right of B
            return false;
        }

        // The two objects intersect
        return true;
    }

    public enum Direction {
        UP,
        DOWN,
        RIGHT,
        LEFT,
        NONE;

        boolean hasHorizontalCollision() {
            return switch (this) {
                case RIGHT, LEFT -> true;
                default -> false;
            };
        }

        boolean hasVerticalCollision() {
            return switch (this) {
                case UP, DOWN -> true;
                default -> false;
            };
        }
    }

    public Direction detectCollisionDirection(GameObject other) {
        int leftA = x;
        int rightA = hitboxWidth + x;
        int bottomA = hitboxHeight + y;
        int topA = y;

        int leftB = other.x;
        int rightB = other.hitboxWidth + other.x;
        int bottomB = other.hitboxHeight + other.y;
        int topB = other.y;

        if (leftA < rightB && rightA > leftB && topA < bottomB && bottomA > topB) {
            Direction xCollision, yCollision;
            int xDepth, yDepth;

            if (leftA < leftB && rightA < rightB) {
                xCollision = Direction.LEFT;
                xDepth = leftB - rightA;
            } else {
                xCollision = Direction.RIGHT;
                xDepth = leftA - rightB;
            }

            if (bottomA > bottomB && topA > topB) {
                yCollision = Direction.DOWN;
                yDepth = bottomB - topA;
            } else {
                yCollision = Direction.UP;
                yDepth = bottomA - topB;
            }

            if (Math.abs(yDepth) < Math.abs(xDepth)) {
                return yCollision;
            } else {
                return xCollision;
            }
        }

        return Direction.NONE;
    }

    public int distanceTo(GameObject other) {
        return (int) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHitboxWidth() {
        return hitboxWidth;
    }

    public int getHitboxHeight() {
        return hitboxHeight;
    }

    protected BufferedImage getSprite() {
        return sprite;
    }

    protected int getSpriteWidth() {
        return sprite.getWidth();
    }

    protected int getSpriteHeight() {
        return sprite.getHeight();
    }
}
