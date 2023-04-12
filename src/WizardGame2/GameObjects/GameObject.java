package WizardGame2.GameObjects;

import WizardGame2.Map;

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

    public void render(Graphics gfx, int centerX, int centerY) {
        gfx.drawImage(sprite, x - centerX, y - centerY, null);
    }

    public abstract void update(Map map);

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

        if (leftA >= rightB) {
            // A is to the right of B
            return false;
        }

        // The two objects intersect
        return true;
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

    protected int getSpriteWidth() {
        return sprite.getWidth();
    }

    protected int getSpriteHeight() {
        return sprite.getHeight();
    }
}
