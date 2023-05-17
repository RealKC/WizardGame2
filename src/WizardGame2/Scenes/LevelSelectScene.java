package WizardGame2.Scenes;

import WizardGame2.*;

import java.awt.*;
import java.util.ArrayList;

public class LevelSelectScene implements Scene {
    private final Button[] buttons;

    private LevelScene levelScene = null;

    private double angle = Math.toRadians(0.0);

    enum NextScene {
        NONE,
        LEVEL,
        MAIN_MENU,
    }

    private NextScene nextScene = NextScene.NONE;

    public LevelSelectScene(ArrayList<LevelData> levelDatas) {
        int centerX = Game.getInstance().getWindowWidth() / 2;

        final int buttonWidth = 350;

        int yOffset = 150;

        int bound = Math.min(levelDatas.size(), DatabaseManager.getInstance().getNextPlayableLevel());
        buttons = new Button[bound + 1];

        int i;
        for (i = 0; i < bound; ++i) {
            var bounds = new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38);
            yOffset += 40;

            final var levelData = levelDatas.get(i);

            buttons[i] = new TextButton(bounds, (i + 1) + ". " + levelDatas.get(i).getName(), () -> {
                levelScene = LevelScene.initializeInstance(levelData);
                nextScene = NextScene.LEVEL;
            });
        }

        var bounds = new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38);
        buttons[i] = new TextButton(bounds, "Back", () -> nextScene = NextScene.MAIN_MENU);
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return nextScene == NextScene.NONE ? SceneUpdate.STAY : SceneUpdate.NEXT_SCENE;
    }

    @Override
    public void render(Graphics gfx) {
        var assets = Assets.getInstance();

        gfx.drawImage(Utils.rotateImage(assets.getOuterMagicCircle(), angle), 100, 0, null);
        angle += Math.toRadians(0.1);

        gfx.drawImage(assets.getLevelSelectBackground(), 0, 0, null);

        for (var button : buttons) {
            button.render(gfx);
        }
    }

    @Override
    public Scene nextScene() {
        switch (nextScene) {
            case LEVEL -> {
                return levelScene;
            }
            case MAIN_MENU -> {
                return new MainMenuScene();
            }
            default -> {
                return null;
            }
        }
    }
}
