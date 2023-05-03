package WizardGame2.Scenes;

import java.awt.*;

/**
 * Interface defining the operations for a Game scene
 */
public interface Scene {
    SceneUpdate update(long currentTime);

    void render(Graphics gfx);

    Scene nextScene();
}
