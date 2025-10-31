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

public class IPBanCommand extends BaseCommand {

    public IPBanCommand(Punishments plugin, ConfigManager configManager, Database database) {
        super(plugin, configManager, database);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /ipban <player> <reason> [-s]");
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
            String ip = database.getPlayerIP(target.getUniqueId());
            if (ip == null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(ChatColor.RED + "Could not find an IP address for that player.");
                });
                return;
            }

            Punishment punishment = new Punishment(target.getUniqueId(), PunishmentType.IP_BAN, finalReason, -1, staffUUID, System.currentTimeMillis());
            database.savePunishment(punishment);

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(ip, finalReason, null, sender.getName());

                if (target.isOnline()) {
                    ((Player) target).kickPlayer(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.ip_ban").replace("{reason}", finalReason)));
                }

                if (!silent) {
                    Bukkit.broadcastMessage(ChatColor.RED + target.getName() + " has been IP banned by " + sender.getName() + " for: " + finalReason);
                }
            });
        });

        return true;
    }
}
