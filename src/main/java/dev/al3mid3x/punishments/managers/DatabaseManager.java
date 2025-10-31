package dev.al3mid3x.punishments.managers;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.database.provider.MongoDBManager;
import dev.al3mid3x.punishments.database.provider.MySQLManager;
import dev.al3mid3x.punishments.database.provider.SQLiteManager;

public class DatabaseManager {

    private final Database database;

    public DatabaseManager(Punishments plugin, ConfigManager configManager) {
        String databaseType = configManager.getString("database.type", "sqlite");

        if (databaseType.equalsIgnoreCase("sqlite")) {
            database = new SQLiteManager(plugin, configManager);
        } else if (databaseType.equalsIgnoreCase("mysql")) {
            database = new MySQLManager(plugin, configManager);
        } else if (databaseType.equalsIgnoreCase("mongodb")) {
            database = new MongoDBManager(plugin, configManager);
        } else {
            throw new RuntimeException("Invalid database type specified in the configuration!");
        }
    }

    public Database getDatabase() {
        return database;
    }
}
