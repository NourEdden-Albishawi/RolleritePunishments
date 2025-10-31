package dev.al3mid3x.punishments.commands;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnbanCommand extends BaseCommand {

    public UnbanCommand(Punishments plugin, ConfigManager configManager, Database database) {
        super(plugin, configManager, database);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /unban <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Punishment punishment = database.getPunishment(target.getUniqueId(), PunishmentType.BAN);
            if (punishment == null) {
                punishment = database.getPunishment(target.getUniqueId(), PunishmentType.TEMP_BAN);
            }

            if (punishment != null) {
                database.removePunishment(punishment);
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(target.getName());
                sender.sendMessage(ChatColor.GREEN + "You have unbanned " + target.getName());
            });
        });

        return true;
    }
}
