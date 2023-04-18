package WizardGame2.Items;

@SuppressWarnings("unused") // It IS used, but it's created indirectly through assets
public class DefendereMagiFactory extends ItemFactory {
    @Override
    public Item makeItem() {
        return new DefendereMagiItem(itemData.name, 2, itemData.sprite);
    }
}
