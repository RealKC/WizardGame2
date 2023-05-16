package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class FluerulSfVineriItem extends PassiveItem {
    final double speedBost;

    protected FluerulSfVineriItem(String name, int id, BufferedImage sprite, double speedBost) {
        super(name, id, sprite, 0);
        this.speedBost = speedBost;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseSpeedBost(speedBost);
    }
}
