package WizardGame2.Scenes;

/**
 * The result of calling {@link Scene#update(long)}
 */
public enum SceneUpdate {
    /**
     * The scene manager should progress to the next scene
     */
    NEXT_SCENE,
    /**
     * The scene manager should keep the current scene
     */
    STAY,
}
