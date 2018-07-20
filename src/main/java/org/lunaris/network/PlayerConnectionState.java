package org.lunaris.network;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public enum PlayerConnectionState {
    /**
     * The player is still waiting for the login packet.
     */
    HANDSHAKE,

    /**
     * We told the client it should get ready for encryption
     */
    ENCRPYTION_INIT,

    /**
     * Sending resource packs and waiting for the client to decide
     */
    RESOURCE_PACK,

    /**
     * The player has logged in and is preparing for playing.
     */
    LOGIN,

    /**
     * The player is entirely connected and is playing on the server.
     */
    PLAYING,

    DISCONNECTED
}
