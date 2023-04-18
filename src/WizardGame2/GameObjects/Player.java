package WizardGame2.GameObjects;

import WizardGame2.Assets;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.Inventory;
import WizardGame2.Keyboard;
import WizardGame2.Map;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * This class implements the player character of the game
 */
public class Player extends GameObject {
    private static final int STEP = 2;

    Inventory inventory = new Inventory();

    /**
     * This class implements a camera that ensures the player character is centered on the screen, except for when it
     * reaches the edges of the map.
     */
    public class Camera {
        private int x, y, cameraWidth, cameraHeight, mapWidth, mapHeight;

        private Camera(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private void moveBy(int deltaX, int deltaY) {
            // Code based on https://lazyfoo.net/tutorials/SDL/30_scrolling/index.php
            // But modified to try and get a camera that stops following at the edges
            int leftEdge = cameraWidth / 2;
            int rightEdge = mapWidth - cameraWidth / 2;
            int topEdge = cameraHeight / 2;
            int bottomEdge = mapHeight - cameraHeight / 2;

            if (leftEdge < Player.this.getX() && Player.this.getX() < rightEdge) {
                x += deltaX;
            }

            if (topEdge < Player.this.getY() && Player.this.getY() < bottomEdge) {
                y += deltaY;
            }
        }

        public int getX() {
            return x + Player.this.getSpriteWidth() / 2 - cameraWidth / 2;
        }

        public int getY() {
            return y + Player.this.getSpriteHeight() / 2 - cameraHeight / 2;
        }

        public int getCameraWidth() {
            return cameraWidth;
        }

        public int getCameraHeight() {
            return cameraHeight;
        }

        public void setCameraHeight(int cameraHeight) {
            this.cameraHeight = cameraHeight;
        }

        public void setCameraWidth(int cameraWidth) {
            this.cameraWidth = cameraWidth;
        }

        public void setMapWidth(int mapWidth) {
            this.mapWidth = mapWidth;
        }

        public void setMapHeight(int mapHeight) {
            this.mapHeight = mapHeight;
        }
    }

    /**
     * This interface should be implemented by classes which are interested in the position of the player character
     */
    public interface PositionObserver {
        /**
         * Called whenever the player character's position changes
         */
        void notifyAboutNewPosition(int x, int y, double movementAngle);
    }

    private final Camera camera;
    private final ArrayList<PositionObserver> positionObservers = new ArrayList<>();

    private double movementAngle;

    public Player(SpriteSheet spriteSheet, int x, int y) {
        super(spriteSheet.crop(0, 0), x, y, 32, 32);
        this.camera = new Camera(x, y);

        var item = Assets.getInstance().getItemFactories().get(0).makeItem();
        positionObservers.add(item);
        inventory.addActiveItem(item);
        notifyPositionObservers();
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void render(Graphics gfx, int centerX, int centerY) {
        super.render(gfx, centerX, centerY);
        inventory.render(gfx);

        // Render an aiming direction indicator

        boolean angleIsRight = (0 <= movementAngle && movementAngle < Math.PI / 2)
                || (0.75 * Math.PI <= movementAngle && movementAngle < Math.PI);
        int factor = angleIsRight ? 1 : 0;

        int spriteCenterX = getX() + getSpriteWidth() / 2;
        int spriteCenterY = getY() + getSpriteHeight() / 2;

        double radius = Math.hypot(getSpriteHeight(), getSpriteWidth());

        double indicatorX = spriteCenterX + radius * Math.cos(movementAngle);
        double indicatorY = spriteCenterY + radius * Math.sin(movementAngle);

        var oldColor = gfx.getColor();
        gfx.setColor(Color.RED);
        final int INDICATOR_RADIUS = 5;
        gfx.fillOval((int) (indicatorX) - centerX - factor * INDICATOR_RADIUS, (int) (indicatorY) - centerY - factor * INDICATOR_RADIUS, INDICATOR_RADIUS, INDICATOR_RADIUS);
        gfx.setColor(oldColor);
    }

    @Override
    public void update(Map map, long currentTime) {
        inventory.update(currentTime);

        int deltaY = 0, deltaX = 0;

        if (Keyboard.isKeyPressed(KeyEvent.VK_S)) {
            deltaY += STEP;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_W)) {
            deltaY -= STEP;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_A)) {
            deltaX -= STEP;
        }

        if (Keyboard.isKeyPressed(KeyEvent.VK_D)) {
            deltaX += STEP;
        }

        moveBy(deltaX, deltaY);

        boolean hadAnyCollisions = false;

        for (Obstacle obstacle : map.getObstacles()) {
            if (this.collidesWith(obstacle)) {
                moveBy(-deltaX, -deltaY);
                hadAnyCollisions = true;
                break;
            }
        }

        if (!hadAnyCollisions) {
            camera.moveBy(deltaX, deltaY);

            movementAngle = Math.atan2(deltaY, deltaX);

            notifyPositionObservers();
        }
    }

    /**
     * Adds a position observer to an internal list of observers
     */
    public void addPositionObserver(PositionObserver positionObserver) {
        positionObservers.add(positionObserver);
    }

    private void notifyPositionObservers() {
        for (var positionObserver : positionObservers) {
            positionObserver.notifyAboutNewPosition(getX(), getY(), movementAngle);
        }
    }
}
