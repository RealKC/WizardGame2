package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Keyboard;
import WizardGame2.Map;

import java.awt.event.KeyEvent;

public class Player extends GameObject {
    private static final int STEP = 2;

    public static class Camera {
        int x, y, width, height;

        public Camera(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        void moveBy(int deltaX, int deltaY) {
            x += deltaX;
            y += deltaY;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    private final Camera camera;

    public Player(SpriteSheet spriteSheet, int x, int y, Camera camera) {
        super(spriteSheet.crop(0, 0), x, y, 32, 32);
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void update(Map map) {
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
        }
    }
}
