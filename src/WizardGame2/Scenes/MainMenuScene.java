package WizardGame2.Scenes;

import WizardGame2.Game;

import java.awt.*;

public class MainMenuScene implements Scene {
    private final Button[] buttons = new Button[2];

    boolean shouldSwitchScenes = false;

    public MainMenuScene() {
        buttons[0] = new TextButton(new Rectangle(Game.getInstance().getWindowWidth() / 2 - 100, 200, 200, 30), "Play", () -> shouldSwitchScenes = true);
        buttons[1] = new TextButton(new Rectangle(Game.getInstance().getWindowWidth() / 2 - 100, 300, 200, 30), "Exit", () -> Game.getInstance().exit());
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return shouldSwitchScenes ? SceneUpdate.NEXT_SCENE : SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        var oldColor = gfx.getColor();
        gfx.setColor(Color.BLACK);

        for (var button : buttons) {
            button.render(gfx);
        }

        gfx.setColor(oldColor);
    }

    @Override
    public Scene nextScene() {
        for (var button : buttons) {
            button.unregisterListeners();
        }

        return LevelScene.getInstance();
    }
}
