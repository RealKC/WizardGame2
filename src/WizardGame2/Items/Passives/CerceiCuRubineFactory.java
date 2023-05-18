package WizardGame2.Items.Passives;

import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused")
public class CerceiCuRubineFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new CerceiCuRubineItem(itemData.name, IDAllocator.nextId(), itemData.sprite, itemData.extraValue);
    }
}
