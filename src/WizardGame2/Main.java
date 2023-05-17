package WizardGame2;

public class Main {
    // Poor man's feature flags

    /**
     * Allows code to check if it's running in a debug build and act appropriately
     */
    public static final boolean IS_DEBUG_BUILD = true;

    public static void main(String[] args) {
        Game.getInstance().startGame();
    }
}
