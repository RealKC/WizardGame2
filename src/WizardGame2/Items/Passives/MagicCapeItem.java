package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class MagicCapeItem extends PassiveItem {
    private final double magicPowerBoost;

    protected MagicCapeItem(String name, int id, BufferedImage sprite, double magicPowerBoost) {
        super(name, id, sprite);

        this.magicPowerBoost = magicPowerBoost;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseMagicPower(magicPowerBoost);
    }
}
