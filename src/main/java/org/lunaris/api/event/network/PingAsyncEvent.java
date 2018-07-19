package org.lunaris.api.event.network;

import org.lunaris.api.event.Event;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PingAsyncEvent extends Event {

    private String motd;
    private int amountOfPlayers;
    private int maxPlayers;

    public PingAsyncEvent(String motd, int amountOfPlayers, int maxPlayers) {
        this.motd = motd;
        this.amountOfPlayers = amountOfPlayers;
        this.maxPlayers = maxPlayers;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public void setAmountOfPlayers(int amountOfPlayers) {
        this.amountOfPlayers = amountOfPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

}
