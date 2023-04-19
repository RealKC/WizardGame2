package WizardGame2;

import WizardGame2.Graphics.ImageLoader;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.ItemData;
import WizardGame2.Items.ItemFactory;
import com.google.gson.Gson;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets {
    private static Assets instance = null;

    private SpriteSheet characters;
    private SpriteSheet obstacles;
    private SpriteSheet items;

    private ArrayList<MapData> maps;
    private ArrayList<ItemFactory> itemFactories;

    private static final String[] mapPaths = new String[]{"/levels/level1.json"};
    private static final String[] itemPaths = new String[]{
            "/active-items/pistol-carpati.json",
            "/active-items/defendere-magi.json",
    };

    private Assets() {
        characters = new SpriteSheet(ImageLoader.loadImage("/textures/characters.png"));
        items = new SpriteSheet(ImageLoader.loadImage("/textures/items-spritesheet.png"));
        obstacles = new SpriteSheet(ImageLoader.loadImage("/textures/obstacles.png"));

        maps = new ArrayList<>(mapPaths.length);
        itemFactories = new ArrayList<>(16);

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

        loadItems(gson);
    }

    private void loadItems(Gson gson) {
        for (var path : itemPaths) {
            try (var resource = Assets.class.getResourceAsStream(path)) {
                var rawItemData = gson.fromJson(new InputStreamReader(Objects.requireNonNull(resource)), ItemData.Raw.class);
                var itemData = ItemData.fromRaw(getItems(), rawItemData);

                var itemFactoryClass = Class.forName(rawItemData.getItemFactoryName());
                var itemFactory = (ItemFactory) itemFactoryClass.getDeclaredConstructor().newInstance();
                itemFactory.setItemData(itemData);
                itemFactories.add(itemFactory);
            } catch (Exception e) {
                System.out.printf("[ASSETS] Caught exception while processing '%s', %s: %s\n", path, e.getClass().getName(), e.getMessage());
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

    public SpriteSheet getObstacles() {
        return obstacles;
    }

    public SpriteSheet getItems() {
        return items;
    }

    public ArrayList<ItemFactory> getItemFactories() {
        return itemFactories;
    }

    public ArrayList<MapData> getMapDatas() {
        return maps;
    }
}
