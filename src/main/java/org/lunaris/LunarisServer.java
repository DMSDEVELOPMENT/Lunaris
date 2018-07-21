package org.lunaris;

import co.aikar.timings.Timings;
import jline.console.ConsoleReader;
import org.lunaris.api.event.EventHandler;
import org.lunaris.api.event.Listener;
import org.lunaris.api.plugin.PluginManager;
import org.lunaris.api.server.Scheduler;
import org.lunaris.command.CommandManager;
import org.lunaris.entity.EntityProvider;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.PlayerProvider;
import org.lunaris.event.EventManager;
import org.lunaris.event.network.PacketReceivedAsyncEvent;
import org.lunaris.event.network.PacketSendingAsyncEvent;
import org.lunaris.jwt.EncryptionKeyFactory;
import org.lunaris.network.NetworkManager;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.packet.Packet09Text;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.server.*;
import org.lunaris.util.logger.FormatLogger;

import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.UUID;

/**
 * Created by RINES on 12.09.17.
 */
public class LunarisServer implements IServer {

    private static LunarisServer instance;

    private FormatLogger logger;

    private Scheduler scheduler;

    private EventManager eventManager;

    private ServerSettings serverSettings;

    private NetworkManager networkManager;

    private PlayerProvider playerProvider;

    private EntityProvider entityProvider;

    private ResourcePackManager resourcePackManager;

    private WorldProvider worldProvider;

    private CommandManager commandManager;

    private PlayerList playerList;

    private PluginManager pluginManager;

    private EncryptionKeyFactory encryptionKeyFactory;

    private static boolean shuttingDown;

    LunarisServer(FormatLogger logger, ConsoleReader consoleReader) {
        logger.info("Starting Lunaris version %s for %s clients..", getServerVersion(), getSupportedClientVersion());
        instance = this;
        this.logger = logger;
        loadConfigurations();
        loadDefaults();
        this.logger.info("Done (%.3f seconds)! For list of commands type \"help\".", ManagementFactory.getRuntimeMXBean().getUptime() / 1000F);
        runConsole(consoleReader);
    }

    public void disable() {
        this.logger.info("Disabling Lunaris version %s..", getServerVersion());
        shuttingDown = true;
        this.pluginManager.disablePlugins();
        Timings.stopServer();
        this.networkManager.shutdown();
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.logger.info("Good bye!");
        System.exit(0);
    }

    public static LunarisServer getInstance() {
        return instance;
    }

    @Override
    public FormatLogger getLogger() {
        return this.logger;
    }

    @Override
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public EntityProvider getEntityProvider() {
        return this.entityProvider;
    }

    public static boolean isShuttingDown() {
        return shuttingDown;
    }

    @Override
    public String getServerVersion() {
        return "DEV";
    }

    @Override
    public String getSupportedClientVersion() {
        return "1.5.0";
    }

    private void loadConfigurations() {
        this.logger.info("Loading configuration..");
        this.serverSettings = new ServerSettings(this);
    }

    private void loadDefaults() {
        this.logger.info("Loading defaults..");
        this.scheduler = new Scheduler(this);
        this.eventManager = new EventManager(this);
        this.entityProvider = new EntityProvider();
        this.playerProvider = new PlayerProvider(this);
        this.worldProvider = new WorldProvider(this); //this one starts global server tick
        this.networkManager = new NetworkManager(this);
        this.resourcePackManager = new ResourcePackManager();
        this.commandManager = new CommandManager();
        this.commandManager.registerDefaults();
        this.playerList = new PlayerList(this);
        this.pluginManager = new PluginManager(this);
        this.pluginManager.loadPlugins();
        this.encryptionKeyFactory = new EncryptionKeyFactory(this);

        this.eventManager.register(new Listener() {

//            @EventHandler
//            public void onBreak(BlockBreakEvent e) {
//                e.getPlayer().setVelocity(new Vector3d(0d, 1d, 0d));
//            }

            @EventHandler
            public void onSending(PacketSendingAsyncEvent e) {
                logger.info("Sent packet %s to %s", e.getPacket().getClass().getSimpleName(), e.getPlayer().getName());
            }

            @EventHandler
            public void onReceiving(PacketReceivedAsyncEvent e) {
                if (e.getPacket().getID() == 0x13) {
                    return;
                }
                PlayerConnection connection = e.getPacket().getConnection();
                String name = connection.getPlayer() == null ? "---" : connection.getPlayer().getName();
                logger.info("Received packet %s from %s", e.getPacket().getClass().getSimpleName(), name);
            }

        });
    }

    private void runConsole(ConsoleReader consoleReader) {
        while (!shuttingDown) {
            try {
                String line = consoleReader.readLine("> ");
                if (line != null)
                    this.scheduler.run(() -> this.commandManager.handle('/' + line, null));
            } catch (Exception ex) {
                this.logger.error(ex, "Can not handle command from server console. Is everything ok?");
            }
        }
    }

    @Override
    public ServerSettings getServerSettings() {
        return this.serverSettings;
    }

    @Override
    public ResourcePackManager getResourcePackManager() {
        return this.resourcePackManager;
    }

    @Override
    public WorldProvider getWorldProvider() {
        return this.worldProvider;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public Collection<LPlayer> getOnlinePlayers() {
        return this.playerProvider.getOnlinePlayers();
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    @Override
    public LPlayer getPlayer(String name) {
        return this.playerProvider.getPlayer(name);
    }

    @Override
    public LPlayer getPlayer(UUID uuid) {
        return this.playerProvider.getPlayer(uuid);
    }

    @Override
    public void broadcastMessage(String message) {
        this.networkManager.sendPacket(getOnlinePlayers(), new Packet09Text(Packet09Text.MessageType.CLIENT_MESSAGE, "", message));
        this.logger.info(message);
    }

    public PlayerProvider getPlayerProvider() {
        return this.playerProvider;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    public EncryptionKeyFactory getEncryptionKeyFactory() {
        return this.encryptionKeyFactory;
    }

}
