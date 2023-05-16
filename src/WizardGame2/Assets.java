package WizardGame2;

import WizardGame2.Graphics.ImageLoader;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.ItemData;
import WizardGame2.Items.ItemFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets {
    private static Assets instance = null;

    private final SpriteSheet characters;
    private final SpriteSheet obstacles;
    private final SpriteSheet items;

    private final ArrayList<LevelData> levelDatas;
    private final ArrayList<ItemFactory> itemFactories;
    private final ArrayList<ItemFactory> passiveItemFactories;

    private Assets() {
        characters = new SpriteSheet(ImageLoader.loadImage("/textures/characters.png"));
        items = new SpriteSheet(ImageLoader.loadImage("/textures/items-spritesheet.png"));
        obstacles = new SpriteSheet(ImageLoader.loadImage("/textures/obstacles.png"));

        levelDatas = new ArrayList<>(3);
        itemFactories = new ArrayList<>(16);
        passiveItemFactories = new ArrayList<>(9);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EnemyDistribution.class, new EnemyDistribution.Deserializer());
        var gson = gsonBuilder.create();

        loadMaps(gson);
        loadItems(gson);
    }

    private void loadMaps(Gson gson) {
        iterateOverResourceFolder("/levels/", (path, reader) -> {
            LevelData.Raw rawLevelData = gson.fromJson(reader, LevelData.Raw.class);
            levelDatas.add(LevelData.fromRaw(rawLevelData));
        });
    }

    private void loadItems(Gson gson) {
        iterateOverResourceFolder("/active-items/", (path, reader) -> {
            var rawItemData = gson.fromJson(reader, ItemData.Raw.class);
            var itemData = ItemData.fromRaw(getItems(), rawItemData);

            try {
                var itemFactoryClass = Class.forName(rawItemData.getItemFactoryName());
                var itemFactory = (ItemFactory) itemFactoryClass.getDeclaredConstructor().newInstance();
                itemFactory.setItemData(itemData);
                itemFactories.add(itemFactory);
            } catch (Exception e) {
                System.out.printf("[ASSETS] Caught exception while processing '%s', %s: %s\n", path, e.getClass().getName(), e.getMessage());
            }
        });

        iterateOverResourceFolder("/passive-items/", (path, reader) -> {
            var rawItemData = gson.fromJson(reader, ItemData.Raw.class);
            var itemData = ItemData.fromRaw(getItems(), rawItemData);

            try {
                var itemFactoryClass = Class.forName(rawItemData.getItemFactoryName());
                var itemFactory = (ItemFactory) itemFactoryClass.getDeclaredConstructor().newInstance();
                itemFactory.setItemData(itemData);
                passiveItemFactories.add(itemFactory);
            } catch (Exception e) {
                System.out.printf("[ASSETS] Caught exception while processing '%s', %s: %s\n", path, e.getClass().getName(), e.getMessage());
            }
        });
    }

    /**
     * Iterates over every file in a resource folder and calls a callback for every one
     * @param folder the resource folder to be iterated
     * @param consumer the callback that will be called for each file
     */
    private static void iterateOverResourceFolder(String folder, BiConsumer<Path, Reader> consumer) {
        // Based on this StackOverflow answer: https://stackoverflow.com/a/67839914
        try {
            URI uri = Objects.requireNonNull(Assets.class.getResource(folder)).toURI();
            Path path;

            try {
                path = Paths.get(uri);
            } catch (FileSystemNotFoundException e) {
                // We are running inside a jar, not from IDEA
                var env = new HashMap<String, String>();
                try (var jarFS = FileSystems.newFileSystem(uri, env)) {
                    path = jarFS.getPath(folder);
                } catch (IOException ioException) {
                    e.printStackTrace();
                    return;
                }
            }

            try (var files = Files.list(path)) {
                files.forEach((filePath) -> {
                    try {
                        consumer.accept(filePath, Files.newBufferedReader(filePath));
                    } catch (IOException e) {
                        System.out.printf("[ASSETS] Encountered an exception while processing '%s': %s\n", filePath, e.getMessage());
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                System.out.printf("[ASSETS] Failed to list files in '%s' (not a path?): %s\n", path, e.getMessage());
                e.printStackTrace();
            }

        } catch (URISyntaxException e) {
            System.out.printf("[ASSETS] Got an invalid URI: %s\n", e.getMessage());
            e.printStackTrace();
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

    public ArrayList<ItemFactory> getPassiveItemFactories() {
        return passiveItemFactories;
    }

    public ArrayList<LevelData> getLevelDatas() {
        return levelDatas;
    }
}
