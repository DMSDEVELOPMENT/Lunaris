package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandParameterType;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.entity.data.LPermission;

/**
 * Created by RINES on 01.10.17.
 */
public class CommandTest extends Command {

    public CommandTest() {
        super("test", LPermission.OPERATOR);
        addParametersVariant(new CommandParameter("player", CommandParameterType.PLAYER));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player)sender;
        } else {
            if(args.length == 0) {
                sender.sendMessage("Enter player name");
                return;
            }
            player = Lunaris.getInstance().getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("Player not found");
                return;
            }
        }
        Gamemode gamemode = player.getGamemode();
        player.setGamemode(gamemode == Gamemode.SURVIVAL ? Gamemode.CREATIVE : Gamemode.SURVIVAL);
    }
}
