package WizardGame2.Items.Passives;

import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused")
public class OrichalcumMagnetFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new OrichalcumMagnetItem(itemData.name, 45, itemData.sprite, (int) itemData.extraValue);
    }
}
