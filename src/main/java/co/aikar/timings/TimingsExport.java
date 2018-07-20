package co.aikar.timings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lunaris.LunarisServer;
import org.lunaris.command.CommandSender;
import org.lunaris.util.JsonUtil;
import org.lunaris.util.logger.ChatColor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

import static co.aikar.timings.TimingsManager.HISTORY;

/**
 * Created by RINES on 30.09.17.
 */
public class TimingsExport {

    public static void report(CommandSender sender) {
        LunarisServer server = LunarisServer.getInstance();
        JsonObject out = new JsonObject();
        out.addProperty("version", server.getSupportedClientVersion());
        out.addProperty("maxplayers", server.getServerSettings().getMaxPlayersOnServer());
        out.addProperty("start", TimingsManager.timingStart / 1000);
        out.addProperty("end", System.currentTimeMillis() / 1000);
        out.addProperty("sampletime", (System.currentTimeMillis() - TimingsManager.timingStart) / 1000);

        final Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        JsonObject system = new JsonObject();
        system.addProperty("timingcost", getCost());
        system.addProperty("name", System.getProperty("os.name"));
        system.addProperty("version", System.getProperty("os.version"));
        system.addProperty("jvmversion", System.getProperty("java.version"));
        system.addProperty("arch", System.getProperty("os.arch"));
        system.addProperty("maxmem", runtime.maxMemory());
        system.addProperty("cpu", runtime.availableProcessors());
        system.addProperty("runtime", ManagementFactory.getRuntimeMXBean().getUptime());
        system.addProperty("flags", String.join(" ", runtimeBean.getInputArguments()));
        system.add("gc", JsonUtil.mapToObject(ManagementFactory.getGarbageCollectorMXBeans(), (input) ->
                new JsonUtil.JSONPair(input.getName(), JsonUtil.toArray(input.getCollectionCount(), input.getCollectionTime()))));
        out.add("system", system);

        TimingsHistory[] history = HISTORY.toArray(new TimingsHistory[HISTORY.size() + 1]);
        history[HISTORY.size()] = new TimingsHistory(); //Current snapshot

        JsonObject timings = new JsonObject();
        for (TimingIdentifier.TimingGroup group : TimingIdentifier.GROUP_MAP.values()) {
            for (Timing id : group.timings.stream().toArray(Timing[]::new)) {
                if (!id.timed && !id.isSpecial()) {
                    continue;
                }

                timings.add(String.valueOf(id.id), JsonUtil.toArray(group.id, id.name));
            }
        }
        JsonObject idmap = new JsonObject();
        idmap.add("groups", JsonUtil.mapToObject(TimingIdentifier.GROUP_MAP.values(), group -> new JsonUtil.JSONPair(group.id, group.name)));
        idmap.add("handlers", timings);
        out.add("idmap", idmap);
        out.add("plugins", JsonUtil.mapToObject(server.getPluginManager().getPlugins(), plugin -> {
            JsonObject jsonPlugin = new JsonObject();
            jsonPlugin.addProperty("version", plugin.getVersion());
//            jsonPlugin.addProperty("description", plugin.getDescription());// Sounds legit
//            jsonPlugin.addProperty("website", plugin.getWebsite());
//            jsonPlugin.addProperty("authors", String.join(", ", plugin.getDescription().getAuthors()));
            return new JsonUtil.JSONPair(plugin.getName(), jsonPlugin);
        }));
        out.add("data", JsonUtil.mapToArray(history, TimingsHistory::export));
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://timings.aikar.co/post").openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("User-Agent", "Nukkit/" + server.getServerSettings().getServerName() + "/" + InetAddress.getLocalHost().getHostName());
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            OutputStream request = new GZIPOutputStream(con.getOutputStream()) {
                {
                    this.def.setLevel(7);
                }
            };
//            System.out.println(new String(new Gson().toJson(out).getBytes("UTF-8")));
            request.write(new Gson().toJson(out).getBytes("UTF-8"));
            request.close();

            String response = getResponse(sender, con);
            if (con.getResponseCode() != 302) {
                sender.sendMessage(ChatColor.RED + "Timings upload ended with unknown response code: " + con.getResponseCode() + " (" + con.getResponseMessage() + ")");
                return;
            }
            String location = con.getHeaderField("Location");
            sender.sendMessage(ChatColor.GREEN + "Timings have been uploaded to " + ChatColor.WHITE + location);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String getResponse(CommandSender sender, HttpURLConnection con) throws IOException {
        InputStream is = null;
        try {
            is = con.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }
            return bos.toString();

        } catch (IOException exception) {
            sender.sendMessage(ChatColor.RED + "Timings upload error: " + exception.getMessage());
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static long getCost() {
        int passes = 200;
        Timing SAMPLER1 = TimingsManager.getTiming(null, "Timings sampler 1", null);
        Timing SAMPLER2 = TimingsManager.getTiming(null, "Timings sampler 2", null);
        Timing SAMPLER3 = TimingsManager.getTiming(null, "Timings sampler 3", null);
        Timing SAMPLER4 = TimingsManager.getTiming(null, "Timings sampler 4", null);
        Timing SAMPLER5 = TimingsManager.getTiming(null, "Timings sampler 5", null);
        Timing SAMPLER6 = TimingsManager.getTiming(null, "Timings sampler 6", null);

        long start = System.nanoTime();
        for (int i = 0; i < passes; i++) {
            SAMPLER1.startTiming();
            SAMPLER2.startTiming();
            SAMPLER3.startTiming();
            SAMPLER4.startTiming();
            SAMPLER5.startTiming();
            SAMPLER6.startTiming();
            SAMPLER6.stopTiming();
            SAMPLER5.stopTiming();
            SAMPLER4.stopTiming();
            SAMPLER3.stopTiming();
            SAMPLER2.stopTiming();
            SAMPLER1.stopTiming();
        }

        long timingsCost = (System.nanoTime() - start) / passes / 6;

        SAMPLER1.reset(true);
        SAMPLER2.reset(true);
        SAMPLER3.reset(true);
        SAMPLER4.reset(true);
        SAMPLER5.reset(true);
        SAMPLER6.reset(true);

        return timingsCost;
    }

}
