package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public class ChainmailShirtItem extends PassiveItem {
    private final int hpBoost;

    protected ChainmailShirtItem(String name, int id, BufferedImage sprite, int hpBoost) {
        super(name, id, sprite);

        this.hpBoost = hpBoost;
    }

    @Override
    protected void applyBuffs(Player.Stats stats) {
        stats.increaseMaxHP(hpBoost);
    }
}
