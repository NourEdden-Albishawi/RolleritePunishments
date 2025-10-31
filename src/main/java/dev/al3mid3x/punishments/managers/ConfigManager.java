package dev.al3mid3x.punishments.managers;

import dev.al3mid3x.punishments.Punishments;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final Punishments plugin;
    private FileConfiguration config;

    public ConfigManager(Punishments plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public String getMessage(String path) {
        return config.getString(path);
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
