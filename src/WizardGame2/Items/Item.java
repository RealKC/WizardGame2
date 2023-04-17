package WizardGame2.Items;

import java.awt.image.BufferedImage;

public abstract class Item {
    private final String name;
    private final int id;
    private final BufferedImage sprite;
    private final int attackSpeed;

    private static final int cooldown = 1000;

    private int timeUntilNextActivation;

    protected Item(String name, int id, BufferedImage sprite, int speed) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.attackSpeed = speed;
        timeUntilNextActivation = 0;
    }

    abstract void update(long currentTime);

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    protected boolean hasCooldownPassed() {
        timeUntilNextActivation -= attackSpeed;

        if (timeUntilNextActivation <= 0) {
            timeUntilNextActivation = cooldown;
            return true;
        }

        return false;
    }
}
