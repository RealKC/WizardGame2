package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;
import WizardGame2.Utils;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

public class Enemy extends LivingGameObject implements Player.PositionObserver {
    /**
     * A builder class to encapsulate the complex ways of creating an enemy
     */
    public static class Builder {
        private final Data data;
        private final SpriteSheet spriteSheet;

        private int x, y;

        private boolean isBoss = false;
        private boolean isMiniBoss = false;

        private Builder(SpriteSheet spriteSheet, Data data) {
            this.data = data;
            this.spriteSheet = spriteSheet;
        }

        public Builder atCoordinates(int x, int y) {
            this.x = x;
            this.y = y;

            return this;
        }

        public Builder isBoss(boolean b) {
            this.isBoss = b;

            return this;
        }

        public Builder isMiniBoss(boolean b) {
            this.isMiniBoss = b;

            return this;
        }

        public Enemy build() {
            var sprite = (isBoss || isMiniBoss) ? Utils.scale(spriteSheet.crop(data.x, data.y), Boss.SIZE) : spriteSheet.crop(data.x, data.y);

            if (isBoss) {
                assert data.behaviour != null;

                Boss.Behaviour behaviour = null;
                try {
                    var behaviourClass = Class.forName(data.behaviour);
                    behaviour = (Boss.Behaviour) behaviourClass.getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException e) {
                    Utils.logException(Boss.class, e, "failed to load '%s' behaviour class", data.behaviour);
                } catch (NoSuchMethodException e) {
                    Utils.logException(Boss.class, e, "'%s' does not have a no-parameter constructor", data.behaviour);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    Utils.logException(Boss.class, e, "failed to invoke constructor");
                }
                assert behaviour != null;

                return new Boss(sprite, x, y, Boss.SIZE, Boss.SIZE, data.health, data.score, data.damage, behaviour, data.finalBoss);
            }

            if (isMiniBoss) {
                return new Enemy(sprite, x, y, Boss.SIZE, Boss.SIZE, data.health * 10, data.score * 10, data.damage * 5, data.isFlying);
            }

            return new Enemy(sprite, x, y, 32, 32, data.health, data.score, data.damage, data.isFlying);

        }
    }

    public static Builder newBuilder(SpriteSheet spriteSheet, Data data) {
        return new Builder(spriteSheet, data);
    }

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
