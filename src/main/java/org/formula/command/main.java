package org.formula.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.formula.base.extremeboot;
import org.formula.trust0logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class main implements CommandExecutor, TabCompleter {

    private final extremeboot plugin = extremeboot.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "trust":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /formula trust <ник>");
                    return true;
                }
                showTrustScore(sender, args[1]);
                break;

            case "info":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Использование: /formula info <ник>");
                    return true;
                }
                showPlayerInfo(sender, args[1]);
                break;

            case "serverinfo":
                new serverstat().onCommand(sender, command, label, args);
                break;

            case "map":
                new mapnobool().onCommand(sender, command, label, args);
                break;

            default:
                showHelp(sender);
                break;
        }

        return true;
    }

    private void showTrustScore(CommandSender sender, String playerName) {
        int trustScore = trust0logic.getTrustScore(playerName);
        sender.sendMessage(ChatColor.AQUA + "Уровень доверия игрока " + playerName + ": " + ChatColor.YELLOW + trustScore);
    }

    private void showPlayerInfo(CommandSender sender, String playerName) {
        File playerFile = new File(plugin.getDataFolder() + "/players", playerName + ".yml");
        if (!playerFile.exists()) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден!");
            return;
        }

        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
        String lastIP = data.getString("lastIP", "UNKNOWN");
        String country = data.getString("country", "UNKNOWN");
        String city = data.getString("city", "UNKNOWN");
        String isp = data.getString("isp", "UNKNOWN");
        boolean isVPN = data.getBoolean("isVPN", false);
        boolean isCracked = data.getBoolean("isCracked", false);
        int trustScore = trust0logic.getTrustScore(playerName);
        List<String> ipAddresses = data.getStringList("ip_addresses");

        sender.sendMessage(ChatColor.BLUE + "=========[ " + ChatColor.AQUA + playerName + ChatColor.BLUE + " ]=========");
        sender.sendMessage(ChatColor.AQUA + "Последний IP: " + ChatColor.BLUE + lastIP);
        sender.sendMessage(ChatColor.AQUA + "Страна: " + ChatColor.BLUE + country);
        sender.sendMessage(ChatColor.AQUA + "Город: " + ChatColor.BLUE + city);
        sender.sendMessage(ChatColor.AQUA + "Провайдер: " + ChatColor.BLUE + isp);
        sender.sendMessage(ChatColor.AQUA + "VPN/Proxy: " + (isVPN ? ChatColor.RED + "ДА" : ChatColor.GREEN + "НЕТ"));
        sender.sendMessage(ChatColor.AQUA + "Клиент: " + (isCracked ? ChatColor.RED + "Пиратка" : ChatColor.GREEN + "Лицензия"));
        sender.sendMessage(ChatColor.AQUA + "Trust Score: " + ChatColor.YELLOW + trustScore);
        sender.sendMessage(ChatColor.AQUA + "Список IP:");
        for (String ip : ipAddresses) {
            sender.sendMessage(ChatColor.DARK_AQUA + "- " + ChatColor.GRAY + ip);
        }
        sender.sendMessage(ChatColor.BLUE + "===============================");
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=========[ 📜 Команды Formula ]=========");
        sender.sendMessage(ChatColor.YELLOW + "/formula trust <ник>" + ChatColor.GRAY + " - Проверить Trust Score игрока");
        sender.sendMessage(ChatColor.YELLOW + "/formula info <ник>" + ChatColor.GRAY + " - Посмотреть информацию о игроке");
        sender.sendMessage(ChatColor.YELLOW + "/formula serverinfo" + ChatColor.GRAY + " - Статистика сервера");
        sender.sendMessage(ChatColor.YELLOW + "/formula map" + ChatColor.GRAY + " - Графический вывод стран");
        sender.sendMessage(ChatColor.AQUA + "=====================================");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("trust");
            completions.add("info");
            completions.add("serverinfo");
            completions.add("map");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("trust") || args[0].equalsIgnoreCase("info"))) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        }

        return completions;
    }
}