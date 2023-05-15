package WizardGame2.Items;

import WizardGame2.Assets;
import WizardGame2.GameObjects.Player;
import WizardGame2.Utils;

import java.awt.*;
import java.util.ArrayList;

public class Inventory {
    private static final int MAX_ITEMS = 3;

    // https://colorpicker.me/#891ad6
    private static final Color borderColor = new Color(137, 26, 214);

    private final ArrayList<Item> activeItems = new ArrayList<>(MAX_ITEMS);
    private final ArrayList<Item> passiveItems = new ArrayList<>(MAX_ITEMS);

    public Inventory() {}

    public void render(Graphics gfx) {
        int width = Assets.getInstance().getItems().getTileWidth();
        int height = Assets.getInstance().getItems().getTileHeight();

        int x = 12, y = 12;

        Color oldColor = gfx.getColor();
        gfx.setColor(borderColor);

        Stroke oldStroke = null;
        if (gfx instanceof Graphics2D) {
            var gfx2d = (Graphics2D) gfx;
            oldStroke = gfx2d.getStroke();
            gfx2d.setStroke(new BasicStroke(3.0f));
        }

        for (var activeItem : activeItems) {
            gfx.drawRect(x - 2, y - 2, width + 4, height + 4);
            gfx.drawImage(activeItem.getSprite(), x, y, null);
            x += width + 4;
        }

        for (int i = 0; i < MAX_ITEMS - activeItems.size(); ++i) {
            gfx.drawRect(x - 2, y - 2, width + 4, height + 4);
            x += width + 4;
        }

        y += height + 4;

        x = 12;
        for (var passiveItem : passiveItems) {
            gfx.drawRect(x - 2, y - 2, width + 4, height + 4);
            gfx.drawImage(passiveItem.getSprite(), x, y, null);
            x += width + 4;
        }

        for (int i = 0; i < MAX_ITEMS - passiveItems.size(); ++i) {
            gfx.drawRect(x - 2, y - 2, width + 4, height + 4);
            x += width + 4;
        }

        if (oldStroke != null) {
            ((Graphics2D) gfx).setStroke(oldStroke);
        }
        gfx.setColor(oldColor);
    }

    public void update(long currentTime, Player.Stats stats) {
        for (var activeItem : activeItems) {
            activeItem.update(currentTime, stats);
        }
    }

    public void upgradeActiveItem(int id) {

    }

    public void upgradePassiveItem(int id) {

    }

    public boolean hasItem(ItemFactory itemFactory) {
        if (itemFactory == null) {
            return false;
        }

        for (var activeItem : activeItems) {
            if (activeItem.getName().equals(itemFactory.getName())) {
                return true;
            }
        }

        for (var passiveItem : passiveItems) {
            if (passiveItem.getName().equals(itemFactory.getName())) {
                return true;
            }
        }

        return false;
    }

    public void addActiveItem(Item item) {
        if (activeItems.size() == 3) {
            Utils.warn("Tried adding '%s' but activeItems is full", item);

            return;
        }

        for (var activeItem : activeItems) {
            if (activeItem.getName().equals(item.getName())) {
                Utils.warn("Tried adding '%s' but it was already in the inventory", item);

                return;
            }
        }

        activeItems.add(item);
    }

    public int activeItemCount() {
        return activeItems.size();
    }

    public int passiveItemCount() {
        return passiveItems.size();
    }
}
