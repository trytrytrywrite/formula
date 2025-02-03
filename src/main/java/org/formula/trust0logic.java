package org.formula;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.formula.base.extremeboot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class trust0logic {

    private static final extremeboot plugin = extremeboot.getInstance();
    private static final File file = new File(plugin.getDataFolder(), "trust_scores.yml");
    private static FileConfiguration trustData;
    private static final Map<String, Integer> cache = new HashMap<>();

    private static final int DEFAULT_TRUST = 100;
    private static final int VPN_PENALTY = 20;
    private static final int FREQUENT_IP_CHANGE_PENALTY = 10;

    public static void init() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        trustData = YamlConfiguration.loadConfiguration(file);
    }

    public static int getTrustScore(String playerName) {
        if (cache.containsKey(playerName)) {
            return cache.get(playerName);
        }
        int trust = trustData.getInt(playerName, DEFAULT_TRUST);
        cache.put(playerName, trust);
        return trust;
    }

    public static void modifyTrustScore(String playerName, int amount) {
        int newTrust = Math.max(0, getTrustScore(playerName) + amount);
        trustData.set(playerName, newTrust);
        cache.put(playerName, newTrust);
        save();
    }

    public static void applyVpnPenalty(String playerName) {
        modifyTrustScore(playerName, -VPN_PENALTY);
    }

    public static void checkIpChange(String playerName, String newIP, String newISP, String newCity) {
        File playerFile = new File(plugin.getDataFolder() + "/players", playerName + ".yml");
        if (!playerFile.exists()) return;

        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);
        List<String> oldIPs = data.getStringList("ip_addresses");
        String lastISP = data.getString("isp", "UNKNOWN");
        String lastCity = data.getString("city", "UNKNOWN");

        if (!oldIPs.contains(newIP)) {
            if (!lastISP.equalsIgnoreCase(newISP) && !lastCity.equalsIgnoreCase(newCity)) {
                modifyTrustScore(playerName, -FREQUENT_IP_CHANGE_PENALTY);
            }
            oldIPs.add(newIP);
            data.set("ip_addresses", oldIPs);
            data.set("isp", newISP);
            data.set("city", newCity);
            savePlayerData(data, playerFile);
        }
    }

    private static void savePlayerData(FileConfiguration data, File file) {
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void save() {
        try {
            trustData.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}