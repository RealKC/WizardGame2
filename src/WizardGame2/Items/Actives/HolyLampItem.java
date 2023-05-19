package WizardGame2.Items.Actives;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class HolyLampItem extends Item {
    private static class HolyLight extends Bullet {
        static final Color COLOR = new Color(243, 211, 6, 255);

        static final double WIDTH = 192;
        static final double HEIGHT = 64;

        static final double[] PROBLEMATIC_ANGLES = new double[] { 45, 90, 135 };

        Player.Stats playerStats = null;
        /**
         * The angle of the attack, is in <b>degrees</b>, not radians, because of how Arc2D works
         */
        double angle = 0.0;

        HolyLight(double attackDamage) {
            super(COLOR, 0, 0, 24, 24, MovementType.NONE, 0.0, 0.0, attackDamage, Target.ENEMY);
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
                var arc = new Arc2D.Double(getX() - centerX, getY() - centerY, WIDTH, HEIGHT, angle - 32.0, 48.0, Arc2D.PIE);
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

    private final HolyLight holyLight;

    public HolyLampItem(String name, int id, BufferedImage sprite, double attackDamage) {
        super(name, id, sprite, 0);

        holyLight = new HolyLight(attackDamage);

        var levelScene = LevelScene.getInstance();
        levelScene.getBullets().add(holyLight);
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        holyLight.setPosition(playerX, playerY, playerAngle);

        if (holyLight.playerStats == null) {
            holyLight.playerStats = stats;
        }
    }
}
