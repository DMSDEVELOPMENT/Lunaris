package org.lunaris.command;

import org.lunaris.command.defaults.*;
import org.lunaris.util.exception.CommandExecutionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 15.09.17.
 */
public class CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final CommandSender consoleSender = new ConsoleSender();

    public void registerDefaults() {
        new Stop();
        new Version();
        new List();
        new Say();
        new Timings();
    }

    public boolean isCommand(String line) {
        return line != null && !line.isEmpty() && line.charAt(0) == '/';
    }

    public void handle(String line, CommandSender sender) {
        if(sender == null)
            sender = consoleSender;
        String[] split = line.split(" ");
        String[] args = new String[0];
        if(split.length > 0) {
            args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);
        }
        Command command = this.commands.get(split[0]);
        if(command == null)
            sender.sendMessage("Unknown command. Type /help to see list of them.");
        else {
            if(!sender.hasPermission(command.getRequiredPermission())) {
                sender.sendMessage("&cYou don't have permissions to execute this command.");
                return;
            }
            try {
                command.execute(sender, args);
            } catch (Exception ex) {
                sender.sendMessage("&cAn internal error occurred whilst executing your command.");
                new CommandExecutionException(ex).printStackTrace();
            }
        }
    }

    public CommandSender getConsoleSender() {
        return this.consoleSender;
    }

    void register(Command command) {
        this.commands.put('/' + command.getName(), command);
        command.getAliases().forEach(alias -> this.commands.put('/' + alias, command));
    }

}
