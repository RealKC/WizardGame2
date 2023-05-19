package WizardGame2.Items.Actives;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;
import WizardGame2.Level;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MorningStarItem extends Item {
    private class Ball extends Bullet {
        private static final Color COLOR = new Color(210, 205, 231, 255);

        private static final int DISTANCE_FROM_PLAYER = 100;

        private double angle = 0.0;

        Ball(double attackDamage) {
            super(COLOR, 0, 0, 40, 40, MovementType.NONE, 1, 0, attackDamage);
        }

        @Override
        public void update(Level level, long currentTime) {
        }

        void update(int playerX, int playerY) {
            var x = playerX + DISTANCE_FROM_PLAYER * Math.cos(Math.toRadians(angle));
            var y = playerY + DISTANCE_FROM_PLAYER * Math.sin(Math.toRadians(angle));

            moveTo((int) x, (int) y);

            angle = (angle + 6) % 360;
        }

        @Override
        public boolean shouldBeRemovedAfterThisHit() {
            return false;
        }
    }

    private final Ball ball;
    private boolean firstUpdate = true;

    public MorningStarItem(String name, int id, BufferedImage sprite, double attackDamage) {
        super(name, id, sprite, 0);

        ball = new Ball(attackDamage);

        LevelScene.getInstance().getBullets().add(ball);
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!firstUpdate && (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused())) {
            return;
        }

        firstUpdate = true;
        ball.update(playerX, playerY);
    }
}
