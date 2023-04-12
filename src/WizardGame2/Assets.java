package WizardGame2;

import WizardGame2.Graphics.ImageLoader;
import WizardGame2.Graphics.SpriteSheet;
import com.google.gson.Gson;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets {
    private static Assets instance = null;

    private SpriteSheet characters;
    private BufferedImage tempMap;

    private ArrayList<MapData> maps;

    private static final String[] mapPaths = new String[]{"/levels/level1.json"};

    private Assets() {
        characters = new SpriteSheet(ImageLoader.loadImage("/textures/characters.png"));
        tempMap = ImageLoader.loadImage("/textures/bigmap.png");

        maps = new ArrayList<>(mapPaths.length);

        var gson = new Gson();

        for (var path : mapPaths) {
            var filePath = Objects.requireNonNull(Assets.class.getResource(path)).getFile();
            try {
                var rawMapData = gson.fromJson(new FileReader(filePath), MapData.Raw.class);
                maps.add(MapData.fromRaw(rawMapData));
            } catch (FileNotFoundException e) {
                System.err.printf("File: '%s' not found!\n", filePath);
                e.printStackTrace();
            }
        }
    }

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }

        return instance;
    }

    public SpriteSheet getCharacters() {
        return characters;
    }

    public ArrayList<MapData> getMapDatas() {
        return maps;
    }

    public BufferedImage getTempMap() {
        return tempMap;
    }
}
