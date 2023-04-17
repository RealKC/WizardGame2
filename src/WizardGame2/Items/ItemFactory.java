package WizardGame2.Items;

public abstract class ItemFactory {
    protected ItemData itemData;

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

     public abstract Item makeItem();
}
