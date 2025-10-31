package dev.al3mid3x.punishments.commands;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class BaseCommand implements CommandExecutor {

    protected final Punishments plugin;
    protected final ConfigManager configManager;
    protected final Database database;

    public BaseCommand(Punishments plugin, ConfigManager configManager, Database database) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.database = database;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return execute(sender, command, label, args);
    }

    public abstract boolean execute(CommandSender sender, Command command, String label, String[] args);
}
