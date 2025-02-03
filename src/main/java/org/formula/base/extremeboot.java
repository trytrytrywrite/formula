package org.formula.base;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.formula.trust0logic;
import org.formula.command.main;

public class extremeboot extends JavaPlugin {

    private static extremeboot instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getLogger().info("Formula Plugin загружен!");

        trust0logic.init();

        Bukkit.getPluginManager().registerEvents(new join(), this);
        Bukkit.getPluginManager().registerEvents(new leave(), this);

        getCommand("formula").setExecutor(new main());
        getCommand("formula").setTabCompleter(new main());
    }

    @Override
    public void onDisable() {
        getLogger().info("Formula Plugin выключается...");
    }

    public static extremeboot getInstance() {
        return instance;
    }
}