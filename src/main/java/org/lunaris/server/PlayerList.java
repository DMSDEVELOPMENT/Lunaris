package org.lunaris.server;

import org.lunaris.LunarisServer;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.protocol.packet.Packet3FPlayerList;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerList {

    private final LunarisServer server;

    public PlayerList(LunarisServer server) {
        this.server = server;
    }

    public void addPlayer(LPlayer player) {
        Packet3FPlayerList packet = new Packet3FPlayerList(Packet3FPlayerList.Type.ADD, new Packet3FPlayerList.Entry(player));
        Collection<LPlayer> players = getPlayersWithout(player);
        this.server.getNetworkManager().sendPacket(players, packet);
        createFor(player);
    }

    private void createFor(LPlayer player) {
        Collection<LPlayer> players = this.server.getOnlinePlayers();
        Packet3FPlayerList.Entry[] entries = new Packet3FPlayerList.Entry[players.size()];
        int index = 0;
        for(Iterator<LPlayer> iterator = players.iterator(); iterator.hasNext();)
            entries[index++] = new Packet3FPlayerList.Entry(iterator.next());
        player.sendPacket(new Packet3FPlayerList(Packet3FPlayerList.Type.ADD, entries));
    }

    public void removePlayer(LPlayer player) {
        Packet3FPlayerList packet = new Packet3FPlayerList(Packet3FPlayerList.Type.REMOVE, new Packet3FPlayerList.Entry(player.getUUID()));
        this.server.getNetworkManager().sendPacket(getPlayersWithout(player), packet);
    }

    private Collection<LPlayer> getPlayersWithout(LPlayer p) {
        return this.server.getPlayerProvider().getOnlinePlayersWithout(p);
    }

}
