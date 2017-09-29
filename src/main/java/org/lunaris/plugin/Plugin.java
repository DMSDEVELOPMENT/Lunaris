package org.lunaris.plugin;

import org.lunaris.server.IServer;

/**
 * @author xtrafrancyz
 */
public class Plugin {
    private boolean enabled = false;
    String name;
    String version;
    IServer server;
    PluginClassLoader classLoader;

    protected void onEnable() {
        
    }

    protected void onDisable() {
        
    }

    public IServer getServer() {
        return this.server;
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

    public boolean isEnabled() {
        return this.enabled;
    }
    
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
