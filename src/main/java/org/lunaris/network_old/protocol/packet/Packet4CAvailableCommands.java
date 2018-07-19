package org.lunaris.network_old.protocol.packet;

import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xtrafrancyz
 */
public class Packet4CAvailableCommands extends MinePacket {
    /**
     * This flag is set on all types EXCEPT the TEMPLATE type. Not completely sure what this is for, but it is required
     * for the argtype to work correctly. VALID seems as good a name as any.
     */
    private static final int ARG_FLAG_VALID = 0x100000;

    /**
     * Basic parameter types. These must be combined with the ARG_FLAG_VALID constant.
     * ARG_FLAG_VALID | (type const)
     */
    private static final int ARG_TYPE_INT = 0x01;
    private static final int ARG_TYPE_FLOAT = 0x02;
    private static final int ARG_TYPE_VALUE = 0x03;
    private static final int ARG_TYPE_TARGET = 0x04;

    private static final int ARG_TYPE_STRING = 0x0d;
    private static final int ARG_TYPE_POSITION = 0x0e;

    private static final int ARG_TYPE_RAWTEXT = 0x11;
    private static final int ARG_TYPE_TEXT = 0x13;

    private static final int ARG_TYPE_JSON = 0x16;
    private static final int ARG_TYPE_COMMAND = 0x1d;

    /**
     * Enums are a little different: they are composed as follows:
     * ARG_FLAG_ENUM | ARG_FLAG_VALID | (enum index)
     */
    private static final int ARG_FLAG_ENUM = 0x200000;

    private static final int ARG_FLAG_TEMPLATE = 0x01000000;

    /**
     * This is used for /xp <level: int>L.
     */
    private static final int ARG_FLAG_POSTFIX = 0x1000000;

    // Enums are stored in an indexed list at the start. Enums are just collections of a name and
    // a integer list reflecting the index inside enumValues
    private List<String> enumValues = new ArrayList<>();
    private Map<String, List<Integer>> enums = new LinkedHashMap<>(16, 0.75f, false); // write order
    private Map<String, Integer> enumIndexes = new HashMap<>();
    private List<Command> commands;

    public Packet4CAvailableCommands(List<Command> commands) {
        this.commands = commands;
        
        // Aliases is enums
        for (Command command : commands) {
            if (!command.getAliases().isEmpty()) {
                int index = -1;
                for (String alias : command.getAliases())
                    index = this.addEnum(command.getName() + "@alias", alias);
                this.enumIndexes.put(command.getName() + "@alias", index);
            }
        }

        // Enums in parameters
        for (Command command : commands) {
            for (CommandParameter[] parameters : command.getParametersVariants()) {
                for (CommandParameter parameter : parameters) {
                    if (parameter.enumValues != null) {
                        int index = -1;
                        for (String s : parameter.enumValues)
                            index = this.addEnum(parameter.name, s);
                        this.enumIndexes.put(command.getName() + "#" + parameter.name, index);
                    }
                }
            }
        }
    }

    @Override
    public int getId() {
        return 0x4c;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        // enum values
        buffer.writeUnsignedVarInt(enumValues.size());
        for (String value : enumValues)
            buffer.writeString(value);

        // postfix data # unknown
        buffer.writeUnsignedVarInt(0);

        // enum index data
        buffer.writeUnsignedVarInt(enums.size());
        for (Map.Entry<String, List<Integer>> entry : this.enums.entrySet()) {
            buffer.writeString(entry.getKey());
            buffer.writeUnsignedVarInt(entry.getValue().size());
            for (Integer enumValueIndex : entry.getValue())
                writeEnumIndex(enumValueIndex, buffer);
        }

        // Command data
        buffer.writeUnsignedVarInt(commands.size());
        for (Command command : commands) {
            // Command meta
            buffer.writeString(command.getName());
            buffer.writeString(command.getDescription());

            // Flags
            buffer.writeByte((byte) 0);
            buffer.writeByte((byte) CommandPermission.NORMAL.ordinal());

            if (command.getAliases().isEmpty())
                buffer.writeUnsignedInt(-1);
            else
                buffer.writeUnsignedInt(this.enumIndexes.get(command.getName() + "@alias"));

            buffer.writeUnsignedVarInt(command.getParametersVariants().size());
            for (CommandParameter[] parameters : command.getParametersVariants()) {
                buffer.writeUnsignedVarInt(parameters.length);
                for (CommandParameter parameter : parameters) {
                    buffer.writeString(parameter.name);
                    buffer.writeUnsignedInt(getFlag(command, parameter));
                    buffer.writeBoolean(parameter.optional);
                }
            }
        }
    }

    private int addEnum(String name, String value) {
        // Check if we already know this enum value
        int enumValueIndex;
        if (this.enumValues.contains(value)) {
            enumValueIndex = this.enumValues.indexOf(value);
        } else {
            this.enumValues.add(value);
            enumValueIndex = this.enumValues.size() - 1;
        }

        // Create / add this value to the enum
        this.enums.computeIfAbsent(name, k -> new ArrayList<>()).add(enumValueIndex);
        return enums.size() - 1;
    }

    private void writeEnumIndex(int enumValueIndex, MineBuffer buffer) {
        if (this.enumValues.size() < 256)
            buffer.writeByte((byte) enumValueIndex);
        else if (this.enumValues.size() < 65536)
            buffer.writeUnsignedShort((short) enumValueIndex);
        else
            buffer.writeUnsignedInt(enumValueIndex);
    }

    private int getFlag(Command command, CommandParameter parameter) {
        int paramType = ARG_FLAG_VALID; // We don't support postfixes yet
        switch (parameter.type) {
            case INT:
                paramType |= ARG_TYPE_INT;
                break;
            case BOOLEAN:
            case STRING_ENUM:
                paramType |= ARG_FLAG_ENUM;
                paramType |= this.enumIndexes.get(command.getName() + "#" + parameter.name);
                break;
            case PLAYER:
                paramType |= ARG_TYPE_TARGET;
                break;
            case STRING:
                paramType |= ARG_TYPE_STRING;
                break;
            case BLOCK_POSITION:
                paramType |= ARG_TYPE_POSITION;
                break;
            case RAW_TEXT:
                paramType |= ARG_TYPE_TEXT;
                break;
            default:
                paramType |= ARG_TYPE_VALUE;
        }
        return paramType;
    }

    public enum CommandPermission {
        NORMAL,
        OPERATOR,
        HOST,
        AUTOMATION,
        ADMIN
    }
}
