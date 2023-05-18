package WizardGame2.Scenes;

import WizardGame2.*;
import WizardGame2.GameObjects.Player;

import java.awt.*;
import java.util.ArrayList;

public class CharacterSelectScene implements Scene {
    enum NextScene {
        NONE,
        LEVEL,
        BACK,
    }

    NextScene nextScene = NextScene.NONE;
    LevelScene levelScene;

    final Button[] buttons;
    final LevelData levelData;

    double angle = 0.0;

    public CharacterSelectScene(LevelData levelData, ArrayList<Player.Data> characterDatas) {
        this.levelData = levelData;

        int centerX = Game.getInstance().getWindowWidth() / 2;
        var databaseManager = DatabaseManager.getInstance();
        var viableCharacters = characterDatas
                .stream()
                .filter((data) -> databaseManager.isCharacterUnlocked(data.getName()))
                .toList();

        final int buttonWidth = 350;

        int yOffset = 200;

        buttons = new Button[viableCharacters.size() + 1];

        for (int i = 0; i < viableCharacters.size(); ++i) {
            var bounds = new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38);
            yOffset += 40;

            final var character = viableCharacters.get(i);

            buttons[i] = new TextButton(bounds, character.getName(), () -> {
                levelScene = LevelScene.initializeInstance(levelData, character);
                nextScene = NextScene.LEVEL;
            });
        }

        buttons[buttons.length - 1] = new TextButton(new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38), "Back", () -> nextScene = NextScene.BACK);
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return nextScene != NextScene.NONE ? SceneUpdate.NEXT_SCENE : SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        var assets = Assets.getInstance();

        gfx.drawImage(Utils.rotateImage(assets.getInnerMagicCircle(), angle), 204, 100, null);
        gfx.drawImage(assets.getCharacterSelectBackground(), 0, 0, null);
        angle -= Math.toRadians(0.1);

        for (var button : buttons) {
            button.render(gfx);
        }
    }

    @Override
    public Scene nextScene() {
        for (var button : buttons) {
            button.unregisterListeners();
        }

        switch (nextScene) {
            case LEVEL -> {
                return levelScene;
            }

            case BACK -> {
                return new LevelSelectScene(Assets.getInstance().getLevelDatas());
            }
        }

        return null;
    }
}
