package org.lunaris.api.plugin;

import org.lunaris.api.util.Internal;
import org.lunaris.server.IServer;
import org.lunaris.api.util.configuration.yaml.YamlConfiguration;
import org.lunaris.util.logger.OwnLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author xtrafrancyz
 */
public class PluginManager {

    private final Map<String, Plugin> plugins;
    private final File pluginsDir;
    private final IServer server;
    final Map<String, Class<?>> cachedClasses;

    public PluginManager(IServer server) {
        this.server = server;
        this.plugins = new HashMap<>();
        this.pluginsDir = new File("plugins");
        this.cachedClasses = new HashMap<>();
    }

    @Internal
    public void loadPlugins() {
        if (!this.pluginsDir.exists())
            this.pluginsDir.mkdirs();
        File[] files = this.pluginsDir.listFiles(f -> f.isFile() && f.getName().endsWith(".jar"));
        if (files == null)
            return;
        for (File pluginFile : files) {
            ZipFile zip = null;
            try {
                zip = new ZipFile(pluginFile);
                ZipEntry descriptionEntry = zip.getEntry("plugin.yml");
                if (descriptionEntry == null)
                    continue;
                YamlConfiguration description = new YamlConfiguration();
                description.load(new InputStreamReader(zip.getInputStream(descriptionEntry), StandardCharsets.UTF_8));
                loadPlugin(pluginFile, description);
            } catch (Exception ex) {
                this.server.getLogger().warn(ex, "Cannot load plugin %s", pluginFile.getName());
                ex.printStackTrace();
            } finally {
                if (zip != null)
                    try { zip.close(); } catch (IOException ignored) {}
            }
        }
        for (Plugin plugin : plugins.values()) {
            try {
                server.getLogger().info("Enabling plugin %s", plugin.getFullName());
                plugin.setEnabled(true);
            } catch (Exception ex) {
                server.getLogger().warn(ex, "Exception whilst enabling plugin %s", plugin.getFullName());
            }
        }
    }

    private void loadPlugin(File file, YamlConfiguration description) throws Exception {
        if (!description.contains("main"))
            throw new Exception("plugin.yml must have 'main' parameter");
        if (!description.contains("name"))
            throw new Exception("plugin.yml must have 'name' parameter");
        PluginClassLoader classLoader = new PluginClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader(), this);
        Class<?> pluginClass = Class.forName(description.getString("main"), true, classLoader);
        Plugin plugin = pluginClass.asSubclass(Plugin.class).newInstance();
        plugin.name = description.getString("name");
        plugin.version = description.getString("version", "0");
        plugin.logger = OwnLogger.getLogger(plugin.name);
        this.plugins.put(plugin.getName(), plugin);
    }

    @Internal
    public void disablePlugins() {
        for (Plugin plugin : plugins.values()) {
            try {
                server.getLogger().info("Disabling plugin %s", plugin.getFullName());
                plugin.setEnabled(false);
            } catch (Exception ex) {
                server.getLogger().warn(ex, "Exception whilst disabling plugin %s", plugin.getFullName());
            }
        }
    }

    public Collection<Plugin> getPlugins() {
        return this.plugins.values();
    }

    Class<?> findClassInPlugins(String name) {
        Class<?> result = cachedClasses.get(name);
        if (result != null)
            return result;
        for (Plugin plugin : plugins.values()) {
            try {
                result = plugin.classLoader.findClass0(name, false);
            } catch (ClassNotFoundException ignored) {}
            if (result != null)
                return result;
        }
        return null;
    }

}
