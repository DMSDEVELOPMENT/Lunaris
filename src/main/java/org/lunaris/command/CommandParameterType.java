package org.lunaris.command;

/**
 * Created by RINES on 28.09.17.
 */
public enum CommandParameterType {
    STRING("string"),
    STRING_ENUM("stringenum"),
    BOOLEAN("bool"),
    PLAYER("target"),
    BLOCK_POSITION("blockpos"),
    RAW_TEXT("rawtext"),
    INT("int"),
    ITEM_TYPE("itemType"),
    BLOCK_TYPE("blockType"),
    COMMAND_NAME("commandName"),
    ENCHANTMENT("enchantmentType"),
    ENTITY_TYPE("entityType"),
    EFFECT_TYPE("effectType"),
    PARTICLE_TYPE("particleType");

    private final String value;

    CommandParameterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
