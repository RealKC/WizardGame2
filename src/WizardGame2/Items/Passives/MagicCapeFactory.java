package WizardGame2.Items.Passives;

import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused")
public class MagicCapeFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new MagicCapeItem(itemData.name, 43, itemData.sprite, itemData.extraValue);
    }
}
