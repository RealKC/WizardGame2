package WizardGame2.Items.Passives;

import WizardGame2.Items.IDAllocator;
import WizardGame2.Items.Item;
import WizardGame2.Items.ItemFactory;

@SuppressWarnings("unused")
public class ChainmailShirtFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new ChainmailShirtItem(itemData.name, IDAllocator.nextId(), itemData.sprite, (int) itemData.extraValue);
    }
}
