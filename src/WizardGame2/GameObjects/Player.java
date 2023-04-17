package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Keyboard;
import WizardGame2.Map;

import java.awt.event.KeyEvent;

public class Player extends GameObject {
    private static final int STEP = 2;

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

    private final Camera camera;

    public Player(SpriteSheet spriteSheet, int x, int y) {
        super(spriteSheet.crop(0, 0), x, y, 32, 32);
        this.camera = new Camera(x, y);
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void update(Map map, long currentTime) {
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
