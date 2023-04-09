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
    public static SpriteSheet characters;
    public static BufferedImage tempMap;

    public static ArrayList<MapData> maps;

    private static final String[] mapPaths = new String[]{"/levels/level1.json"};

    /*! \fn public static void Init()
        \brief Functia initializaza referintele catre elementele grafice utilizate.

        Aceasta functie poate fi rescrisa astfel incat elementele grafice incarcate/utilizate
        sa fie parametrizate. Din acest motiv referintele nu sunt finale.
     */
    public static void init() {
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
}
