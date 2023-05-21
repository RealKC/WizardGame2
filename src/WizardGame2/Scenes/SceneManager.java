package WizardGame2.Scenes;

import java.awt.*;

/**
 * The class that manages the scene-state machine of the game
 */
public class SceneManager {
    private Scene currentScene;

    /**
     * Creates a new {@link SceneManager}
     * @param scene the initial scene
     */
    public SceneManager(Scene scene) {
        currentScene = scene;
    }

    /**
     * Runs the update code of the current {@link Scene}
     * @param currentTime the current time in nanoseconds
     */
    public void update(long currentTime) {
        var result = currentScene.update(currentTime);

        switch (result) {
            case NEXT_SCENE:
                currentScene = currentScene.nextScene();
                break;
            case STAY:
                break;
        }
    }

    /**
     * Renders the current scene on the screen
     */
    public void render(Graphics gfx) {
        currentScene.render(gfx);
    }
}
