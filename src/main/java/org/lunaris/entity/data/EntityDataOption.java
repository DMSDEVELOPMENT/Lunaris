package org.lunaris.entity.data;

/**
 * Created by RINES on 14.09.17.
 */
public enum EntityDataOption {
    FLAGS,
    HEALTH, //int
    VARIANT, //int
    COLOUR, //byte
    NAMETAG, //string
    OWNER_ENTITY_ID, //long
    TARGET_ENTITY_ID, //long
    AIR, //short
    POTION_COLOR, //int (argb)
    POTION_AMBIENT, //byte
    JUMP_DURATION, //long
    HURT_TIME, //int
    HURT_DIRECTION, //int
    PADDLE_TIME_LEFT, //float
    PADDLE_TIME_RIGHT, //float
    EXPERIENCE_VALUE, //int
    MINECART_DISPLAY_BLOCK, //int (id | data << 16)
    MINECART_DISPLAY_OFFSET, //int
    MINECART_HAS_DISPLAY, //byte (1, чтобы показывать блок внутри)
    UNKNOWN19,
    UNKNOWN20,
    UNKNOWN21,
    UNKNOWN22,
    ENDERMAN_HELD_ITEM_ID, //short
    ENDERMAN_HELD_ITEM_DAMAGE, //short
    ENTITY_AGE, //short
    UNKNOWN26,
    UNKNOWN27,
    UNKNOWN28,
    UNKNoWN29,
    FIREBALL_POWER_X, //float
    FIREBALL_POWER_Y, //float
    FIREBALL_POWER_Z, //float
    UNKNoWN33,
    UNKNoWN34,
    UNKNoWN35,
    UNKNoWN36,
    UNKNoWN37,
    LEAD_HOLDER_ENTITY_ID, //long
    SCALE, //float
    INTERACTIVE_TAG, //string (button text)
    NPC_SKIN_ID, //string
    URL_TAG, //string
    MAX_AIR, //short
    MARK_VARIANT, //int
    UNKNOWN45,
    UNKNOWN46,
    UNKNOWN47,
    BLOCK_TARGET, //block coords (ender crystal)
    WITHER_INVULNERABLE_TICKS, //int
    WITHER_TARGET_1, //long
    WITHER_TARGET_2, //long
    WITHER_TARGET_3, //long
    UNKNOWN53, //short
    BOUNDING_BOX_WIDTH, //float
    BOUNDING_BOX_HEIGHT, //float
    FUSE_LENGTH, //int
    RIDER_SEAT_POSITION, //vector3f
    RIDER_ROTATION_LOCKED, //byte
    RIDER_MAX_ROTATION, //float
    RIDER_MIN_ROTATION, //float
    AREA_EFFECT_CLOUD_RADIUS, //float
    AREA_EFFECT_CLOUD_WAITING, //int
    AREA_EFFECT_CLOUD_PARTICLE_ID, //int


}
