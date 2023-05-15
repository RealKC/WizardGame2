package WizardGame2.Scenes;

import WizardGame2.DatabaseManager;
import WizardGame2.Game;
import WizardGame2.LevelData;
import WizardGame2.Utils;

import java.awt.*;
import java.util.ArrayList;

public class LevelSelectScene implements Scene {
    private final Button[] buttons;
    private int textWidth = -1;
    private final int centerX;

    private LevelScene levelScene = null;

    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 30);

    public LevelSelectScene(ArrayList<LevelData> levelDatas) {
        buttons = new Button[levelDatas.size()];

        centerX = Game.getInstance().getWindowWidth() / 2;

        final int buttonWidth = 350;

        int yOffset = 150;

        int bound = Math.min(levelDatas.size(), DatabaseManager.getInstance().getNextPlayableLevel() + 1);

        for (int i = 0; i < bound; ++i) {
            var bounds = new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38);
            final var levelData = levelDatas.get(i);

            //noinspection CodeBlock2Expr
            buttons[i] = new TextButton(bounds, levelDatas.get(i).getName(), () -> {
                levelScene = LevelScene.initializeInstance(levelData);
            });
        }
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return levelScene == null ? SceneUpdate.STAY : SceneUpdate.NEXT_SCENE;
    }

    @Override
    public void render(Graphics gfx) {
        String text = "Pick a level";

        var oldFont = gfx.getFont();
        gfx.setFont(FONT);

        if (textWidth < 0) {
            var fontMetrics = gfx.getFontMetrics();
            var stringBounds = fontMetrics.getStringBounds(text, gfx);
            textWidth = (int) stringBounds.getWidth();
        }

        Utils.drawTextWithOutline(gfx, text, centerX - textWidth / 2, 100);
        gfx.setFont(oldFont);

        for (var button : buttons) {
            button.render(gfx);
        }
    }

    @Override
    public Scene nextScene() {
        return levelScene;
    }
}
