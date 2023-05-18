package WizardGame2.Items.Passives;

import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused")
public class RoyMustangFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new RoyMustangItem(itemData.name, IDAllocator.nextId(), itemData.sprite, itemData.extraValue, (int) itemData.baseAttackDamage);
    }
}
