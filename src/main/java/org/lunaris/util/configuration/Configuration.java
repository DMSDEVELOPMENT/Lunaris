package org.lunaris.util.configuration;

import java.util.Map;

/**
 * Created by RINES on 21.04.17.
 */
public interface Configuration extends ConfigurationSection {

    void addDefaults(Map<String, Object> defaults);

    void addDefaults(Configuration defaults);

    void setDefaults(Configuration defaults);

    Configuration getDefaults();

    ConfigurationOptions options();

}
