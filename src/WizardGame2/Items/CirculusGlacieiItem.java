package WizardGame2.Items;

import WizardGame2.Game;
import WizardGame2.GameObjects.Bullet;

import java.awt.image.BufferedImage;

public class CirculusGlacieiItem extends Item {
    static class CirculusGlacieiArea extends Bullet {
        CirculusGlacieiArea() {
            super(null, 0, 0, 32, 32, MovementType.NONE, 0.0, 0.0);
        }

        @Override
        public boolean shouldBeRemovedAfterThisHit() {
            return false;
        }

        void setPosition(int x, int y) {
            moveTo(x, y);
        }
    }

    CirculusGlacieiArea area = new CirculusGlacieiArea();

    public CirculusGlacieiItem(String name, int id, BufferedImage sprite, int attackSpeed) {
        super(name, id, sprite, attackSpeed);

        Game.getInstance().getBullets().add(area);
    }

    @Override
    void update(long currentTime) {
        area.setPosition(playerX, playerY);
    }
}
