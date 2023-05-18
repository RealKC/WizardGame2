package WizardGame2.Items.Passives;

import WizardGame2.GameObjects.Player;
import WizardGame2.Items.Item;
import WizardGame2.Scenes.LevelScene;

import java.awt.image.BufferedImage;

public class RingAnimaItem extends Item {
    private final double healAmount;

    protected RingAnimaItem(String name, int id, BufferedImage sprite, int speed, double healAmount) {
        super(name, id, sprite, speed);

        this.healAmount = healAmount;
    }

    @Override
    public void update(long currentTime, Player.Stats stats) {
        if (!hasCooldownPassed(stats.getHaste()) || LevelScene.getInstance().getIsPaused()) {
            return;
        }

        stats.healBy(healAmount);
    }
}
