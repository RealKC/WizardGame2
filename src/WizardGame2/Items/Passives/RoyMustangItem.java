package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class RoyMustangItem extends PassiveItem {
    private final double magicBoost;
    private final int pickupLoss;

    public RoyMustangItem(String name, int id, BufferedImage sprite, double magicBoost, int pickupLoss) {
        super(name, id, sprite);

        this.magicBoost = magicBoost;
        this.pickupLoss = pickupLoss;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseMagicPower(magicBoost);
        stats.increasePickupRange(pickupLoss);
    }
}
