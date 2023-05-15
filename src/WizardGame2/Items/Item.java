package WizardGame2.Items;

import WizardGame2.GameObjects.Player;

import java.awt.image.BufferedImage;

public abstract class Item implements Player.PositionObserver {
    private final String name;
    private final int id;
    private final BufferedImage sprite;
    private final int attackSpeed;

    private static final int cooldown = 330;

    private int timeUntilNextActivation;

    protected int playerX, playerY;
    protected double playerAngle;

    protected Item(String name, int id, BufferedImage sprite, int speed) {
        this.id = id;
        this.name = name;
        this.sprite = sprite;
        this.attackSpeed = speed;
        timeUntilNextActivation = 0;
    }

    public abstract void update(long currentTime, Player.Stats stats);

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    protected boolean hasCooldownPassed(double haste) {
        timeUntilNextActivation -= attackSpeed * haste;

        if (timeUntilNextActivation <= 0) {
            timeUntilNextActivation = cooldown;
            return true;
        }

        return false;
    }

    @Override
    public void notifyAboutNewPosition(int x, int y, double movementAngle) {
        playerX = x;
        playerY = y;
        playerAngle = movementAngle;
    }
}
