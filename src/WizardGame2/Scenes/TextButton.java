package WizardGame2.Scenes;

import WizardGame2.Game;
import WizardGame2.Utils;

import java.awt.*;

public class TextButton extends Button {
    public interface OnClick {
        void accept();
    }

    private final String text;

    private int textWidth = -1;

    private final OnClick onClick;

    public TextButton(Rectangle bounds, String text, OnClick onClick) {
        super(bounds);
        this.text = text;
        this.onClick = onClick;

        var canvas = Game.getInstance().getCanvas();

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
    public void render(Graphics gfx) {

        var oldColor = gfx.getColor();
        gfx.setColor(buttonColor);
        gfx.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        gfx.setColor(oldColor);

        var oldFont = gfx.getFont();
        gfx.setFont(font);

        if (textWidth < 0) {
            var fontMetrics = gfx.getFontMetrics();
            var stringBounds = fontMetrics.getStringBounds(text, gfx);
            textWidth = (int) stringBounds.getWidth();
        }

        int x = bounds.x + bounds.width / 2 - textWidth / 2;
        int y = bounds.y + bounds.height - 5;
        Utils.drawTextWithOutline(gfx, text, x, y);
        gfx.setFont(oldFont);
    }

    @Override
    void onClicked() {
        onClick.accept();
    }
}
