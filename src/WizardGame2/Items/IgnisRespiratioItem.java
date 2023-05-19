package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class IgnisRespiratioItem extends Item {
    private static class Flame extends Bullet {
        static final Color COLOR = new Color(222, 100, 36, 255);

        static final double WIDTH = 192;
        static final double HEIGHT = 64;

        static final double[] PROBLEMATIC_ANGLES = new double[] { 45, 90, 135 };

        Player.Stats playerStats = null;
        /**
         * The angle of the attack, is in <b>degrees</b>, not radians, because of how Arc2D works
         */
        double angle = 0.0;

        Flame(double attackDamage) {
            super(COLOR, 0, 0, 24, 24, MovementType.NONE, 0.0, 0.0, attackDamage);
        }

        void setPosition(int x, int y, double angle) {
            moveTo((int) (x + 16 - WIDTH / 2), (int) (y + 16  - HEIGHT / 2));

            double angleInDegrees = Math.toDegrees(angle);

            // HACK: Vertical angles go in the oposite direction of where the player moves, so flip them around
            if (Arrays.stream(PROBLEMATIC_ANGLES).anyMatch((val) -> val == Math.abs(angleInDegrees))) {
                this.angle = -Math.toDegrees(angle);
            } else {
                this.angle = Math.toDegrees(angle);
            }
        }

        @Override
        public void render(Graphics gfx, int centerX, int centerY) {
            var oldColor = gfx.getColor();
            gfx.setColor(color);
            if (gfx instanceof Graphics2D gfx2d) {
                System.out.println("angle: " + angle);
                var arc = new Arc2D.Double(getX() - centerX, getY() - centerY, WIDTH, HEIGHT, angle - 16.0, 32.0, Arc2D.PIE);
                gfx2d.fill(arc);
            }
            gfx.setColor(oldColor);
        }

        @Override
        public boolean shouldBeRemovedAfterThisHit() {
            return false;
        }

        @Override
        public double getAttackDamage() {
            return playerStats.applyAttackModifiers(super.getAttackDamage());
        }
    }

    private final Flame flame;

    public IgnisRespiratioItem(String name, int id, BufferedImage sprite, double attackDamage) {
        super(name, id, sprite, 0);

        flame = new Flame(attackDamage);

        var levelScene = LevelScene.getInstance();
        levelScene.getBullets().add(flame);
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        flame.setPosition(playerX, playerY, playerAngle);

        if (flame.playerStats == null) {
            flame.playerStats = stats;
        }
    }
}
