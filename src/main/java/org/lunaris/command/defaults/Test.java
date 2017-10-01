package org.lunaris.command.defaults;

import org.lunaris.Lunaris;
import org.lunaris.command.Command;
import org.lunaris.command.CommandParameter;
import org.lunaris.command.CommandSender;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.entity.data.LPermission;

/**
 * Created by RINES on 01.10.17.
 */
public class Test extends Command {

    public Test() {
        super("test", LPermission.OPERATOR, new CommandParameter("player", false));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0)
            return;
        Player player = Lunaris.getInstance().getPlayer(args[0]);
        if(player != null) {
            Gamemode gamemode = player.getGamemode();
            player.setGamemode(gamemode == Gamemode.SURVIVAL ? Gamemode.CREATIVE : Gamemode.SURVIVAL);
        }
    }

}
