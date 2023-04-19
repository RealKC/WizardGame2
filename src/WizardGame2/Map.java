package WizardGame2;

import WizardGame2.GameObjects.Obstacle;
import WizardGame2.GameObjects.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Map {
    ArrayList<Obstacle> obstacles;

    BufferedImage texture;

    public static Map fromData(MapData mapData) {
        var obstacles = new ArrayList<Obstacle>();

        mapData.getObstacles().forEach((position, data) -> {
            obstacles.add(Obstacle.fromData(Assets.getInstance().getObstacles(), data, position.x, position.y));
        });

        return new Map(obstacles, mapData);
    }

    public Map(ArrayList<Obstacle> obstacles, MapData mapData) {
        this.obstacles = obstacles;
        this.texture = mapData.getTexture();
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getWidth() {
        return texture.getWidth();
    }

    public int getHeight() {
        return texture.getHeight();
    }

    public void render(Graphics gfx, Player.Camera camera) {
        int x = Utils.clamp(0, texture.getWidth() - camera.getCameraWidth(), camera.getX());
        int y = Utils.clamp(0, texture.getHeight() - camera.getCameraHeight(), camera.getY());
        var visibleMap = texture.getSubimage(x, y, camera.getCameraWidth(), camera.getCameraHeight());
        gfx.drawImage(visibleMap, 0, 0, null);

        Color oldColor = gfx.getColor();
        gfx.setColor(Color.RED);

        for (var obstacle : obstacles) {
            obstacle.render(gfx, camera.getX(), camera.getY());
        }

        gfx.setColor(oldColor);
    }
}
