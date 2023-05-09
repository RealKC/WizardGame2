package WizardGame2.Items;

@SuppressWarnings("unused") // It IS used, but it's created indirectly through assets
public class IgnisRespiratioFactory extends ItemFactory {
    @Override
    public Item makeItem() {
         return new IgnisRespiratioItem(itemData.name, 1, itemData.sprite);
    }
}