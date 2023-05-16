package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;

import java.awt.image.BufferedImage;

public abstract class PassiveItem extends Item {
    protected PassiveItem(String name, int id, BufferedImage sprite, int speed) {
        super(name, id, sprite, speed);
    }

    private boolean firstUpdate = true;

    public void update(long currentTime, Player.Stats stats) {
        if (firstUpdate) {
            firstUpdate = false;
            applyBuffs(stats);
        }
    }

    protected abstract void applyBuffs(Player.Stats stats);
}
