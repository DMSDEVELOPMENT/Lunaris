package org.lunaris.api.util.configuration;

/**
 * Created by RINES on 21.04.17.
 */
public class ConfigurationOptions {

    private char pathSeparator = '.';
    private boolean copyDefaults = false;
    private final Configuration configuration;

    public ConfigurationOptions(Configuration configuration) {
        this.configuration = configuration;
    }

    public char getPathSeparator() {
        return pathSeparator;
    }

    public void setPathSeparator(char pathSeparator) {
        this.pathSeparator = pathSeparator;
    }

    public boolean isCopyDefaults() {
        return copyDefaults;
    }

    public void setCopyDefaults(boolean copyDefaults) {
        this.copyDefaults = copyDefaults;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
