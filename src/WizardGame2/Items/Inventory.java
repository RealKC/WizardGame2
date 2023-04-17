package WizardGame2.Items;

import java.awt.*;
import java.util.ArrayList;

public class Inventory {
    private final ArrayList<Item> activeItems = new ArrayList<>(8);
    private final ArrayList<Item> passiveItems = new ArrayList<>(8);

    public Inventory() {}

    public void render(Graphics gfx) {
        for (var activeItem : activeItems) {
            gfx.drawImage(activeItem.getSprite(), 0, 0, null);
        }
    }

    public void update(long currentTime) {
        for (var activeItem : activeItems) {
            activeItem.update(currentTime);
        }
    }

    public void upgradeActiveItem(int id) {

    }

    public void upgradePassiveItem(int id) {

    }

    public void addActiveItem(Item item) {
        activeItems.add(item);
    }
}
