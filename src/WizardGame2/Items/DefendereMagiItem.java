package WizardGame2.Items;

import WizardGame2.GameObjects.Bullet;
import WizardGame2.Scenes.LevelScene;

import java.awt.image.BufferedImage;

public class DefendereMagiItem extends Item {
    private static class DefendereMagiArea extends Bullet {
        DefendereMagiArea(double attackDamage) {
            super(null, 0, 0, 32, 32, MovementType.NONE, 0.0, 0.0, attackDamage);
        }

        @Override
        public boolean shouldBeRemovedAfterThisHit() {
            return false;
        }

        void setPosition(int x, int y) {
            moveTo(x, y);
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
    void update(long currentTime) {
        area.setPosition(playerX, playerY);
    }
}
