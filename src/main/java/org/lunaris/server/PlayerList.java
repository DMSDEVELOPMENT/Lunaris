package org.lunaris.server;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.network.protocol.packet.Packet3FPlayerList;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerList {

    private final Lunaris server;

    public PlayerList(Lunaris server) {
        this.server = server;
    }

    public void addPlayer(Player player) {
        Packet3FPlayerList packet = new Packet3FPlayerList(Packet3FPlayerList.Type.ADD, new Packet3FPlayerList.Entry(player));
        Collection<Player> players = getPlayersWithout(player);
        this.server.getNetworkManager().sendPacket(players, packet);
        createFor(player);
    }

    private void createFor(Player player) {
        Collection<Player> players = this.server.getOnlinePlayers();
        Packet3FPlayerList.Entry[] entries = new Packet3FPlayerList.Entry[players.size()];
        int index = 0;
        for(Iterator<Player> iterator = players.iterator(); iterator.hasNext();)
            entries[index++] = new Packet3FPlayerList.Entry(iterator.next());
        player.sendPacket(new Packet3FPlayerList(Packet3FPlayerList.Type.ADD, entries));
    }

    public void removePlayer(Player player) {
        Packet3FPlayerList packet = new Packet3FPlayerList(Packet3FPlayerList.Type.REMOVE, new Packet3FPlayerList.Entry(player.getClientUUID()));
        this.server.getNetworkManager().sendPacket(getPlayersWithout(player), packet);
    }

    private Collection<Player> getPlayersWithout(Player p) {
        return this.server.getPlayerProvider().getOnlinePlayersWithout(p);
    }

}
