package WizardGame2.Graphics;

import WizardGame2.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Class that makes loading images from resources easier
 */
public class ImageLoader {
    /**
     * Load an image into memory
     * @param path resource root-relative path to the image
     * @return the image, or null if it could not be loaded
     */
    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(Objects.requireNonNull(ImageLoader.class.getResource(path)));
        } catch (IOException e) {
            Utils.logException(ImageLoader.class, e, "got an IO Exception trying to load '%s'", path);
        }

        return null;
    }
}

