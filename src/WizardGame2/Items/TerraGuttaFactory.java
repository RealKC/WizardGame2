package WizardGame2.Items;

@SuppressWarnings("unused") // It IS used, but it's created indirectly through assets
public class TerraGuttaFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new TerraGuttaItem(itemData.name, 1, itemData.sprite, (int)itemData.baseAttackSpeed, itemData.baseAttackDamage);
    }
}
