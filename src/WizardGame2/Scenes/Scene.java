package WizardGame2.Scenes;

import java.awt.*;

/**
 * Interface defining the operations for a Game scene
 */
public interface Scene {
    /**
     * Runs scene update code
     * @param currentTime the current time in nanoseconds
     * @return whether the {@link SceneManager} should progress to the next scene or not
     */
    SceneUpdate update(long currentTime);

    /**
     * Renders the scene to the screen
     */
    void render(Graphics gfx);

    /**
     * Returns the next scene the {@link SceneManager} should progress to
     */
    Scene nextScene();
}
