package WizardGame2.Scenes;

import WizardGame2.Game;

import java.awt.*;

public class PauseMenuScene implements Scene {
    private enum NextScene {
        LEVEL,
        MAIN_MENU,
    }

    private static final Color background = new Color(10, 10, 10, 128);

    private boolean shouldSwitchScenes = false;
    private NextScene nextScene = NextScene.LEVEL;

    private final LevelScene levelScene;

    private final Button[] buttons = new Button[3];

    public PauseMenuScene(LevelScene levelScene) {
        this.levelScene = levelScene;

        buttons[0] = new Button(new Rectangle(Game.getInstance().getWindowWidth() / 2 - 100, 200, 250, 30), "Continue", () -> {
            shouldSwitchScenes = true;
            nextScene = NextScene.LEVEL;
        });
        buttons[1] = new Button(new Rectangle(Game.getInstance().getWindowWidth() / 2 - 100, 250, 250, 30), "Back to Main Menu", () -> {
            shouldSwitchScenes = true;
            nextScene = NextScene.MAIN_MENU;
        });
        buttons[2] = new Button(new Rectangle(Game.getInstance().getWindowWidth() / 2 - 100, 300, 250, 30), "Quit to desktop", () -> {
            Game.getInstance().exit();
        });
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return shouldSwitchScenes ? SceneUpdate.NEXT_SCENE : SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        levelScene.render(gfx);

        var game = Game.getInstance();
        var oldColor = gfx.getColor();
        gfx.setColor(background);
        gfx.fillRect(0, 0, game.getWindowWidth(), game.getWindowWidth());
        gfx.setColor(Color.BLACK);

        for (var button : buttons) {
            button.render(gfx);
        }

        gfx.setColor(oldColor);
    }

    @Override
    public Scene nextScene() {
        switch (nextScene) {
            case LEVEL:
                levelScene.setPaused(false);
                return levelScene;
            case MAIN_MENU:
                levelScene.onLevelLeave();
                LevelScene.reset();
                return new MainMenuScene();
        }

        return null;
    }
}
