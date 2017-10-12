package org.lunaris.command;

import org.lunaris.LunarisServer;
import org.lunaris.entity.misc.LPermission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 15.09.17.
 */
public abstract class Command {

    private final String name;
    private String description = "";
    private final List<String> aliases;
    private final List<CommandParameter[]> parameters = new ArrayList<>();
    private final LPermission requiredPermission;

    public Command(String name, LPermission requiredPermission) {
        this(name, null, requiredPermission);
    }

    public Command(String name, List<String> aliases, LPermission requiredPermission) {
        this.name = name.toLowerCase();
        if (aliases == null) {
            this.aliases = Collections.singletonList(name);
        } else {
            this.aliases = new ArrayList<>(aliases);
            this.aliases.add(name);
        }
        this.requiredPermission = requiredPermission;
        LunarisServer.getInstance().getCommandManager().register(this);
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void addParametersVariant(CommandParameter... parameters) {
        this.parameters.add(parameters);
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<CommandParameter[]> getParametersVariants() {
        return parameters;
    }

    public LPermission getRequiredPermission() {
        return requiredPermission;
    }

}
