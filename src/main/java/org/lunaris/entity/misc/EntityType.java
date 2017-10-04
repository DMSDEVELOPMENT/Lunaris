package org.lunaris.entity.misc;

/**
 * Created by RINES on 04.10.17.
 */
public enum EntityType {
    ITEM_DROP(64),
    CHICKEN(10),
    COW(11),
    PIG(12),
    SHEEP(13),
    WOLF(14),
    VILLAGER(15),
    MUSHROOM_COW(16),
    SQUID(17),
    RABBIT(18),
    BAT(19),
    IRON_GOLEM(20),
    SNOW_GOLEM(21),
    ZOMBIE(0x30B20),
    CREEPER(0x000B21),
    SKELETON(0x010B22),
    SPIDER(0x040B23),
    PIG_ZOMBIE(0x010B24),
    SLIME(0x000B25),
    ENDERMAN(0x000B26),
    SILVERFISH(0x040B27),
    CAVE_SPIDER(0x040B28),
    GHAST(0x000B29),
    LAVA_SLIME(0x000B2A),
    BLAZE(0x000B2B),
    ZOMBIE_VILLAGER(0x30B2C),
    WITCH(0x000B2D),
    PLAYER(0x00013F),
    FISHING_HOOK(0x00004D),
    ARROW(0x000050),
    SNOWBALL(0x000051),
    THROWN_EGG(0x000052),
    PAINTING(0x000053),
    MINECART_RIDEABLE(0x080054),
    LARGE_FIREBALL(0x000055),
    THROWN_POTION(0x000056),
    BOAT_RIDEABLE(0x00005A),
    LIGHTNING(0x00005D),
    SMALL_FIREBALL(0x00005E);

    private final int id;

    EntityType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
