package org.lunaris.command;

/**
 * Created by RINES on 15.09.17.
 */
public class CommandParameter {

    public String name;
    public CommandParameterType type;
    public boolean optional;

    public String enum_type;
    public String[] enum_values;

    public CommandParameter(String name, CommandParameterType type, boolean optional) {
        this.name = name;
        this.type = type;
        this.optional = optional;
    }

    public CommandParameter(String name, boolean optional) {
        this(name, CommandParameterType.RAW_TEXT, optional);
    }

    public CommandParameter(String name) {
        this(name, false);
    }

    public CommandParameter(String name, boolean optional, String enumType) {
        this.name = name;
        this.type = CommandParameterType.STRING_ENUM;
        this.optional = optional;
        this.enum_type = enumType;
    }

    public CommandParameter(String name, boolean optional, String[] enumValues) {
        this.name = name;
        this.type = CommandParameterType.STRING_ENUM;
        this.optional = optional;
        this.enum_values = enumValues;
    }

    public CommandParameter(String name, String enumType){
        this(name, false, enumType);
    }

    public CommandParameter(String name, String[] enumValues){
        this(name, false, enumValues);
    }

}
