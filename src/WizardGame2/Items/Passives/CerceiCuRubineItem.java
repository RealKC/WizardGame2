package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class CerceiCuRubineItem extends PassiveItem {
    private final double attackSpeed;

    public CerceiCuRubineItem(String name, int id, BufferedImage sprite, double attackSpeed) {
        super(name, id, sprite);

        this.attackSpeed = attackSpeed;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseHaste(attackSpeed);
    }
}
