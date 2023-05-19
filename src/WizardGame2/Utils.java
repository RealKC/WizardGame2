package WizardGame2;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

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
        if (gfx instanceof Graphics2D gfx2d) {
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

    public static BufferedImage scale(BufferedImage original, int newSize) {
        return scale(original, newSize, newSize);
    }

    static BufferedImage scale(BufferedImage original, int newWidth, int newHeight) {
        // https://stackoverflow.com/a/4216315
        var scaled = new BufferedImage(newWidth, newHeight, original.getType());

        var gfx = scaled.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gfx.drawImage(original, 0, 0, newWidth, newHeight, 0, 0, original.getWidth(), original.getHeight(), null);
        gfx.dispose();

        return scaled;
    }

    public static <T> void logException(Class<T> clazz, Exception e, String message, Object... args) {
        System.out.printf("[in %s] %s: %s\n", clazz == null ? "???" : clazz.getName(), String.format(message, args), e);
        e.printStackTrace();
    }

    public static boolean isClose(int val, int to) {
        return (to - 10 <= val) && (val <= to + 10);
    }

    public static void warn(String format, Object... args) {
        System.out.print("[WARN] ");
        System.out.printf(format, args);
        System.out.println();
        Thread.dumpStack();
    }

    public static BufferedImage rotateImage(BufferedImage image, double angle) {
        BufferedImage rotated = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform transform = new AffineTransform();
        transform.rotate(angle, (double) image.getWidth() / 2, (double) image.getHeight() / 2);
        g2d.setTransform(transform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotated;
    }
}
