package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class GlassesInfirmaItem extends PassiveItem {
    private final double critChance;

    public GlassesInfirmaItem(String name, int id, BufferedImage sprite, double critChance) {
        super(name, id, sprite);

        this.critChance = critChance;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseCritChance(critChance);
    }
}
