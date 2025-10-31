package dev.al3mid3x.punishments.commands;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.PunishmentType;
import dev.al3mid3x.punishments.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TempBanCommand extends BaseCommand {

    public TempBanCommand(Punishments plugin, ConfigManager configManager, Database database) {
        super(plugin, configManager, database);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /tempban <player> <duration> <reason> [-s]");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        long duration;
        try {
            duration = System.currentTimeMillis() + TimeUtil.parseTime(args[1]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return true;
        }

        String reason = "";
        for (int i = 2; i < args.length; i++) {
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

        long finalDuration = duration;
        String finalReason = reason;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Punishment punishment = new Punishment(target.getUniqueId(), PunishmentType.TEMP_BAN, finalReason, finalDuration, staffUUID, System.currentTimeMillis());
            database.savePunishment(punishment);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (target.isOnline()) {
                    long remainingTime = finalDuration - System.currentTimeMillis();
                    String timeString = TimeUtil.formatTime(remainingTime);
                    ((Player) target).kickPlayer(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.temp_ban").replace("{reason}", finalReason).replace("{time}", timeString)));
                }

                if (!silent) {
                    Bukkit.broadcastMessage(ChatColor.RED + target.getName() + " has been temporarily banned by " + sender.getName() + " for: " + finalReason);
                }
            });
        });

        return true;
    }
}
