package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.GameObjects.Player;
import WizardGame2.Scenes.LevelScene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DefendereMagiItem extends Item {
    private static class DefendereMagiArea extends Bullet {
        static final int BASE_DIAMATER = 60;
        static final int BASE_RADIUS = BASE_DIAMATER / 2;

        static final Color COLOR = new Color(246, 194, 14, 139);

        private Player.Stats playerStats;

        DefendereMagiArea(double attackDamage) {
            super(COLOR, 0, 0, BASE_DIAMATER, BASE_DIAMATER, MovementType.NONE, 0.0, 0.0, attackDamage);
        }

        @Override
        public boolean shouldBeRemovedAfterThisHit() {
            return false;
        }


        @Override
        public double getAttackDamage() {
            return playerStats.applyAttackModifiers(super.getAttackDamage());
        }

        void setPosition(int x, int y) {
            moveTo(x - BASE_RADIUS + 16, y - BASE_RADIUS + 16);
        }
    }

    private final DefendereMagiArea area;

    public DefendereMagiItem(String name, int id, BufferedImage sprite, double attackDamage) {
        super(name, id, sprite, 0);
        area = new DefendereMagiArea(attackDamage);

        var levelScene = LevelScene.getInstance();
        assert levelScene != null;
        levelScene.getBullets().add(area);
    }

    @Override
    void update(long currentTime, Player.Stats stats) {
        area.setPosition(playerX, playerY);

        if (area.playerStats == null) {
            area.playerStats = stats;
        }
    }
}
