package org.formula.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class mapnobool implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("formula.admin")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на эту команду.");
            return true;
        }

        Map<String, Integer> countryStats = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            String country = player.getLocale();
            countryStats.put(country, countryStats.getOrDefault(country, 0) + 1);
        }

        sender.sendMessage(ChatColor.AQUA + "=========[ 🌍 Карта игроков ]=========");

        countryStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String country = entry.getKey();
                    int count = entry.getValue();

                    StringBuilder barBuilder = new StringBuilder();
                    for (int i = 0; i < Math.min(count, 10); i++) {
                        barBuilder.append("█");
                    }
                    String bar = ChatColor.YELLOW + "[" + ChatColor.GREEN + barBuilder + ChatColor.GRAY + " " + count + ChatColor.YELLOW + "]";

                    sender.sendMessage(ChatColor.BLUE + " " + country + ": " + bar);
                });

        sender.sendMessage(ChatColor.AQUA + "=====================================");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}