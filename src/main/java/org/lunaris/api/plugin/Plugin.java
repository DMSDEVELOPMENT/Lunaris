package org.lunaris.api.plugin;

import org.lunaris.api.util.Internal;
import org.lunaris.api.util.configuration.FileConfiguration;
import org.lunaris.api.util.configuration.yaml.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author xtrafrancyz
 */
public class Plugin {

    private boolean enabled = false;
    private Map<String, FileConfiguration> configs = new HashMap<>();
    String name;
    String version;
    PluginClassLoader classLoader;
    Logger logger;

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getFullName() {
        return this.name + " v" + version;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public File getDataFolder() {
        return new File("plugins", name);
    }

    public File getConfigFile() {
        return getConfigFile("config");
    }

    public File getConfigFile(String name) {
        return new File(getDataFolder(), name + ".yml");
    }

    public FileConfiguration getConfig() {
        return getConfig("config");
    }

    public FileConfiguration getConfig(String name) {
        return this.configs.computeIfAbsent(name, name0 -> {
            File file = getConfigFile(name0);
            if (!file.exists()) {
                try {
                    getDataFolder().mkdir();
                    InputStream defaultConfigFile = getResource(name0 + ".yml");
                    if (defaultConfigFile != null)
                        Files.copy(defaultConfigFile, file.toPath());
                } catch (IOException ex) {
                    this.logger.log(Level.SEVERE, "Can't copy " + name0 + ".yml to data dir", ex);
                }
            }
            return YamlConfiguration.loadConfiguration(file);
        });
    }

    public void reloadConfig() {
        reloadConfig("config");
    }

    public void reloadConfig(String name) {
        this.configs.remove(name);
    }

    public InputStream getResource(String path) {
        try {
            URL url = this.classLoader.getResource(path);
            if (url == null)
                return null;
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Internal
    void setEnabled(boolean flag) {
        if (this.enabled != flag) {
            this.enabled = flag;
            if (this.enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

}
