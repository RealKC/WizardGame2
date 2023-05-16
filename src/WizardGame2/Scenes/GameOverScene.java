package WizardGame2.Scenes;

import WizardGame2.Game;
import WizardGame2.Utils;

import java.awt.*;

public class GameOverScene implements Scene {
    private final LevelScene levelScene;

    public enum GameResult {
        LOST,
        WON,
    }

    private final GameResult gameResult;
    private final int canvasWidth, canvasHeight;

    private static final Font levelFont = new Font(Font.MONOSPACED, Font.PLAIN, 25);
    private static final Color overlay = new Color(0, 0, 0, 128);

    public GameOverScene(LevelScene levelScene, GameResult gameResult) {
        this.levelScene = levelScene;
        this.gameResult = gameResult;
        canvasWidth = Game.getInstance().getWindowWidth();
        canvasHeight = Game.getInstance().getWindowHeight();

        levelScene.setPaused(true);
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return SceneUpdate.STAY;
    }

    @Override
    public void render(Graphics gfx) {
        levelScene.render(gfx);

        var oldColor = gfx.getColor();
        gfx.setColor(overlay);
        gfx.fillRect(0, 0, canvasWidth, canvasHeight);
        gfx.setColor(oldColor);

        var oldFont = gfx.getFont();
        gfx.setFont(levelFont);
        switch (gameResult) {
            case WON: {
                Utils.drawTextWithOutline(gfx, "You have won!!!", canvasWidth / 2, canvasHeight / 2);
                break;
            }
            case LOST: {
                Utils.drawTextWithOutline(gfx, "You have lost :(", canvasWidth / 2, canvasHeight / 2);
                break;
            }
        }
        gfx.setFont(oldFont);
    }

    @Override
    public Scene nextScene() {
        return null;
    }
}
