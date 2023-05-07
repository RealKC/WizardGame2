package WizardGame2.Items;

import WizardGame2.Graphics.SpriteSheet;

import java.awt.image.BufferedImage;

public class ItemData {
    @SuppressWarnings("unused")
    public static class Raw {
        private String name;
        private float baseAttack;
        private float speed;
        private int x, y;
        private String itemFactoryName;

        public Raw() {
        }

        public String getItemFactoryName() {
            return itemFactoryName;
        }
    }

    public final BufferedImage sprite;
    public final float baseAttackDamage;
    public final float baseAttackSpeed;
    public final String name;

    public static ItemData fromRaw(SpriteSheet spriteSheet, Raw raw) {
        return new ItemData(spriteSheet.crop(raw.x, raw.y), raw.baseAttack, raw.speed, raw.name);
    }

    private ItemData(BufferedImage sprite, float baseAttackDamage, float baseAttackSpeed, String name) {
        this.sprite = sprite;
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.name = name;
    }
}
