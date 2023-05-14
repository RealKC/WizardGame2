package WizardGame2.Scenes;

import WizardGame2.Game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * An abstract class that implements clicking on an area of the screen
 */
public abstract class Button implements MouseListener, MouseMotionListener {

    protected final Rectangle bounds;

    private static final Color notHovered = new Color(0, 0, 0, 0);
    private static final Color hovered = new Color(10, 10, 10, 10);

    protected Color buttonColor = notHovered;

    protected static final Font font = new Font(Font.DIALOG, Font.BOLD, 25);

    public Button(Rectangle bounds) {
        this.bounds = bounds;

        var canvas = Game.getInstance().getCanvas();

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    abstract public void render(Graphics gfx);

    abstract void onClicked();

    @Override
    public final void mouseReleased(MouseEvent mouseEvent) {
        if (!bounds.contains(mouseEvent.getPoint())) {
            return;
        }

        onClicked();
    }

    @Override
    public final void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public final void mousePressed(MouseEvent mouseEvent) {
    }

    @Override
    public final void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public final void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public final void mouseDragged(MouseEvent mouseEvent) {
    }

    @Override
    public final void mouseMoved(MouseEvent mouseEvent) {
        if (bounds.contains(mouseEvent.getPoint())) {
            buttonColor = hovered;
        } else {
            buttonColor = notHovered;
        }
    }
}
