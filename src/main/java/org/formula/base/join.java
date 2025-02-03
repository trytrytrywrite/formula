package org.formula.base;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.formula.trust0logic;
import org.formula.vpncheck;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class join implements Listener {

    private final extremeboot plugin = extremeboot.getInstance();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String ip = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "UNKNOWN";
        int ping = player.getPing();
        String locale = player.getLocale();
        long joinTime = System.currentTimeMillis();

        File playerFile = new File(plugin.getDataFolder() + "/players", name + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

        List<String> ipList = data.getStringList("ip_addresses");
        if (!ipList.contains(ip)) {
            ipList.add(ip);
        }

        boolean isCracked = !Bukkit.getOnlineMode();
        data.set("isCracked", isCracked);

        boolean isVPN = vpncheck.isVPN(ip);

        JSONObject ipInfo = vpncheck.getIPDetails(ip);
        String country = ipInfo.optString("country", "UNKNOWN");
        String region = ipInfo.optString("region", "UNKNOWN");
        String city = ipInfo.optString("city", "UNKNOWN");
        String isp = ipInfo.optString("isp", "UNKNOWN");

        List<Integer> openPorts = scanOpenPorts(ip, new int[]{80, 443, 25565, 22, 21});

        if (isVPN) {
            trust0logic.applyVpnPenalty(name);
        }

        trust0logic.checkIpChange(name, ip, isp, city);

        data.set("name", name);
        data.set("ip_addresses", ipList);
        data.set("lastIP", ip);
        data.set("ping", ping);
        data.set("locale", locale);
        data.set("country", country);
        data.set("region", region);
        data.set("city", city);
        data.set("isp", isp);
        data.set("isVPN", isVPN);
        data.set("open_ports", openPorts);
        data.set("lastJoinTime", joinTime);
        data.set("lastJoinDate", new Date().toString());

        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (plugin.getConfig().getBoolean("kick-on-vpn") && isVPN) {
            player.kickPlayer(ChatColor.RED + "Вы были кикнуты за использование VPN/Proxy.");
            Bukkit.getLogger().warning("[Formula] Игрок " + name + " был кикнут за использование VPN!");
            return;
        }

        if (plugin.getConfig().getBoolean("kick-on-low-trust")) {
            int trustScore = trust0logic.getTrustScore(name);
            if (trustScore < plugin.getConfig().getInt("min-trust-score", 20)) {
                player.kickPlayer(ChatColor.RED + "Ваш уровень доверия слишком низкий!");
                Bukkit.getLogger().warning("[Formula] Игрок " + name + " был кикнут за низкий Trust Score (" + trustScore + ")");
                return;
            }
        }
    }

    private List<Integer> scanOpenPorts(String ip, int[] ports) {
        List<Integer> openPorts = new ArrayList<>();
        for (int port : ports) {
            threadPool.execute(() -> {
                if (isPortOpen(ip, port, 500)) {
                    synchronized (openPorts) {
                        openPorts.add(port);
                    }
                }
            });
        }
        return openPorts;
    }

    private boolean isPortOpen(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}