package co.aikar.timings;

import org.lunaris.LunarisServer;
import org.lunaris.api.event.Event;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.server.ServerSettings;
import org.lunaris.world.LWorld;

import java.util.Queue;

import static co.aikar.timings.TimingIdentifier.DEFAULT_GROUP;

/**
 * Created by RINES on 15.09.17.
 */
public final class Timings {
    private static boolean timingsEnabled = false;
    private static boolean verboseEnabled = false;

    private static final int MAX_HISTORY_FRAMES = 12;
    private static int historyInterval = -1;
    private static int historyLength = -1;

    public static final FullServerTickTiming fullServerTickTimer;
    public static final Timing timingsTickTimer;

    private static final Timing directTasks;
    private static final Timing worldsTimer;
    private static final Timing chunksTickTimer;
    private static final Timing entitiesTickTimer;
    private static final Timing packetsReceptionTimer;
    private static final Timing packetsSerializationTimer;
    private static final Timing packetsSendingTimer;
    private static final Timing eventTimer;


    static {
        ServerSettings config = LunarisServer.getInstance().getServerSettings();
        setTimingsEnabled(config.isTimingsEnabledByDefault());
        setVerboseEnabled(config.isTimingsVerbose());
        setHistoryInterval(config.getTimingsHistoryInterval());
        setHistoryLength(config.getTimingsHistoryLength());

        fullServerTickTimer = new FullServerTickTiming();
        timingsTickTimer = TimingsManager.getTiming(DEFAULT_GROUP.name, "Timings Tick", fullServerTickTimer);

        directTasks = TimingsManager.getTiming("Direct Tasks Summary");
        worldsTimer = TimingsManager.getTiming(directTasks.name, "Worlds", directTasks);
        chunksTickTimer = TimingsManager.getTiming(directTasks.name, "Chunks", directTasks);
        entitiesTickTimer = TimingsManager.getTiming(directTasks.name, "Entities", directTasks);

        Timing packetsGroup = TimingsManager.getTiming("Packets");
        packetsReceptionTimer = TimingsManager.getTiming(packetsGroup.name, "Reception", packetsGroup);
        packetsSerializationTimer = TimingsManager.getTiming(packetsGroup.name, "Serialization", packetsGroup);
        packetsSendingTimer = TimingsManager.getTiming(packetsGroup.name, "Sending", packetsGroup);

        eventTimer = TimingsManager.getTiming("Event Executions");
    }

    public static Timing getWorldTickTimer(LWorld world) {
        return TimingsManager.getTiming(worldsTimer.name, world.getName(), worldsTimer);
    }

    public static Timing getChunksTickTimer() {
        return chunksTickTimer;
    }

    public static Timing getEntitiesTickTimer() {
        return entitiesTickTimer;
    }

    public static Timing getPacketsReceptionTimer() {
        return packetsReceptionTimer;
    }

    public static Timing getPacketsSerializationTimer(MinePacket packet) {
        return TimingsManager.getTiming(packetsSerializationTimer.name, packet.getClass().getSimpleName(), packetsSerializationTimer);
    }

    public static Timing getPacketsSendingTimer() {
        return packetsSendingTimer;
    }

    public static Timing getEventTimer(Event event) {
        return TimingsManager.getTiming(eventTimer.name, event.getClass().getSimpleName(), eventTimer);
    }

    public static boolean isTimingsEnabled() {
        return timingsEnabled;
    }

    public static void setTimingsEnabled(boolean enabled) {
        timingsEnabled = enabled;
        TimingsManager.reset();
    }

    public static boolean isVerboseEnabled() {
        return verboseEnabled;
    }

    public static void setVerboseEnabled(boolean enabled) {
        verboseEnabled = enabled;
        TimingsManager.needsRecheckEnabled = true;
    }

    public static int getHistoryInterval() {
        return historyInterval;
    }

    public static void setHistoryInterval(int interval) {
        historyInterval = Math.max(20 * 60, interval);
        //Recheck the history length with the new Interval
        if (historyLength != -1) {
            setHistoryLength(historyLength);
        }
    }

    public static int getHistoryLength() {
        return historyLength;
    }

    public static void setHistoryLength(int length) {
        //Cap at 12 History Frames, 1 hour at 5 minute frames.
        int maxLength = historyInterval * MAX_HISTORY_FRAMES;
        //For special cases of servers with special permission to bypass the max.
        //This max helps keep data file sizes reasonable for processing on Aikar's Timing parser side.
        //Setting this will not help you bypass the max unless Aikar has added an exception on the API side.
//        if (Server.getInstance().getConfig().getBoolean("timings.bypass-max", false)) {
//            maxLength = Integer.MAX_VALUE;
//        }

        historyLength = Math.max(Math.min(maxLength, length), historyInterval);

        Queue<TimingsHistory> oldQueue = TimingsManager.HISTORY;
        int frames = (getHistoryLength() / getHistoryInterval());
        if (length > maxLength) {
            LunarisServer.getInstance().getLogger().warning(
                    "Timings Length too high. Requested " + length + ", max is " + maxLength
                            + ". To get longer history, you must increase your interval. Set Interval to "
                            + Math.ceil(length / MAX_HISTORY_FRAMES)
                            + " to achieve this length.");
        }

        TimingsManager.HISTORY = new TimingsManager.BoundedQueue<>(frames);
        TimingsManager.HISTORY.addAll(oldQueue);
    }

    public static void reset() {
        TimingsManager.reset();
    }

    public static void stopServer() {
        setTimingsEnabled(false);
        TimingsManager.recheckEnabled();
    }

}