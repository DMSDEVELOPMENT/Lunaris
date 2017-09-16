package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.LPermission;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by RINES on 16.09.17.
 */
public class List extends Command {

    public List() {
        super("list", LPermission.USER);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Collection<Player> players = Lunaris.getInstance().getOnlinePlayers();
        sender.sendMessage("Players online: %d", players.size());
        if(!players.isEmpty())
            sender.sendMessage(players.stream().map(Player::getName).collect(Collectors.joining(", ")));
    }

}
