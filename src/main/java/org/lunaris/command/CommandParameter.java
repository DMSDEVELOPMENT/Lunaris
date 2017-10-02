package org.lunaris.command;

/**
 * Created by RINES on 15.09.17.
 */
public class CommandParameter {

    public String name;
    public CommandParameterType type;
    public boolean optional = false;
    public String[] enumValues;

    public CommandParameter(String name) {
        this(name, CommandParameterType.STRING);
    }

    public CommandParameter(String name, CommandParameterType type) {
        this.name = name;
        this.type = type;
    }

    public CommandParameter(String name, String[] enumValues) {
        this.name = name;
        this.type = CommandParameterType.STRING_ENUM;
        this.enumValues = enumValues;
    }

    public CommandParameter optional() {
        this.optional = true;
        return this;
    }
}
