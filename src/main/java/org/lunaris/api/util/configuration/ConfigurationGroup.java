package org.lunaris.api.util.configuration;

import java.io.File;

/**
 * Created by RINES on 13.09.17.
 */
public class ConfigurationGroup {

    private final ConfigurationManager manager;
    private final String ancestor;

    public ConfigurationGroup(ConfigurationManager manager, String ancestor) {
        this.manager = manager;
        this.ancestor = ancestor.isEmpty() ? "" : ancestor + '/';
        File ancestorDirectory = new File(ancestor);
        if (!ancestorDirectory.exists())
            ancestorDirectory.mkdirs();
    }

    public FileConfiguration getConfig(String name) {
        return manager.getLConfig(this.ancestor + name).get();
    }

    public void saveConfig(String name) {
        manager.getLConfig(this.ancestor + name).save();
    }

    public void reloadConfig(String name) {
        manager.getLConfig(this.ancestor + name).reload();
    }

}
