package WizardGame2.Items.Actives;

import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused") // It IS used, but it's created indirectly through assets
public class TerraGuttaFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new TerraGuttaItem(itemData.name, IDAllocator.nextId(), itemData.sprite, (int)itemData.baseAttackSpeed, itemData.baseAttackDamage);
    }
}
