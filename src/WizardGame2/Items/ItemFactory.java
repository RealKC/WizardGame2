package WizardGame2.Items;

public abstract class ItemFactory {
    protected ItemData itemData;

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public abstract Item makeItem();

    public String getName() {
        return itemData.name;
    }

    public ItemData getItemData() {
        return itemData;
    }
}
