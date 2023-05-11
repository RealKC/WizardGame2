package WizardGame2.GameObjects;

import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Level;

import java.awt.image.BufferedImage;

public class ExperienceObject extends GameObject {
    private final int value;

    private static BufferedImage spriteForValue(SpriteSheet spriteSheet, int value) {
        if (value <= 35) {
            return spriteSheet.crop(0, 1);
        } else if (value <= 75){
            return spriteSheet.crop(1,1);
        } else {
            return spriteSheet.crop(2, 1);
        }
    }


    public ExperienceObject(SpriteSheet spriteSheet, int x, int y, int hitboxWidth, int hitboxHeight, int value) {
        super(spriteForValue(spriteSheet, value), x, y, hitboxWidth, hitboxHeight);
        this.value = value;
    }

    @Override
    public void update(Level level, long currentTime) {
        // An intentional noop
    }

    public int getValue() {
        return value;
    }
}
