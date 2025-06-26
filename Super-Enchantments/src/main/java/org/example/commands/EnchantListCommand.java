package org.example.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.example.SuperEnchantments;
import org.example.util.EnchantmentManager;

import java.util.List;

public class EnchantListCommand implements CommandExecutor {

    private final SuperEnchantments plugin;
    private final EnchantmentManager enchantmentManager;

    public EnchantListCommand(SuperEnchantments plugin) {
        this.plugin = plugin;
        this.enchantmentManager = plugin.getEnchantmentManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must hold an item in your hand.");
            return true;
        }

        List<String> availableEnchants = enchantmentManager.getAvailableEnchantments(item);

        if (availableEnchants.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No enchantments are available for this item.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Available enchantments for " +
                           ChatColor.GOLD + item.getType().name() + ChatColor.GREEN + ":");

        for (String enchantName : availableEnchants) {
            player.sendMessage(ChatColor.YELLOW + "• " + ChatColor.WHITE + enchantName);
        }

        player.sendMessage(ChatColor.GREEN + "Use " + ChatColor.GOLD + "/superenchant <Enchantment> <Level>" +
                          ChatColor.GREEN + " to apply an enchantment.");

        return true;
    }
}
