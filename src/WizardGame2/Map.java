package WizardGame2;

import WizardGame2.GameObjects.Obstacle;

import java.awt.*;

public class Map {
    Obstacle[] obstacles;

    public Map(Obstacle[] obstacles) {
        this.obstacles = obstacles;
    }

    public Obstacle[] getObstacles() {
        return obstacles;
    }

    public void render(Graphics gfx) {
        Color oldColor = gfx.getColor();
        gfx.setColor(Color.RED);

        for (var rect : obstacles) {
            gfx.drawRect(rect.getX(), rect.getY(), rect.getHitboxWidth(), rect.getHitboxHeight());
        }

        gfx.setColor(oldColor);
    }
}
