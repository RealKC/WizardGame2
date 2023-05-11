package WizardGame2.Scenes;

import WizardGame2.Keyboard;
import WizardGame2.Utils;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LevelUpScene implements Scene {
    private final LevelScene levelScene;

    public LevelUpScene(LevelScene levelScene) {
        this.levelScene = levelScene;
    }

    @Override
    public SceneUpdate update(long currentTime) {
        if (Keyboard.isKeyPressed(KeyEvent.VK_ENTER)) {
            return SceneUpdate.NEXT_SCENE;
        }

        return SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        levelScene.render(gfx);

        Utils.drawTextWithOutline(gfx, "you have leveled up", 300, 200);
    }

    @Override
    public Scene nextScene() {
        return levelScene;
    }
}
