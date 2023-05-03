package WizardGame2.Scenes;

import WizardGame2.Game;
import WizardGame2.Utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Button implements MouseListener, MouseMotionListener {
    public interface OnClick {
        void accept();
    }

    private final Rectangle bounds;

    private final String text;
    private int textWidth = -1;

    private final OnClick onClick;

    private static final Color notHovered = new Color(0, 0, 0, 0);
    private static final Color hovered = new Color(10, 10, 10, 10);

    private Color buttonColor = notHovered;

    private static final Font font = new Font(Font.DIALOG, Font.BOLD, 25);

    public Button(Rectangle bounds, String text, OnClick onClick) {
        this.bounds = bounds;
        this.text = text;
        this.onClick = onClick;

        var canvas = Game.getInstance().getCanvas();

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

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
    public void mouseReleased(MouseEvent mouseEvent) {
        if (!bounds.contains(mouseEvent.getPoint())) {
            return;
        }

        onClick.accept();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        if (bounds.contains(mouseEvent.getPoint())) {
            buttonColor = hovered;
        } else {
            buttonColor = notHovered;
        }
    }
}
