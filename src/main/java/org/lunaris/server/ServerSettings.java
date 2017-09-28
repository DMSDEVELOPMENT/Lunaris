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

    private final boolean timingsEnabledByDefault;

    private final boolean timingsVerbose;

    private final int timingsHistoryInterval;

    private final int timingsHistoryLength;

    private final float mtuScaleFactor;

    private final long serverBoostingFactor;

    private final IngameSettings ingameSettings;

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
            this.ingameSettings = new IngameSettings(config);
            this.ingameSettings.load(Packet37AdventureSettings.Flag.WORLD_IMMUTABLE, "ingame.world-immutable");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.NO_PVP, "ingame.no-pvp");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.AUTO_JUMP, "ingame.auto-jump");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.ALLOW_FLIGHT, "ingame.allow-flight");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.FLYING, "ingame.everybody-flying");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.NO_CLIP, "ingame.no-clip");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.WORLD_BUILDER, "ingame.world-builder");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.MUTED, "ingame.everybody-muted");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.BUILD_AND_MINE, "ingame.access.build-and-mine");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.DOORS_AND_SWITCHES, "ingame.access.use-doors-and-switches");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.OPEN_CONTAINERS, "ingame.access.open-containers");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.ATTACK_PLAYERS, "ingame.access.attack-players");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.ATTACK_MOBS, "ingame.access.attack-mobs");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.TELEPORT, "ingame.access.teleport");
            this.ingameSettings.load(Packet37AdventureSettings.Flag.OPERATOR, "ingame.everybody-operator");
            this.timingsEnabledByDefault = config.getOrSetBoolean("timings.enabled-by-default", false);
            this.timingsVerbose = config.getOrSetBoolean("timings.verbose", false);
            this.timingsHistoryInterval = config.getOrSetInt("timings.history-interval", 6000);
            this.timingsHistoryLength = config.getOrSetInt("timings.history-length", 72000);
            this.mtuScaleFactor = (float) config.getOrSetDouble("mtu-scale-factor", 2F / 3F);
            this.serverBoostingFactor = config.getOrSetInt("server-boosting-factor", 5);
            server.getConfigurationManager().saveConfig();
        }catch(Exception ex) {
            throw new IllegalArgumentException("Server Settings file can not be loaded", ex);
        }
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getServerName() {
        return this.serverName;
    }

    public int getSupportedClientProtocol() {
        return this.supportedClientProtocol;
    }

    public String getSupportedClientVersion() {
        return this.supportedClientVersion;
    }

    public int getMaxPlayersOnServer() {
        return this.maxPlayersOnServer;
    }

    public int getNetworkCompressionLevel() {
        return this.networkCompressionLevel;
    }

    public byte getNetworkPacketPrefixedId() {
        return this.networkPacketPrefixedId;
    }

    public Gamemode getDefaultGamemode() {
        return this.defaultGamemode;
    }

    public int getChunksView() {
        return this.chunksView;
    }

    public boolean isUnloadChunks() {
        return this.unloadChunks;
    }

    public boolean isTimingsEnabledByDefault() {
        return this.timingsEnabledByDefault;
    }

    public boolean isTimingsVerbose() {
        return this.timingsVerbose;
    }

    public int getTimingsHistoryInterval() {
        return this.timingsHistoryInterval;
    }

    public int getTimingsHistoryLength() {
        return this.timingsHistoryLength;
    }

    public float getMtuScaleFactor() {
        return this.mtuScaleFactor;
    }

    public long getServerBoostingFactor() {
        return this.serverBoostingFactor;
    }

    public IngameSettings getIngameSettings() {
        return this.ingameSettings;
    }

    public static class IngameSettings {

        private final FileConfiguration config;

        private IngameSettings(FileConfiguration config) {
            this.config = config;
        }

        private void load(Packet37AdventureSettings.Flag flag, String configKey) {
            setSetting(flag, this.config.getOrSetBoolean(configKey, flag.hasDefaultValue()));
        }

        public void setSetting(Packet37AdventureSettings.Flag setting, boolean value) {
            setting.setDefaultValue(value);
        }

        public boolean getSetting(Packet37AdventureSettings.Flag setting) {
            return setting.hasDefaultValue();
        }

    }

}
