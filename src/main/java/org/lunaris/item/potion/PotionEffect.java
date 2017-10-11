package org.lunaris.item.potion;

/**
 * Created by RINES on 11.10.17.
 */
public class PotionEffect {

    private PotionEffectType type;
    private int level;
    private boolean splash;

    public PotionEffect(PotionEffectType type, int level, boolean splash) {
        this.type = type;
        this.level = level;
        this.splash = splash;
    }

    public PotionEffect(PotionEffectType type) {
        this(type, 1, false);
    }

    public PotionEffect type(PotionEffectType type) {
        this.type = type;
        return this;
    }

    public PotionEffect level(int level) {
        this.level = level;
        return this;
    }

    public PotionEffect splash(boolean splash) {
        this.splash = splash;
        return this;
    }

    public PotionEffectType getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isSplash() {
        return this.splash;
    }

}
