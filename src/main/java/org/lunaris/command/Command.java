package org.lunaris.command;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.LPermission;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by RINES on 15.09.17.
 */
public abstract class Command {

    private final String name;
    private final List<String> aliases;
    private final CommandParameter[] parameters;
    private final LPermission requiredPermission;

    public Command(String name, LPermission requiredPermission, CommandParameter... parameters) {
        this(name, null, requiredPermission, parameters);
    }

    public Command(String name, List<String> aliases, LPermission requiredPermission, CommandParameter... parameters) {
        this.name = name.toLowerCase();
        this.aliases = aliases == null ? Collections.emptyList() : aliases.stream().map(String::toLowerCase).collect(Collectors.toList());
        this.requiredPermission = requiredPermission;
        this.parameters = parameters;
        Lunaris.getInstance().getCommandManager().register(this);
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public CommandParameter[] getParameters() {
        return parameters;
    }

    public LPermission getRequiredPermission() {
        return requiredPermission;
    }

}
