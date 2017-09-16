package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.LPermission;

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
        sender.sendMessage("Players online: %d", Lunaris.getInstance().getOnlinePlayers().size());
        sender.sendMessage(Lunaris.getInstance().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.joining(", ")));
    }

}
