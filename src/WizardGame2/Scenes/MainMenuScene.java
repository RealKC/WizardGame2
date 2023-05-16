package WizardGame2.Scenes;

import WizardGame2.Assets;
import WizardGame2.Game;
import WizardGame2.Utils;

import java.awt.*;

public class MainMenuScene implements Scene {
    private final Button[] buttons = new Button[2];

    boolean shouldSwitchScenes = false;

    private double angle = Math.toRadians(0.0);

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
        var assets = Assets.getInstance();

        gfx.drawImage(Utils.rotateImage(assets.getInnerMagicCircle(), angle), 204, 100, null);
        gfx.drawImage(Utils.rotateImage(assets.getOuterMagicCircle(), Math.PI / 2 - angle), 100, 0, null);
        angle += Math.toRadians(0.1);

        gfx.drawImage(assets.getMainMenuBackground(), 0, 0, null);

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

        return new LevelSelectScene(Assets.getInstance().getLevelDatas());
    }
}
