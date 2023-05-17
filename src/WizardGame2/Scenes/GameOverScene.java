package WizardGame2.Scenes;

import WizardGame2.DatabaseManager;
import WizardGame2.Game;
import WizardGame2.Utils;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GameOverScene implements Scene {
    private final LevelScene levelScene;

    public enum GameResult {
        LOST,
        WON,
    }

    private final GameResult gameResult;
    private final int canvasWidth, canvasHeight;

    private static final Font BIG_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 30);
    private static final Font NORMAL_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 18);

    private static final Color overlay = new Color(0, 0, 0, 128);

    private static class Line {
        Line(String text, Font font, int x, int y) {
            this.text = text;
            this.font = font;
            this.x = x;
            this.y = y;
        }

        private final Font font;
        private final String text;
        int width = -1;
        private final int x, y;

        void render(Graphics gfx) {
            var oldFont = gfx.getFont();
            gfx.setFont(font);

            if (width < 0) {
                var fontMetrics = gfx.getFontMetrics();
                var stringBounds = fontMetrics.getStringBounds(text, gfx);
                width = (int) stringBounds.getWidth();
            }

            Utils.drawTextWithOutline(gfx, text,x - width / 2, y);
            gfx.setFont(oldFont);
        }
    }

    private final Line mainText;
    private final Line score;

    private final Line[] scoreLines;

    private enum NextScene {
        NONE,
        RETRY,
        MENU,
    }

    NextScene nextScene = NextScene.NONE;

    private final TextButton[] buttons = new TextButton[2];

    public GameOverScene(LevelScene levelScene, GameResult gameResult) {
        this.levelScene = levelScene;
        this.gameResult = gameResult;
        canvasWidth = Game.getInstance().getWindowWidth();
        canvasHeight = Game.getInstance().getWindowHeight();

        levelScene.setPaused(true);

        int yOffset = 140;
        int x = canvasWidth / 2;
        mainText = new Line(gameResult == GameResult.WON ? "You have won" : "You have lost", BIG_FONT, x, yOffset);
        yOffset += 40;
        score = new Line(processNewScore(levelScene.getScore()), NORMAL_FONT, x, yOffset);
        yOffset += 40;

        buttons[0] = new TextButton(new Rectangle(x - 200, yOffset, 150, 40), "Retry", () -> nextScene = NextScene.RETRY);
        buttons[1] = new TextButton(new Rectangle(x, yOffset, 150, 40), "Main Menu", () -> nextScene = NextScene.MENU);
        yOffset += 80;

        scoreLines = new Line[4];
        scoreLines[0] = new Line("Previous top scores", NORMAL_FONT, x, yOffset);
        yOffset += 40;

        var topScores = DatabaseManager.getInstance().getTopScoresFor(levelScene.getName());
        for (int i = 1; i < 4; ++i) {
            var top = topScores.get(i);
            scoreLines[i] = new Line(i + "# " + top.getScore() + " on " + top.getObtainedAt(), NORMAL_FONT, x, yOffset);
            yOffset += 40;
        }
    }

    private String processNewScore(int score) {
        var databaseManager = DatabaseManager.getInstance();

        var topScores = databaseManager.getTopScoresFor(levelScene.getName());

        var now = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        databaseManager.addNewScoreEntry(levelScene.getName(), now.format(formatter), score);

        boolean isHighScore = false;

        if (topScores.isEmpty()) {
            isHighScore = true;
        }

        if (score > topScores.get(0).getScore()) {
            isHighScore = true;
        }

        if (isHighScore) {
            return "New hi-score: " + score;
        } else {
            return "Score: " + score;
        }
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return nextScene == NextScene.NONE ? SceneUpdate.STAY : SceneUpdate.NEXT_SCENE;
    }

    @Override
    public void render(Graphics gfx) {
        levelScene.render(gfx);

        var oldColor = gfx.getColor();
        gfx.setColor(overlay);
        gfx.fillRect(0, 0, canvasWidth, canvasHeight);
        gfx.setColor(oldColor);

        mainText.render(gfx);
        score.render(gfx);

        for (var line : scoreLines) {
            line.render(gfx);
        }

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
            case RETRY -> {
                return LevelScene.restartLevel();
            }
            case MENU -> {
                return new MainMenuScene();
            }
            default -> {
                return null;
            }
        }
    }
}
