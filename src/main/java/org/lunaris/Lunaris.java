package org.lunaris;

import co.aikar.timings.Timings;
import jline.console.ConsoleReader;
import org.lunaris.command.CommandManager;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.LongEntityData;
import org.lunaris.event.EventHandler;
import org.lunaris.event.EventManager;
import org.lunaris.event.Listener;
import org.lunaris.event.network.PacketReceivedAsyncEvent;
import org.lunaris.event.network.PacketSendingAsyncEvent;
import org.lunaris.network.NetworkManager;
import org.lunaris.network.protocol.packet.Packet09Text;
import org.lunaris.network.protocol.packet.Packet27SetEntityData;
import org.lunaris.network.protocol.packet.Packet3AFullChunkData;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.server.*;
import org.lunaris.util.configuration.ConfigurationManager;
import org.lunaris.util.logger.FormatLogger;
import org.lunaris.world.Location;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 12.09.17.
 */
public class Lunaris implements IServer {

    private static Lunaris instance;

    private FormatLogger logger;

    private Scheduler scheduler;

    private EventManager eventManager;

    private ConfigurationManager configurationManager;

    private ServerSettings serverSettings;

    private NetworkManager networkManager;

    private PlayerProvider playerProvider;

    private EntityProvider entityProvider;

    private ResourcePackManager resourcePackManager;

    private WorldProvider worldProvider;

    private BanChecker banChecker;

    private CommandManager commandManager;

    private PlayerList playerList;

    private static boolean shuttingDown;

    Lunaris(FormatLogger logger, ConsoleReader consoleReader) {
        logger.info("Starting Lunaris version %s for %s clients..", getServerVersion(), getSupportedClientVersion());
        instance = this;
        this.logger = logger;
        long activationTime = System.currentTimeMillis();
        loadConfigurations();
        loadDefaults();
        logger.info("Done (%.3f seconds)! For list of commands type \"help\".", (System.currentTimeMillis() - activationTime) / 1000F);
        runConsole(consoleReader);
    }

    public void disable() {
        logger.info("Disabling Lunaris version %s..", getServerVersion());
        Timings.stopServer();
        this.networkManager.disable();

        try {
            Thread.sleep(2000L);
        }catch(InterruptedException ex) {
            ex.printStackTrace();
        }
        logger.info("Good bye!");
        System.exit(0);
    }

    public static Lunaris getInstance() {
        return instance;
    }

    public FormatLogger getLogger() {
        return this.logger;
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

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
        return "1.2.0.31";
    }

    private void loadConfigurations() {
        logger.info("Loading configuration..");
        this.configurationManager = new ConfigurationManager(this);
        this.serverSettings = new ServerSettings(this, this.configurationManager.getConfig());
    }

    private void loadDefaults() {
        logger.info("Loading defaults..");
        this.scheduler = new Scheduler(this);
        this.eventManager = new EventManager(this);
        this.entityProvider = new EntityProvider();
        this.playerProvider = new PlayerProvider(this);
        this.worldProvider = new WorldProvider(this); //this one starts global server tick
        this.networkManager = new NetworkManager(this);
        this.resourcePackManager = new ResourcePackManager();
        this.banChecker = new BanChecker(this);
        this.commandManager = new CommandManager();
        this.commandManager.registerDefaults();
        this.playerList = new PlayerList(this);

//        this.scheduler.schedule(() -> {
//            if(getOnlinePlayers().isEmpty())
//                return;
//            Location loc = getOnlinePlayers().iterator().next().getLocation();
//            int cx = loc.getBlockX() >> 4, cz = loc.getBlockZ() >> 4;
//            this.logger.info("Chunk {%d;%d}: %s", cx, cz, loc.getWorld().isChunkLoadedAt(cx, cz) ? "loaded" : "not loaded");
//        }, 1, 1, TimeUnit.SECONDS);

//        this.eventManager.register(new Listener() {
//
//            @EventHandler
//            public void onSending(PacketSendingAsyncEvent e) {
////                if(e.getPacket().getId() == 0x27) {
////                    Packet27SetEntityData packet = (Packet27SetEntityData) e.getPacket();
////                    LongEntityData led = (LongEntityData) packet.getMetadata().getMap().get(0);
////                    long value = led.data;
////                    logger.info("Sent %d %s", value, Long.toHexString(value));
////                }
//                if(e.getPacket().getId() == 0x13)
//                    return;
//                logger.info("Sent packet %s", e.getPacket().getClass().getSimpleName());
//            }
//
//            @EventHandler
//            public void onReceiving(PacketReceivedAsyncEvent e) {
//                if(e.getPacket().getId() == 0x13)
//                    return;
//                logger.info("Received packet %s", e.getPacket().getClass().getSimpleName());
//            }
//
//        });
    }

    private void runConsole(ConsoleReader consoleReader) {
        this.scheduler.runAsync(() -> {
            while (!shuttingDown) {
                try {
                    String line = consoleReader.readLine("> ");
                    if (line != null)
                        this.scheduler.addSyncTask(() -> this.commandManager.handle('/' + line, null));
                } catch (Exception ex) {
                    logger.error(ex, "Can not handle command from server console. Is everything ok?");
                }
            }
        });
    }

    @Override
    public ConfigurationManager getConfigurationManager() {
        return this.configurationManager;
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
    public BanChecker getBanChecker() {
        return this.banChecker;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return this.playerProvider.getOnlinePlayers();
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    @Override
    public Player getPlayer(String name) {
        return this.playerProvider.getPlayer(name);
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return this.playerProvider.getPlayer(uuid);
    }

    @Override
    public void broadcastMessage(String message) {
        this.networkManager.sendPacket(getOnlinePlayers(), new Packet09Text(Packet09Text.MessageType.RAW, "", message, false));
        this.logger.info(message);
    }

    public PlayerProvider getPlayerProvider() {
        return this.playerProvider;
    }

    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

}
