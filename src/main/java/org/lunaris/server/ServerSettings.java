package org.lunaris.server;

import org.lunaris.api.entity.Gamemode;
import org.lunaris.api.util.configuration.FileConfiguration;
import org.lunaris.api.util.configuration.yaml.YamlConfiguration;
import org.lunaris.network.packet.Packet37AdventureSettings;

import java.io.File;

/**
 * Created by RINES on 12.09.17.
 */
public class ServerSettings {

    private final String host;
    private final int port;

    private final String serverName;

    private final int supportedClientProtocol = 274;

    private final String supportedClientVersion;

    private final int maxPlayersOnServer;

    private final int networkCompressionLevel;

    private final Gamemode defaultGamemode;

    private final int chunksView;

    private final boolean unloadChunks;

    private final boolean timingsEnabledByDefault;

    private final boolean timingsVerbose;

    private final int timingsHistoryInterval;

    private final int timingsHistoryLength;

    private final float mtuScaleFactor;

    private final IngameSettings ingameSettings;

    private final boolean encryptedConnection;

    private final boolean onlineMode;

    public ServerSettings(IServer server) {
        File configFile = new File("lunaris.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        try {
            String host = config.getOrSetString("bind-address", "0.0.0.0:19132");
            String[] split = host.split(":");
            this.host = split[0];
            this.port = Integer.parseInt(split[1]);
            this.serverName = config.getOrSetString("server-name", "Lunaris Test Server");
            this.supportedClientVersion = server.getSupportedClientVersion();
            this.maxPlayersOnServer = config.getOrSetInt("max-players", 20);
            this.networkCompressionLevel = config.getOrSetInt("network_old.compression-level", 1);
            this.mtuScaleFactor = (float) config.getOrSetDouble("network_old.mtu-scale-factor", 2F / 3F);
            this.defaultGamemode = Gamemode.values()[config.getOrSetInt("default-gamemode", 0)];
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
            this.encryptedConnection = config.getOrSetBoolean("use-encrypted-connection", true);
            this.onlineMode = config.getOrSetBoolean("online-mode", true);
            config.save(configFile);
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

    public IngameSettings getIngameSettings() {
        return this.ingameSettings;
    }

    public boolean isUsingEncryptedConnection() {
        return this.encryptedConnection;
    }

    public boolean isInOnlineMode() {
        return this.onlineMode;
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
