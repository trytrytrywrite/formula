package org.formula.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.formula.trust0logic;

import java.text.DecimalFormat;
import java.util.*;

public class serverstat implements CommandExecutor, TabCompleter {

    private final DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("formula.admin")) {
            sender.sendMessage(ChatColor.RED + "У вас нет прав на эту команду.");
            return true;
        }

        int totalPlayers = Bukkit.getOfflinePlayers().length;
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        double avgPing = 0;
        double avgTrust = 0;
        int vpnUsers = 0;

        Map<String, Integer> countryStats = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            avgPing += player.getPing();
            avgTrust += trust0logic.getTrustScore(player.getName());

            String country = player.getLocale();
            countryStats.put(country, countryStats.getOrDefault(country, 0) + 1);
        }

        avgPing = (onlinePlayers > 0) ? avgPing / onlinePlayers : 0;
        avgTrust = (onlinePlayers > 0) ? avgTrust / onlinePlayers : 0;

        sender.sendMessage(ChatColor.AQUA + "=========[ 📊 Статистика сервера ]=========");
        sender.sendMessage(ChatColor.BLUE + "👥 Всего игроков: " + ChatColor.YELLOW + totalPlayers);
        sender.sendMessage(ChatColor.BLUE + "🔹 Онлайн: " + ChatColor.YELLOW + onlinePlayers);
        sender.sendMessage(ChatColor.BLUE + "📡 Средний пинг: " + ChatColor.YELLOW + df.format(avgPing) + " мс");
        sender.sendMessage(ChatColor.BLUE + "🔐 Средний Trust Score: " + ChatColor.YELLOW + df.format(avgTrust));
        sender.sendMessage(ChatColor.BLUE + "🌍 Топ стран: ");

        countryStats.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> sender.sendMessage(ChatColor.DARK_AQUA + " - " + entry.getKey() + ": " + ChatColor.YELLOW + entry.getValue()));

        sender.sendMessage(ChatColor.AQUA + "=====================================");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}