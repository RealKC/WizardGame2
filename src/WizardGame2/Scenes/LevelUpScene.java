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
                switch (this) {
                    case HP:
                        return 15;
                    case ATK:
                        return 5;
                    case CRIT:
                        return 0.5;
                    case SPD:
                        return 1;
                    case PCK:
                        return 10;
                    case HST:
                        return 0.25;
                }

                return 0;
            }
        }

        Stat stat;
        int textWidth = -1;
        String text;
        Player.Stats playerStats;

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
                case HP:
                    playerStats.increaseMaxHP(stat.value());
                    break;
                case ATK:
                    playerStats.increaseMagicPower(stat.value());
                    break;
                case CRIT:
                    playerStats.increaseCritChance(stat.value());
                    break;
                case SPD:
                    playerStats.increaseSpeedBost(stat.value());
                    break;
                case PCK:
                    playerStats.increasePickupRange((int) stat.value());
                    break;
                case HST:
                    playerStats.increaseHaste(stat.value());
                    break;
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
        final int buttonWidth = 350;

        int yOffset = 150;

        var player = levelScene.getPlayer();

        for (int i = 0; i < buttons.length; ++i) {
            var bounds = new Rectangle(centerX - buttonWidth / 2, yOffset, buttonWidth, 38);

            var grantStat = (!player.canPickUpMoreActiveItems() && !player.canPickUpMorePassiveItems())
                    || (random.nextInt() % 2 == 0);

            if (grantStat) {
                buttons[i] = new StatIncreaseButton(bounds, player.getStats());
            } else {
                boolean active = random.nextBoolean();

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
