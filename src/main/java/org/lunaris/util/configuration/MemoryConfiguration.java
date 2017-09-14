package org.lunaris.util.configuration;

import org.lunaris.util.Validate;

import java.util.Map;

/**
 * Created by RINES on 21.04.17.
 */
public class MemoryConfiguration extends MemorySection implements Configuration {
    
    protected Configuration defaults;
    protected ConfigurationOptions options;

    public MemoryConfiguration() {

    }
    
    public MemoryConfiguration(Configuration defaults) {
        this.defaults = defaults;
    }
    
    @Override
    public void addDefault(String path, Object value) {
        Validate.notNull(path, "Path can't be null");
        if(defaults == null)
            defaults = new MemoryConfiguration();
        defaults.set(path, value);
    }
    
    @Override
    public ConfigurationSection getParent() {
        return null;
    }

    @Override
    public void addDefaults(Map<String, Object> defaults) {
        Validate.notNull(defaults, "Defaults can't be null");
        defaults.entrySet().forEach(e -> addDefault(e.getKey(), e.getValue()));
    }

    @Override
    public void addDefaults(Configuration defaults) {
        Validate.notNull(defaults, "Defaults can't be null");
        addDefaults(defaults.getValues(true));
    }

    @Override
    public void setDefaults(Configuration defaults) {
        Validate.notNull(defaults, "Defaults can't be null");
        this.defaults = defaults;
    }

    @Override
    public Configuration getDefaults() {
        return this.defaults;
    }

    @Override
    public ConfigurationOptions options() {
        if(options == null)
            options = new ConfigurationOptions(this);
        return options;
    }

}
