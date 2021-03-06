package org.lunaris.command.defaults;

import org.lunaris.LunarisServer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandParameterType;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.util.logger.ChatColor;

/**
 * Created by RINES on 02.10.17.
 */
public class CommandGive extends Command {

    public CommandGive() {
        super("give", LPermission.ADMINISTRATIVE);
        setDescription("Give item to player");
        addParametersVariant(
                new CommandParameter("player", CommandParameterType.PLAYER),
                new CommandParameter("item info"),
                new CommandParameter("amount", CommandParameterType.INT).optional());
        addParametersVariant(
                new CommandParameter("player", CommandParameterType.PLAYER),
                new CommandParameter("item info", CommandParameterType.INT),
                new CommandParameter("amount", CommandParameterType.INT).optional());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2)
            return;
        LPlayer target = LunarisServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Target player is not online.");
            return;
        }
        int id, data = 0;
        String sid = args[1], sdata = null;
        if (args[1].contains(":")) {
            String[] split = args[1].split(":");
            sid = split[0];
            sdata = split[1];
        }
        try {
            id = Integer.parseInt(sid);
        } catch (NumberFormatException ex) {
            try {
                id = Material.valueOf(sid.toUpperCase()).getId();
            } catch (Exception ex2) {
                sender.sendMessage(ChatColor.RED + "Unknown item material.");
                return;
            }
        }
        try {
            if (sdata != null && !sdata.isEmpty())
                data = Integer.parseInt(sdata);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Illegal item data.");
            return;
        }

        int amount = 1;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "Illegal item amount.");
                return;
            }
        }
        target.getInventory().addItem(new ItemStack(id, amount, data));
        sender.sendMessage(ChatColor.GREEN + "Item has been given to " + target.getName() + ".");
    }

}
