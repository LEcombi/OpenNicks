package de.prime4g.openNicks;

import de.prime4g.openNicks.Database.DatabaseManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpenNicks extends JavaPlugin {
    public static OpenNicks instance;
    private FileConfiguration config;
    public DatabaseManager dbManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Save default config before loading it
        this.config = getConfig();
        // Connect to the database
        try {
            dbManager = new DatabaseManager(this);
        } catch (Exception e) {
            getLogger().severe("Failed to connect to the database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create the nick table if it doesn't exist
        try {
            dbManager.createNickTableIfNotExists();
        } catch (Exception e) {
            getLogger().severe("Failed to create nick table: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register commands and listeners
        getCommand("nickname").setExecutor(new de.prime4g.openNicks.nick.NickCommand());
        getServer().getPluginManager().registerEvents(new de.prime4g.openNicks.nick.NickListener(), this);

        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new OpenNicksExpansion(this).register();
        }
        getLogger().info("OpenNicks has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OpenNicks has been disabled!");
    }

    public static OpenNicks getInstance() {
        return instance;
    }
}
