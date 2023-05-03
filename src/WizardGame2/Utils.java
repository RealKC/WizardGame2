package WizardGame2;

import java.awt.*;
import java.awt.font.GlyphVector;

public class Utils {
    /**
     * Clamps x to the interval [min, max]
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @param x a value which may or may not be in the interval
     * @return x if x is in [min, max], min if x is less than min, or max otherwise
     */
    public static int clamp(int min, int max, int x) {
        if (x < min) {
            return min;
        }

        // I find the flow of the function easier to read this way
        //noinspection ManualMinMaxCalculation
        if (max < x) {
            return max;
        }

        return x;
    }

    public static void drawTextWithOutline(Graphics gfx, String text, int x, int y) {
        // Based on <https://stackoverflow.com/a/35222059>
        if (gfx instanceof Graphics2D) {
            var gfx2d = (Graphics2D) gfx;
            Color oldColor = gfx2d.getColor();
            RenderingHints oldHints = gfx2d.getRenderingHints();
            Stroke oldStroke = gfx2d.getStroke();

            gfx2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gfx2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            GlyphVector glyphVector = gfx2d.getFont().createGlyphVector(gfx2d.getFontRenderContext(), text);
            Shape textShape = glyphVector.getOutline(x, y);

            // Paint the outline
            gfx2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gfx2d.setColor(Color.BLACK);
            gfx2d.draw(textShape);

            // Pain the text itself
            gfx2d.setColor(Color.WHITE);
            gfx2d.fill(textShape);

            gfx2d.setColor(oldColor);
            gfx2d.setRenderingHints(oldHints);
            gfx2d.setStroke(oldStroke);
        }
    }
}
