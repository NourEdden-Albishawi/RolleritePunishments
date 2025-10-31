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
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand extends BaseCommand {

    public BanCommand(Punishments plugin, ConfigManager configManager, Database database) {
        super(plugin, configManager, database);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ban <player> <reason> [-s]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += args[i] + " ";
        }

        boolean silent = reason.contains("-s");
        if (silent) {
            reason = reason.replace("-s", "").trim();
        }

        UUID staffUUID;
        if (sender instanceof Player) {
            staffUUID = ((Player) sender).getUniqueId();
        } else {
            staffUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        }

        String finalReason = reason;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Punishment punishment = new Punishment(target.getUniqueId(), PunishmentType.BAN, finalReason, -1, staffUUID, System.currentTimeMillis());
            database.savePunishment(punishment);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (target.isOnline()) {
                    ((Player) target).kickPlayer(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.ban").replace("{reason}", finalReason)));
                }

                if (!silent) {
                    Bukkit.broadcastMessage(ChatColor.RED + target.getName() + " has been banned by " + sender.getName() + " for: " + finalReason);
                }
            });
        });

        return true;
    }
}
