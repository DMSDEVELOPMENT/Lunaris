package org.lunaris.server;

import org.lunaris.entity.data.Gamemode;
import org.lunaris.network.protocol.packet.Packet37AdventureSettings;
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

    private final int adventureSettingsFlag;

    private final boolean timingsEnabledByDefault;

    private final boolean timingsVerbose;

    private final int timingsHistoryInterval;

    private final int timingsHistoryLength;

    private final float mtuScaleFactor;

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
            int adventureFlag = 0;
//            if(config.getOrSetBoolean("player-access.build-and-mine", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_BUILD_AND_MINE;
//            if(config.getOrSetBoolean("player-access.doors-and-switches", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_DOORS_AND_SWITCHES;
//            if(config.getOrSetBoolean("player-access.open-containers", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_OPEN_CONTAINERS;
//            if(config.getOrSetBoolean("player-access.attack-players", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_ATTACK_PLAYERS;
//            if(config.getOrSetBoolean("player-access.attack-monsters", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_ATTACK_MOBS;
//            if(config.getOrSetBoolean("player-access.teleport", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_TELEPORT;
//            if(config.getOrSetBoolean("player-access.default-level-perms", true))
//                adventureFlag |= Packet37AdventureSettings.ACTION_FLAG_DEFAULT_LEVEL_PERMISSIONS;
            this.adventureSettingsFlag = adventureFlag;
            this.timingsEnabledByDefault = config.getOrSetBoolean("timings.enabled-by-default", false);
            this.timingsVerbose = config.getOrSetBoolean("timings.verbose", false);
            this.timingsHistoryInterval = config.getOrSetInt("timings.history-interval", 6000);
            this.timingsHistoryLength = config.getOrSetInt("timings.history-length", 72000);
            this.mtuScaleFactor = (float) config.getOrSetDouble("mtu-scale-factor", 2F / 3F);
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

    public int getAdventureSettingsFlag() {
        return adventureSettingsFlag;
    }

    public boolean isTimingsEnabledByDefault() {
        return timingsEnabledByDefault;
    }

    public boolean isTimingsVerbose() {
        return timingsVerbose;
    }

    public int getTimingsHistoryInterval() {
        return timingsHistoryInterval;
    }

    public int getTimingsHistoryLength() {
        return timingsHistoryLength;
    }

    public float getMtuScaleFactor() {
        return mtuScaleFactor;
    }

}
