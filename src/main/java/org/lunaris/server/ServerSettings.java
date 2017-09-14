package org.lunaris.server;

import org.lunaris.entity.data.Gamemode;
import org.lunaris.util.configuration.FileConfiguration;

/**
 * Created by RINES on 12.09.17.
 */
public class ServerSettings {

    private final String host;
    private final int port;

    private final String serverName;

    private final int supportedClientProtocol = 136;

    private final String supportedClientVersion;

    private final int maxPlayersOnServer;

    private final int networkCompressionLevel;

    private final byte networkPacketPrefixedId;

    private final Gamemode defaultGamemode;

    private final int chunksView;

    private final boolean unloadChunks;

    public ServerSettings(IServer server, FileConfiguration config) {
        try {
            String host = config.getOrSetString("bind-address", "0.0.0.0:19132");
            String[] split = host.split(":");
            this.host = split[0];
            this.port = Integer.parseInt(split[1]);
            this.serverName = config.getOrSetString("server-name", "Lunaris Test Server");
            this.supportedClientVersion = server.getSupportedClientVersion();
            this.maxPlayersOnServer = config.getOrSetInt("max-players", 20);
            this.networkCompressionLevel = config.getOrSetInt("network.compression-level", 7);
            this.networkPacketPrefixedId = config.getOrSetByte("network.packet-prefixed-id", (byte) 0xfe);
            this.defaultGamemode = Gamemode.values()[config.getOrSetInt("default-gamemode", 2)];
            this.chunksView = config.getOrSetInt("chunks-view", 6);
            this.unloadChunks = config.getOrSetBoolean("unload-chunks", true);
            server.getConfigurationManager().saveConfig();
        }catch(Exception ex) {
            throw new IllegalArgumentException("Server Settings file can not be loaded", ex);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getServerName() {
        return serverName;
    }

    public int getSupportedClientProtocol() {
        return supportedClientProtocol;
    }

    public String getSupportedClientVersion() {
        return supportedClientVersion;
    }

    public int getMaxPlayersOnServer() {
        return maxPlayersOnServer;
    }

    public int getNetworkCompressionLevel() {
        return networkCompressionLevel;
    }

    public byte getNetworkPacketPrefixedId() {
        return networkPacketPrefixedId;
    }

    public Gamemode getDefaultGamemode() {
        return defaultGamemode;
    }

    public int getChunksView() {
        return chunksView;
    }

    public boolean isUnloadChunks() {
        return unloadChunks;
    }
}
