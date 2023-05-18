package WizardGame2.Items.Passives;

import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused") // It IS used, but it's created indirectly through assets
public class GlassesInfirmaFactory extends ItemFactory {
    @Override
    public Item makeItem() {
         return new GlassesInfirmaItem(itemData.name, 42, itemData.sprite, itemData.extraValue);
    }
}