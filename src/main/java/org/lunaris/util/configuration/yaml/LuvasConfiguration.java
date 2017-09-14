package org.lunaris.util.configuration.yaml;

import org.lunaris.Lunaris;
import org.lunaris.util.configuration.FileConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by RINES on 21.04.17.
 */
public class LuvasConfiguration {

    private final String name;
    private final File file;
    private FileConfiguration config;

    public LuvasConfiguration(String name) {
        this.name = name;
        this.file = new File(name + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration get() {
        if(config == null)
            this.reload();
        return config;
    }

    public void save() {
        if(file == null || config == null)
            return;
        try {
            get().save(file);
        }catch (IOException ex) {
            Lunaris.getInstance().getLogger().warn(ex, "Can not save configuration %s", getName());
        }
    }

    public String getName() {
        return name;
    }
}
