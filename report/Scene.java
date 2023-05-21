public enum SceneUpdate {
    /**
     * The scene manager should progress to the next scene
     */
    NEXT_SCENE,
    /**
     * The scene manager should stay on the current scene
     */
    STAY,
}

public interface Scene {
    /**
     * Called every frame to allow the scene to run code
     */
    SceneUpdate update(long currentTime);

    /**
     * Called every frame to render the scene
     */
    void render(Graphics gfx);

    /**
     * Called once the Scene indicates that the game should progress to the next scene
     */
    Scene nextScene();
}
