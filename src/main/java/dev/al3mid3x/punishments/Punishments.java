package dev.al3mid3x.punishments;

import dev.al3mid3x.punishments.commands.*;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.listeners.PlayerListener;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.managers.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Punishments extends JavaPlugin {

    private Database database;

    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this);
        configManager.loadConfig();

        DatabaseManager databaseManager = new DatabaseManager(this, configManager);
        database = databaseManager.getDatabase();
        database.connect();

        getCommand("kick").setExecutor(new KickCommand(this, configManager, database));
        getCommand("ban").setExecutor(new BanCommand(this, configManager, database));
        getCommand("tempban").setExecutor(new TempBanCommand(this, configManager, database));
        getCommand("mute").setExecutor(new MuteCommand(this, configManager, database));
        getCommand("tempmute").setExecutor(new TempMuteCommand(this, configManager, database));
        getCommand("ipban").setExecutor(new IPBanCommand(this, configManager, database));
        getCommand("unban").setExecutor(new UnbanCommand(this, configManager, database));
        getCommand("unmute").setExecutor(new UnmuteCommand(this, configManager, database));
        getCommand("history").setExecutor(new HistoryCommand(this, configManager, database));

        getServer().getPluginManager().registerEvents(new PlayerListener(this, database, configManager), this);
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.disconnect();
        }
    }
}
