package WizardGame2.Scenes;

import java.awt.*;

public class SceneManager {
    private Scene currentScene;

    public SceneManager(Scene scene) {
        currentScene = scene;
    }

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

    public void render(Graphics gfx) {
        currentScene.render(gfx);
    }
}
