package dev.al3mid3x.punishments.commands;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryCommand extends BaseCommand {

    public HistoryCommand(Punishments plugin, ConfigManager configManager, Database database ) {
        super(plugin, configManager, database);
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /history <player>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        Player player = (Player) sender;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Punishment> punishments = database.getPunishments(target.getUniqueId());

            Bukkit.getScheduler().runTask(plugin, () -> {
                Inventory historyMenu = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', configManager.getMessage("history.title").replace("{player}", target.getName())));

                for (Punishment punishment : punishments) {
                    ItemStack item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + punishment.getType().toString());
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Reason: " + ChatColor.WHITE + punishment.getReason());
                    lore.add(ChatColor.GRAY + "By: " + ChatColor.WHITE + Bukkit.getOfflinePlayer(punishment.getStaff()).getName());
                    lore.add(ChatColor.GRAY + "Date: " + ChatColor.WHITE + new Date(punishment.getTimestamp()));
                    if (punishment.getDuration() != -1) {
                        long remainingTime = punishment.getDuration() - System.currentTimeMillis();
                        if (remainingTime > 0) {
                            lore.add(ChatColor.GRAY + "Remaining: " + ChatColor.WHITE + TimeUtil.formatTime(remainingTime));
                        } else {
                            lore.add(ChatColor.GRAY + "Expired");
                        }
                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    historyMenu.addItem(item);
                }

                player.openInventory(historyMenu);
            });
        });

        return true;
    }
}
