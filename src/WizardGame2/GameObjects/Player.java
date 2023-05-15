package WizardGame2.GameObjects;

import WizardGame2.Assets;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.Abilities.FireMagic;
import WizardGame2.Items.Inventory;
import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;
import WizardGame2.Keyboard;
import WizardGame2.Level;
import WizardGame2.Scenes.LevelScene;
import WizardGame2.Utils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * This class implements the player character of the game
 */
public class Player extends LivingGameObject {
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

        /**
         * Indicates whether this {@link PositionObserver} is safe to remove from the observer list.
         * This may be the result of e.g. an enemy dying.
         */
        default boolean canBeRemoved() {
            return false;
        }
    }

    /**
     * This interface should be implemented by classes that are interested in player level ups
     */
    public interface LevelUpObserver {
        /**
         * Call whenever the player levels up
         */
        void notifyAboutLevelUp(int level);
    }

    private class LevelManager {
        int baseExperience = 0;
        int currentExperience = 0;

        int level = 1;

        static final int[] experienceSteps = new int[] {25, 50, 100, 250, 300, 350, 400, 500, 700};

        int currentExperienceStep() {
            if (level >= experienceSteps.length) {
                return experienceSteps.length - 1;
            }

            return level;
        }

        void addExperience(int value) {
            var experienceNeededToLevelUp = experienceSteps[currentExperienceStep()];

            currentExperience += value;

            if (currentExperience - baseExperience >= experienceNeededToLevelUp) {
                level++;
                baseExperience = currentExperience;

                if (Player.this.levelUpObserver != null) {
                    Player.this.levelUpObserver.notifyAboutLevelUp(level);
                }
            }
        }
    }

    /**
     * Stats are multipliers applied to various player abilities in order to make the player stronger
     */
    public class Stats {
        private Stats() {}

        private final Random random = new Random();

        private double magicPower = 1;
        private double critChance = 0.1;
        private double speedBoost = 1;
        private int pickupRange = 64;
        private double haste = 1;

        public double getHaste() {
            return haste;
        }

        public double applyAttackModifiers(double attackDamage) {
            if (random.nextDouble() <= critChance) {
                return attackDamage * magicPower * 1.5;
            }

            return attackDamage * magicPower;
        }

        public void increaseMaxHP(double delta) {
            Player.this.increaseMaxHP(delta);
        }

        public void increaseMagicPower(double delta) {
            magicPower += delta;
        }

        public void increaseCritChance(double delta) {
            critChance += delta;
        }

        public void increaseSpeedBost(double delta) {
            speedBoost += delta;
        }

        public void increasePickupRange(int delta) {
            pickupRange += delta;
        }

        public void increaseHaste(double delta) {
            haste += delta;
        }
    }

    Inventory inventory = new Inventory();
    Item ability;

    private final LevelManager levelManager = new LevelManager();

    private LevelUpObserver levelUpObserver = null;

    private final Camera camera;


    private final ArrayList<PositionObserver> positionObservers = new ArrayList<>();
    private long lastCleanupAt = 0;


    private int stoppedCounter = 0;
    private double movementAngle;


    /**
     * i-frames are frames in which the player is invincible. They exist in order to make sure the player does not die
     * too quickly.
     */
    private int currentIFrames = 0;

    private static final int MAX_IFRAMES = 30;

    private static final Font levelFont = new Font(Font.MONOSPACED, Font.PLAIN, 25);

    final Stats stats = new Stats();

    public Player(SpriteSheet spriteSheet, int x, int y) {
        super(spriteSheet.crop(0, 0), x, y, 32, 32, 100.0);

        this.camera = new Camera(x, y);
        this.ability = new FireMagic(Assets.getInstance().getItems());
        this.addPositionObserver(ability);

        notifyPositionObservers();
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean hasItem(ItemFactory itemFactory) {
        return inventory.hasItem(itemFactory);
    }

    @Override
    public void render(Graphics gfx, int centerX, int centerY) {
        super.render(gfx, centerX, centerY);
        inventory.render(gfx);

        // Render the player's healthbar
        var oldColor = gfx.getColor();

        gfx.setColor(Color.BLACK);
        final int yOffset = 5;
        final int height = 4;
        gfx.fillRect(getX() - centerX, getY() - yOffset - centerY, getHitboxWidth(), height);
        gfx.setColor(Color.RED);
        var width = getCurrentHp() / getMaxHp() * getHitboxWidth();
        gfx.fillRect(getX() - centerX, getY() - yOffset - centerY, (int) width, height);
        gfx.setColor(oldColor);

        // Render the player's level indicator
        var oldFont = gfx.getFont();
        gfx.setFont(levelFont);
        Utils.drawTextWithOutline(gfx, "lvl " + levelManager.level, 132, 40);
        gfx.setFont(oldFont);

        // Render an aiming direction indicator
        boolean angleIsRight = (0 <= movementAngle && movementAngle < Math.PI / 2)
                || (0.75 * Math.PI <= movementAngle && movementAngle < Math.PI);
        int factor = angleIsRight ? 1 : 0;

        int spriteCenterX = getX() + getSpriteWidth() / 2;
        int spriteCenterY = getY() + getSpriteHeight() / 2;

        double radius = Math.hypot(getSpriteHeight(), getSpriteWidth());

        double indicatorX = spriteCenterX + radius * Math.cos(movementAngle);
        double indicatorY = spriteCenterY + radius * Math.sin(movementAngle);

        oldColor = gfx.getColor();
        gfx.setColor(Color.ORANGE);
        final int INDICATOR_RADIUS = 5;
        gfx.fillOval((int) (indicatorX) - centerX - factor * INDICATOR_RADIUS, (int) (indicatorY) - centerY - factor * INDICATOR_RADIUS, INDICATOR_RADIUS, INDICATOR_RADIUS);
        gfx.setColor(oldColor);
    }

    private static final int STEP = 3;

    @Override
    public void update(Level level, long currentTime) {
        inventory.update(currentTime, stats);
        ability.update(currentTime, stats);

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

        deltaX = (int) (stats.speedBoost * deltaX);
        deltaY = (int) (stats.speedBoost * deltaY);

        moveBy(deltaX, deltaY);

        boolean moved = true;

        int cameraXf = 1, cameraYf = 1;

        for (Obstacle obstacle : level.getObstacles()) {
            if (this.collidesWith(obstacle)) {
                Direction collisionDirection = this.detectCollisionDirection(obstacle);

                int xf = collisionDirection.hasHorizontalCollision() ? -1 : 0;
                int yf = collisionDirection.hasVerticalCollision() ? -1 : 0;

                cameraXf = collisionDirection.hasHorizontalCollision() ? 0 : 1;
                cameraYf = collisionDirection.hasVerticalCollision() ? 0 : 1;

                moveBy(xf * deltaX, yf * deltaY);
                moved = xf == 0 || yf == 0;
                break;
            }
        }

        pickupAnyXp();

        if (moved) {
            camera.moveBy(cameraXf * deltaX, cameraYf * deltaY);

            double newAngle = Math.atan2(deltaY, deltaX);
            boolean shouldUpdateAngle = true;

            if (Math.abs(newAngle) <= 0.1 && deltaX <= 0) {
                stoppedCounter++;
                if (stoppedCounter <= 10) {
                    stoppedCounter = 0;
                    shouldUpdateAngle = false;
                }
            }

            if (shouldUpdateAngle) {
                movementAngle = newAngle;
            }

            notifyPositionObservers();
        }

        maybeCleanupObservers(currentTime);
    }

    private void pickupAnyXp() {
        var experienceObjects = LevelScene.getInstance().getExperienceObjects();
        int i = 0;

        while (i < experienceObjects.size()) {
            var experienceObject = experienceObjects.get(i);
            if (this.collidesWith(experienceObject) || this.distanceTo(experienceObject) <= stats.pickupRange / 2) {
                levelManager.addExperience(experienceObject.getValue());
                experienceObjects.remove(i);
            } else {
                i++;
            }
        }
    }

    @Override
    public Died takeDamage(double amount) {
        if (currentIFrames <= 0) {
            currentIFrames = MAX_IFRAMES;
            return super.takeDamage(amount);
        }

        currentIFrames--;
        return isDead() ? Died.YES : Died.NO;
    }

    public Stats getStats() {
        return stats;
    }

    public boolean canPickUpMoreActiveItems() {
        return inventory.activeItemCount() < 3;
    }

    public boolean canPickUpMorePassiveItems() {
        return inventory.passiveItemCount() < 3;
    }

    /**
     * Adds a position observer to an internal list of observers
     */
    public void addPositionObserver(PositionObserver positionObserver) {
        positionObservers.add(positionObserver);
    }

    public void addPositionObservers(Collection<? extends PositionObserver> positionObservers) {
        this.positionObservers.addAll(positionObservers);
    }

    public void setLevelUpObserver(LevelUpObserver levelUpObserver) {
        this.levelUpObserver = levelUpObserver;
    }

    public void addActiveItem(Item item) {
        inventory.addActiveItem(item);
        addPositionObserver(item);
    }

    private void notifyPositionObservers() {
        for (var positionObserver : positionObservers) {
            positionObserver.notifyAboutNewPosition(getX(), getY(), movementAngle);
        }
    }

    private void maybeCleanupObservers(long currentTime) {
        if (currentTime - lastCleanupAt < 2500) {
            return;
        }

        lastCleanupAt = currentTime;

        int i = 0;
        while (i < positionObservers.size()) {
            if (positionObservers.get(i).canBeRemoved()) {
                positionObservers.remove(i);
            } else {
                i++;
            }
        }
    }
}
