package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class OrichalcumMagnetItem extends PassiveItem {
    private final int pickupBoost;

    public OrichalcumMagnetItem(String name, int id, BufferedImage sprite, int pickupBoost) {
        super(name, id, sprite);

        this.pickupBoost = pickupBoost;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increasePickupRange(pickupBoost);
    }
}
