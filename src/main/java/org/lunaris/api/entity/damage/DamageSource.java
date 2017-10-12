package org.lunaris.api.entity.damage;

/**
 * Created by RINES on 11.10.17.
 */
public enum DamageSource {

    IN_FIRE(false, false, true, false, false),
    LIGHTNING_BOLT(false, false, false, false, false),
    ON_FIRE(true, false, true, false, false),
    LAVA(false, false, true, false, false),
    IN_WALL(true, false, false, false, false),
    DROWN(true, false, false, false, false),
    STARVE(true, true, false, false, false),
    CACTI(false, false, false, false, false),
    FALL(true, false, false, false, false),
    VOID(true, true, false, false, true),
    UNKNOWN(true, false, false, false, false),
    ENTITY_ATTACK(false, false, false, false, false),
    MAGIC(true, false, false, true, false),
    ANVIL(false, false, false, false, false),
    FALLING_BLOCK(false, false, false, false, false);

    /**
     * Игнорирует ли этот тип урона броню.
     */
    private final boolean bypassesArmor;

    /**
     * Является ли тип урона чистым (то есть игнорирует ли уменьшение урона от эффектов и зачарований)
     */
    private final boolean pure;

    private final boolean fireBased;

    private final boolean magicBased;

    private final boolean bypassesCreative;

    DamageSource(boolean bypassesArmor, boolean pure, boolean fireBased,
                 boolean magicBased, boolean bypassesCreative) {
        this.bypassesArmor = bypassesArmor;
        this.pure = pure;
        this.fireBased = fireBased;
        this.magicBased = magicBased;
        this.bypassesCreative = bypassesCreative;
    }

    /**
     * Check whether this damage source ignores armor.
     * @return if this damage source ignores armor.
     */
    public boolean isBypassesArmor() {
        return this.bypassesArmor;
    }

    /**
     * Check whether this damage source ignores potion effects & enchantments resistance.
     * @return if this damage source ignores potion effects & enchantments resistance.
     */
    public boolean isPure() {
        return this.pure;
    }

    /**
     * Check whether this damage source is fire-based.
     * @return if this damage source is related to fire in any way.
     */
    public boolean isFireBased() {
        return this.fireBased;
    }

    /**
     * Check whether this damage source if magic-based.
     * @return if this damage source if magic-based.
     */
    public boolean isMagicBased() {
        return this.magicBased;
    }

    /**
     * Check whether players in creative gamemode will still take damage of this source type.
     * @return if players in creative gamemode will still take damage of this source type.
     */
    public boolean isBypassesCreative() {
        return this.bypassesCreative;
    }

}
