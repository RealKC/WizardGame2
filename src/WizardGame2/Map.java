package WizardGame2;

import WizardGame2.GameObjects.Obstacle;
import WizardGame2.GameObjects.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Map {
    Obstacle[] obstacles;

    BufferedImage texture;

    public Map(Obstacle[] obstacles, MapData mapData) {
        this.obstacles = obstacles;
        this.texture = mapData.getTexture();
    }

    public Obstacle[] getObstacles() {
        return obstacles;
    }



    public void render(Graphics gfx, Player.Camera camera) {
        int x = Utils.clamp(0, texture.getWidth() - camera.getWidth(), camera.getX());
        int y = Utils.clamp(0, texture.getHeight() - camera.getHeight(), camera.getY());
        var visibleMap = texture.getSubimage(x, y, camera.getWidth(), camera.getHeight());
        gfx.drawImage(visibleMap, 0, 0, null);

        Color oldColor = gfx.getColor();
        gfx.setColor(Color.RED);

        for (var rect : obstacles) {
            gfx.drawRect(rect.getX(), rect.getY(), rect.getHitboxWidth(), rect.getHitboxHeight());
        }

        gfx.setColor(oldColor);
    }
}
