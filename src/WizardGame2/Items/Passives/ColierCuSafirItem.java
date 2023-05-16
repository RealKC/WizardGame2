package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class ColierCuSafirItem extends PassiveItem {
    private final double magicPower;

    public ColierCuSafirItem(String name, int id, BufferedImage sprite, double magicPower) {
        super(name, id, sprite);

        this.magicPower = magicPower;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseMagicPower(magicPower);
    }
}
