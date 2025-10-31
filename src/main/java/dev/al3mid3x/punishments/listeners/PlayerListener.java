package dev.al3mid3x.punishments.listeners;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.PunishmentType;
import dev.al3mid3x.punishments.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final Punishments plugin;
    private final Database database;
    private final ConfigManager configManager;

    public PlayerListener(Punishments plugin, Database database, ConfigManager configManager) {
        this.plugin = plugin;
        this.database = database;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String ip = player.getAddress().getAddress().getHostAddress();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            database.savePlayerIP(player.getUniqueId(), ip);

            Punishment ban = database.getPunishment(player.getUniqueId(), PunishmentType.BAN);
            if (ban != null) {
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.ban").replace("{reason}", ban.getReason()))));
                return;
            }

            Punishment tempBan = database.getPunishment(player.getUniqueId(), PunishmentType.TEMP_BAN);
            if (tempBan != null) {
                long remainingTime = tempBan.getDuration() - System.currentTimeMillis();
                String timeString = TimeUtil.formatTime(remainingTime);
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.temp_ban").replace("{reason}", tempBan.getReason()).replace("{time}", timeString))));
                return;
            }

            Punishment ipBan = database.getPunishment(player.getUniqueId(), PunishmentType.IP_BAN);
            if (ipBan != null) {
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.ip_ban").replace("{reason}", ipBan.getReason()))));
            }
        });
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Punishment mute = database.getPunishment(player.getUniqueId(), PunishmentType.MUTE);
        if (mute != null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.mute").replace("{reason}", mute.getReason())));
            return;
        }

        Punishment tempMute = database.getPunishment(player.getUniqueId(), PunishmentType.TEMP_MUTE);
        if (tempMute != null) {
            event.setCancelled(true);
            long remainingTime = tempMute.getDuration() - System.currentTimeMillis();
            String timeString = TimeUtil.formatTime(remainingTime);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("messages.temp_mute").replace("{reason}", tempMute.getReason()).replace("{time}", timeString)));
        }
    }
}
