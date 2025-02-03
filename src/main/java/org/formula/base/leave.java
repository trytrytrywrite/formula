package org.formula.base;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class leave implements Listener {

    private final extremeboot plugin = extremeboot.getInstance();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        long quitTime = System.currentTimeMillis();

        File playerFile = new File(plugin.getDataFolder() + "/players", player.getName() + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

        data.set("lastQuitTime", quitTime);
        data.set("lastQuitDate", new Date().toString());

        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}