package dev.al3mid3x.punishments.commands;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends BaseCommand {

    public KickCommand(Punishments plugin, ConfigManager configManager, Database database) {
        super(plugin, configManager, database);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /kick <player> [reason]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        String reason = configManager.getMessage("messages.kick");
        if (args.length > 1) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reasonBuilder.append(args[i]).append(" ");
            }
            reason = reasonBuilder.toString().trim();
        }

        String finalReason = reason;
        target.kickPlayer(ChatColor.translateAlternateColorCodes('&', finalReason));
        sender.sendMessage(ChatColor.GREEN + "You have kicked " + target.getName() + " for: " + finalReason);

        return true;
    }
}
