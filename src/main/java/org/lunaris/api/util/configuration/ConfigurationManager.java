package org.lunaris.api.util.configuration;

import org.lunaris.server.IServer;
import org.lunaris.api.util.configuration.yaml.LuvasConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 21.04.17.
 */
public class ConfigurationManager {
    
    private final static Map<String, LuvasConfiguration> configs = new HashMap<>();
    private final static String DEFAULT_CONFIG_NAME = "lunaris";

    public ConfigurationManager(IServer server) {}

    public ConfigurationGroup getConfigurationGroup(String ancestor) {
        return new ConfigurationGroup(this, ancestor);
    }

    public FileConfiguration getConfig() {
        return getLConfig(DEFAULT_CONFIG_NAME).get();
    }
    
    public void saveConfig() {
        getLConfig(DEFAULT_CONFIG_NAME).save();
    }
    
    public void reloadConfig() {
        getLConfig(DEFAULT_CONFIG_NAME).reload();
    }
    
    static LuvasConfiguration getLConfig(String name) {
        LuvasConfiguration lc = configs.get(name);
        if(lc != null)
            return lc;
        lc = new LuvasConfiguration(name);
        configs.put(name, lc);
        return lc;
    }
    
}
