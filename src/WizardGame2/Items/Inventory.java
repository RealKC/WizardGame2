package WizardGame2.Items;

import WizardGame2.Assets;

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
        if (gfx instanceof Graphics2D gfx2d) {
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
