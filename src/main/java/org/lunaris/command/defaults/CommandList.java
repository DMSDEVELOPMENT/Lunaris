package org.lunaris.command.defaults;

import org.lunaris.LunarisServer;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by RINES on 16.09.17.
 */
public class CommandList extends Command {

    public CommandList() {
        super("list", LPermission.USER);
        setDescription("Display online players");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<LPlayer> players = LunarisServer.getInstance().getOnlinePlayers();
        sender.sendMessage("Players online: " + ChatColor.GREEN + "%d", players.size());
        if (!players.isEmpty())
            sender.sendMessage(players.stream().map(LPlayer::getName).collect(Collectors.joining(", ")));
    }

}
