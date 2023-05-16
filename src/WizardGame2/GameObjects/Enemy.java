package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;

import java.awt.image.BufferedImage;

public class Enemy extends LivingGameObject implements Player.PositionObserver {
    protected int playerX, playerY;

    private final int scoreValue;

    private final int attackDamage;

    private final boolean isFlying;

    @SuppressWarnings("unused")
    public static class Data {
        String name;
        double health;
        /**
         * Coordinates within the character spritesheet
         */
        int x, y;

        int score;

        int damage;

        /**
         * Represents the behaviour of a boss enemy, if nonnull
         */
        String behaviour;

        boolean isFlying;

        boolean finalBoss;

        public Data() {}

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "name='" + name + '\'' +
                    ", health=" + health +
                    ", x=" + x +
                    ", y=" + y +
                    ", score = " + score +
                    ", damage = " + damage +
                    ", behaviour = " + behaviour +
                    ", isFlying = " + isFlying +
                    ", finalBoss = " + finalBoss +
                    '}';
        }
    }

    public static Enemy fromData(SpriteSheet spriteSheet, Data data, int x, int y) {
        return new Enemy(spriteSheet.crop(data.x, data.y), x, y, 32, 32, data.health, data.score, data.damage, data.isFlying);
    }

    public Enemy(BufferedImage sprite, int x, int y, int hitboxWidth, int hitboxHeight, double health, int score, int attackDamage, boolean isFlying) {
        super(sprite, x, y, hitboxWidth, hitboxHeight, health);
        this.scoreValue = score;
        this.attackDamage = attackDamage;
        this.isFlying = isFlying;
    }

    public int getScoreValue() {
        return scoreValue;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    @Override
    public void update(Level level, long currentTime) {
        moveToTarget(level, playerX, playerY);
    }

    @Override
    public boolean collidesWith(GameObject other) {
        if (isFlying && !(other instanceof Player)) {
            return false;
        }

        return super.collidesWith(other);
    }

    public void moveToTarget(Level level, int targetX, int targetY) {
        int xfactor = 0;
        int yfactor = 0;

        if (targetX < this.getX() + 10) {
            xfactor = -1;
        } else if (targetX > this.getX() + 10) {
            xfactor = 1;
        }

        if (targetY < this.getY() + 10) {
            yfactor = -1;
        } else if (targetY > this.getY() + 10) {
            yfactor = 1;
        }

        int step = 2;

        int deltaX = xfactor * step;
        int deltaY = yfactor * step;

        moveBy(deltaX, deltaY);

        for (Obstacle obstacle : level.getObstacles()) {
            if (this.collidesWith(obstacle)) {
                Direction collisionDirection = this.detectCollisionDirection(obstacle);

                int xf = collisionDirection.hasHorizontalCollision() ? -1 : 0;
                int yf = collisionDirection.hasVerticalCollision() ? -1 : 0;

                moveBy(xf * deltaX, yf * deltaY);
                break;
            }
        }
    }

    @Override
    public void notifyAboutNewPosition(int x, int y, double movementAngle) {
        playerX = x;
        playerY = y;
    }

    @Override
    public boolean canBeRemoved() {
        // We stop caring about the player's position after death
        return isDead();
    }
}
