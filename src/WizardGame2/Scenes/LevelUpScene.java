package WizardGame2.Scenes;

import WizardGame2.Assets;
import WizardGame2.Game;
import WizardGame2.GameObjects.Player;
import WizardGame2.Items.ItemFactory;
import WizardGame2.Utils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;

public class LevelUpScene implements Scene {
    private final LevelScene levelScene;

    private final Random random = new Random();
    private final DecimalFormat format = new DecimalFormat("0.##");

    private SceneUpdate sceneUpdate = SceneUpdate.STAY;

    private class StatIncreaseButton extends Button {
        enum Stat {
            HP,
            ATK,
            CRIT,
            SPD,
            PCK,
            HST;

            double value() {
                return switch (this) {
                    case HP -> 15;
                    case ATK -> 5;
                    case CRIT -> 0.5;
                    case SPD -> 1;
                    case PCK -> 10;
                    case HST -> 0.25;
                };

            }
        }

        private final Stat stat;
        int textWidth = -1;
        private final String text;
        private final Player.Stats playerStats;

        public StatIncreaseButton(Rectangle bounds, Player.Stats stats) {
            super(bounds);

            this.stat = Stat.values()[Math.abs(random.nextInt() % Stat.values().length)];
            this.text = String.format("Increase %s by %s", stat, format.format(stat.value()));
            this.playerStats = stats;
        }

        @Override
        public void render(Graphics gfx) {
            var oldColor = gfx.getColor();
            gfx.setColor(Color.BLACK);
            gfx.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            gfx.setColor(buttonColor);
            gfx.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            if (textWidth < 0) {
                var fontMetrics = gfx.getFontMetrics();
                var stringBounds = fontMetrics.getStringBounds(text, gfx);
                textWidth = (int) stringBounds.getWidth();
            }

            Utils.drawTextWithOutline(gfx, text, bounds.x + bounds.width / 2 - textWidth / 2, bounds.y + bounds.height - 5);

            gfx.setColor(oldColor);
        }

        @Override
        void onClicked() {
            sceneUpdate = SceneUpdate.NEXT_SCENE;
            switch (stat) {
                case HP -> playerStats.increaseMaxHP(stat.value());
                case ATK -> playerStats.increaseMagicPower(stat.value());
                case CRIT -> playerStats.increaseCritChance(stat.value());
                case SPD -> playerStats.increaseSpeedBost(stat.value());
                case PCK -> playerStats.increasePickupRange((int) stat.value());
                case HST -> playerStats.increaseHaste(stat.value());
            }
        }
    }

    private final HashSet<ItemFactory> pickedItemFactories = new HashSet<>();

    private class ItemGrantingButton extends Button {
        final ItemFactory itemFactory;

        final Player player;

        final String text;

        int textWidth = -1;

        private final boolean isActiveItem;

        public ItemGrantingButton(Rectangle bounds, Player player, ItemFactory itemFactory, boolean isActiveItem) {
            super(bounds);

            this.player = player;
            this.itemFactory = itemFactory;
            this.text = itemFactory.getName();
            this.isActiveItem = isActiveItem;
        }

        @Override
        public void render(Graphics gfx) {
            var oldColor = gfx.getColor();
            gfx.setColor(Color.BLACK);
            gfx.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            gfx.setColor(buttonColor);
            gfx.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

            gfx.drawImage(itemFactory.getItemData().sprite, bounds.x + 4, bounds.y + 4, null);

            if (textWidth < 0) {
                var fontMetrics = gfx.getFontMetrics();
                var stringBounds = fontMetrics.getStringBounds(text, gfx);
                textWidth = (int) stringBounds.getWidth();
            }

            Utils.drawTextWithOutline(gfx, text,
                    bounds.x + 4 + itemFactory.getItemData().sprite.getWidth() + bounds.width / 2 - textWidth / 2,
                    bounds.y + bounds.height - 5);

            gfx.setColor(oldColor);
        }

        @Override
        void onClicked() {
            if (isActiveItem) {
                player.addActiveItem(itemFactory.makeItem());
            } else {
                player.addPassiveItem(itemFactory.makeItem());
            }

            sceneUpdate = SceneUpdate.NEXT_SCENE;
        }
    }

    private final Button[] buttons;
    private int textWidth = -1;
    private final int centerX;
    private final int canvasWidth, canvasHeight;

    private static final Color DARKEN = new Color(0, 0, 0, 128);

    public LevelUpScene(LevelScene levelScene) {
        this.levelScene = levelScene;
        levelScene.setPaused(true);

        buttons = new Button[4];

        centerX = Game.getInstance().getWindowWidth() / 2;
        canvasWidth = Game.getInstance().getWindowWidth();
        canvasHeight = Game.getInstance().getWindowHeight();
        final int buttonWidth = 400;

        int yOffset = 150;

        var player = levelScene.getPlayer();

        for (int i = 0; i < buttons.length; ++i) {
            var bounds = new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38);

            boolean grantStat = false;
            boolean active = random.nextBoolean();

            if (!player.canPickUpMoreActiveItems()) {
                if (!player.canPickUpMorePassiveItems()) {
                    grantStat = true;
                } else {
                    active = false;
                }
            } else if (!player.canPickUpMorePassiveItems()) {
                active = true;
            }

            if (grantStat) {
                buttons[i] = new StatIncreaseButton(bounds, player.getStats());
            } else {
                var item = pickItem(player, active);

                if (item != null) {
                    buttons[i] = new ItemGrantingButton(bounds, player, item, active);
                } else {
                    buttons[i] = new StatIncreaseButton(bounds, player.getStats());
                }
            }

            yOffset += 50;
        }
    }

    ItemFactory pickItem(Player player, boolean active) {
        ItemFactory itemFactory;

        var assets = Assets.getInstance();
        var itemFactories = active ? assets.getItemFactories() : assets.getPassiveItemFactories();

        int attemptCount = 0;
        final int maxAttempts = 16;

        do {
            if (attemptCount >= maxAttempts) {
                return null;
            }

            do {
                int idx = Math.abs(random.nextInt() % itemFactories.size());
                itemFactory = itemFactories.get(idx);
                attemptCount++;

                if (attemptCount >= maxAttempts) {
                    break;
                }
            } while (pickedItemFactories.contains(itemFactory));

            attemptCount++;
            pickedItemFactories.add(itemFactory);
        } while (player.hasItem(itemFactory));

        return itemFactory;
    }

    @Override
    public SceneUpdate update(long currentTime) {
        return sceneUpdate;
    }

    @Override
    public void render(Graphics gfx) {
        levelScene.render(gfx);

        var oldColor = gfx.getColor();
        gfx.setColor(DARKEN);
        gfx.fillRect(0, 0, canvasWidth, canvasHeight);
        gfx.setColor(oldColor);

        String text = "LEVEL UP!";

        if (textWidth < 0) {
            var fontMetrics = gfx.getFontMetrics();
            var stringBounds = fontMetrics.getStringBounds(text, gfx);
            textWidth = (int) stringBounds.getWidth();
        }

        Utils.drawTextWithOutline(gfx, text, centerX - textWidth / 2, 100);

        for (var button : buttons) {
            button.render(gfx);
        }
    }

    @Override
    public Scene nextScene() {
        for (var button : buttons) {
            button.unregisterListeners();
        }

        levelScene.setPaused(false);
        return levelScene;
    }
}
