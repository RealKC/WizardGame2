package WizardGame2.Items;

/**
 * Class that can allocate numeric IDs
 */
public class IDAllocator {
    private static int id = 0;

    public static int nextId() {
        id++;
        return id;
    }
}
