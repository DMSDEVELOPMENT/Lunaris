package org.lunaris.command;

import org.lunaris.command.defaults.*;
import org.lunaris.util.exception.CommandExecutionException;
import org.lunaris.util.logger.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RINES on 15.09.17.
 */
public class CommandManager {

    private final List<Command> commands = new ArrayList<>();
    private final Map<String, Command> aliases = new HashMap<>();
    private final CommandSender consoleSender = new ConsoleSender();

    public void registerDefaults() {
        new CommandStop();
        new CommandVersion();
        new CommandList();
        new CommandSay();
        new CommandTimings();
        new CommandTest();
        new CommandOp();
        new CommandGive();
        new CommandTPS();
    }

    public boolean isCommand(String line) {
        return line != null && !line.isEmpty() && line.charAt(0) == '/';
    }

    public void handle(String line, CommandSender sender) {
        if (sender == null)
            sender = consoleSender;
        String[] split = line.split(" ");
        String[] args = new String[0];
        if (split.length > 0) {
            args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);
        }
        Command command = this.aliases.get(split[0]);
        if (command == null)
            sender.sendMessage("Unknown command. Type /help to see list of them.");
        else {
            if (!sender.hasPermission(command.getRequiredPermission())) {
                sender.sendMessage(ChatColor.colored("&cYou don't have permissions to execute this command."));
                return;
            }
            try {
                command.execute(sender, args);
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.colored("&cAn internal error occurred whilst executing your command."));
                new CommandExecutionException(ex).printStackTrace();
            }
        }
    }

    public List<Command> getAvailableCommands(CommandSender sender) {
        List<Command> available = new ArrayList<>();
        for (Command command : commands)
            if (sender.hasPermission(command.getRequiredPermission()))
                available.add(command);
        return available;
    }

    public CommandSender getConsoleSender() {
        return this.consoleSender;
    }

    void register(Command command) {
        this.commands.add(command);
        this.aliases.put('/' + command.getName(), command);
        command.getAliases().forEach(alias -> this.aliases.put('/' + alias, command));
    }

}
