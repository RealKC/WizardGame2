package WizardGame2.Items.Actives;

import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused")
public class CirculusGlacieiFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new CirculusGlacieiItem(itemData.name, IDAllocator.nextId(), itemData.sprite, (int)itemData.baseAttackSpeed, itemData.baseAttackDamage);
    }
}
