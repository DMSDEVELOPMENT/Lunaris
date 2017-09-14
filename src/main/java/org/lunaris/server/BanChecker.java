package org.lunaris.server;

import org.lunaris.util.configuration.ConfigurationGroup;
import org.lunaris.util.configuration.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by RINES on 14.09.17.
 */
public class BanChecker {

    private final ConfigurationGroup group;
    private final FileConfiguration config;

    private final Set<String> bannedNames = new HashSet<>();
    private final Set<String> bannedIps = new HashSet<>();
    private final Set<UUID> bannedUUIDs = new HashSet<>();

    public BanChecker(IServer server) {
        this.group = server.getConfigurationManager().getConfigurationGroup("");
        this.config = this.group.getConfig("bans");
        if(this.config.isSet("names"))
            this.config.getStringList("names").forEach(name -> this.bannedNames.add(name.toLowerCase()));
        else
            this.config.set("names", new ArrayList<String>());

        if(this.config.isSet("ips"))
            this.config.getStringList("ips").forEach(this.bannedIps::add);
        else
            this.config.set("ips", new ArrayList<String>());

        if(this.config.isSet("uuids"))
            this.config.getStringList("uuids").forEach(uuid -> this.bannedUUIDs.add(UUID.fromString(uuid)));
        else
            this.config.set("uuids", new ArrayList<String>());

        saveConfig();
    }

    public boolean isNameBanned(String name) {
        return this.bannedNames.contains(name.toLowerCase());
    }

    public boolean isAddressBanned(String ip) {
        return this.bannedIps.contains(ip);
    }

    public boolean isUUIDBanned(UUID uuid) {
        return this.bannedUUIDs.contains(uuid);
    }

    public void ban(String name) {
        if(this.bannedNames.add(name.toLowerCase())) {
            this.config.set("names", new ArrayList<>(this.bannedNames));
            saveConfig();
        }
    }

    public void unban(String name) {
        if(this.bannedNames.remove(name.toLowerCase())) {
            this.config.set("names", new ArrayList<>(this.bannedNames));
            saveConfig();
        }
    }

    public void banIP(String ip) {
        if(this.bannedIps.add(ip)) {
            this.config.set("ips", new ArrayList<>(this.bannedIps));
            saveConfig();
        }
    }

    public void unbanIP(String ip) {
        if(this.bannedIps.remove(ip)) {
            this.config.set("ips", new ArrayList<>(this.bannedIps));
            saveConfig();
        }
    }

    public void ban(UUID uuid) {
        if(this.bannedUUIDs.add(uuid)) {
            this.config.set("uuids", this.bannedUUIDs.stream().map(UUID::toString).collect(Collectors.toList()));
            saveConfig();
        }
    }

    public void unban(UUID uuid) {
        if(this.bannedUUIDs.remove(uuid)) {
            this.config.set("uuids", this.bannedUUIDs.stream().map(UUID::toString).collect(Collectors.toList()));
            saveConfig();
        }
    }

    private void saveConfig() {
        this.group.saveConfig("bans");
    }

}
