package WizardGame2;

import WizardGame2.GameObjects.Player;
import WizardGame2.Graphics.ImageLoader;
import WizardGame2.Graphics.SpriteSheet;
import WizardGame2.Items.ItemData;
import WizardGame2.Items.ItemFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

/*! \class public class Assets
    \brief Clasa incarca fiecare element grafic necesar jocului.

    Game assets include tot ce este folosit intr-un joc: imagini, sunete, harti etc.
 */
public class Assets {
    private static Assets instance = null;

    private final BufferedImage mainMenuBackground;
    private final BufferedImage levelSelectBackground;
    private final BufferedImage characterSelectBackground;
    private final BufferedImage innerMagicCircle;
    private final BufferedImage outerMagicCircle;

    private final SpriteSheet characters;
    private final SpriteSheet obstacles;
    private final SpriteSheet items;

    private final ArrayList<LevelData> levelDatas;
    private final ArrayList<ItemFactory> itemFactories;
    private final ArrayList<ItemFactory> passiveItemFactories;
    private final ArrayList<Player.Data> characterStats;

    private Assets() {
        mainMenuBackground = ImageLoader.loadImage("/textures/main-menu-background.png");
        levelSelectBackground = ImageLoader.loadImage("/textures/level-select-background.png");
        innerMagicCircle = ImageLoader.loadImage("/textures/inner-magic-circle.png");
        outerMagicCircle = ImageLoader.loadImage("/textures/outer-magic-circle.png");
        characterSelectBackground = ImageLoader.loadImage("/textures/character-select-background.png");

        characters = new SpriteSheet(ImageLoader.loadImage("/textures/characters.png"));
        items = new SpriteSheet(ImageLoader.loadImage("/textures/items-spritesheet.png"));
        obstacles = new SpriteSheet(ImageLoader.loadImage("/textures/obstacles.png"));

        levelDatas = new ArrayList<>(3);
        itemFactories = new ArrayList<>(16);
        passiveItemFactories = new ArrayList<>(9);
        characterStats = new ArrayList<>(3);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(EnemyDistribution.class, new EnemyDistribution.Deserializer());
        var gson = gsonBuilder.create();

        loadMaps(gson);
        loadItems(gson);
        loadCharacters(gson);
    }

    private void loadCharacters(Gson gson) {
        iterateOverResourceFolder("/characters/", (path, reader) -> {
            Player.Data characterData = gson.fromJson(reader, Player.Data.class);
            characterStats.add(characterData);
        });

        characterStats.sort(Comparator.comparingInt(Player.Data::getId));
    }

    private void loadMaps(Gson gson) {
        iterateOverResourceFolder("/levels/", (path, reader) -> {
            LevelData.Raw rawLevelData = gson.fromJson(reader, LevelData.Raw.class);
            levelDatas.add(LevelData.fromRaw(rawLevelData));
        });

        levelDatas.sort(Comparator.comparingInt(LevelData::getId));
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
                Utils.logException(getClass(), e, "(active items): failed to process: '%s'", path);
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
                Utils.logException(getClass(), e, "(passive items): failed to process: '%s'", path);
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
            URI uri = Objects.requireNonNull(Assets.class.getResource(folder), "folder was not in the resource folder").toURI();
            Path path;
            FileSystem jarFS = null;

            try {
                path = Paths.get(uri);
            } catch (FileSystemNotFoundException e) {
                // We are running inside a jar, not from IDEA
                var env = new HashMap<String, String>();
                try {
                    jarFS = FileSystems.newFileSystem(uri, env);
                    path = jarFS.getPath(folder);
                } catch (IOException ioe) {
                    Utils.logException(Assets.class, ioe, "got an IO exception trying to get the path from inside the jar filesystem");
                    return;
                }
            }

            try (var files = Files.list(path)) {
                files.forEach((filePath) -> {
                    try {
                        consumer.accept(filePath, Files.newBufferedReader(filePath));
                    } catch (IOException e) {
                        Utils.logException(Assets.class, e, "encountered an IO Exception while processing '%s'", filePath);
                    }
                });
            } catch (IOException e) {
                Utils.logException(Assets.class, e, "failed to list files in '%s' (not a path?)", path);
            }

            if (jarFS != null) {
                try {
                    jarFS.close();
                } catch (IOException e) {
                    Utils.logException(Assets.class, e, "failed to close jar filesystem");
                }
            }
        } catch (URISyntaxException e) {
            Utils.logException(Assets.class, e, "got an invalid URI from folder '%s'", folder);
        } catch (NullPointerException e) {
            Utils.logException(Assets.class, e, "'%s'", folder);
        }
    }

    public static Assets getInstance() {
        if (instance == null) {
            instance = new Assets();
        }

        return instance;
    }

    public BufferedImage getMainMenuBackground() {
        return mainMenuBackground;
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

    public ArrayList<Player.Data> getCharacterStats() {
        return characterStats;
    }

    public BufferedImage getLevelSelectBackground() {
        return levelSelectBackground;
    }

    public BufferedImage getInnerMagicCircle() {
        return innerMagicCircle;
    }

    public BufferedImage getOuterMagicCircle() {
        return outerMagicCircle;
    }

    public BufferedImage getCharacterSelectBackground() {
        return characterSelectBackground;
    }
}
