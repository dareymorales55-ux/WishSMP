package me.deejay.wishsmp;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the WishSMP plugin.
 * Fully expandable for future features.
 */
public final class WishSMP extends JavaPlugin {

    private static WishSMP instance;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("WishSMP Enabled! Ready for expansion.");

        // Future systems, commands, and listeners can be registered here
        // Example placeholders:
        // registerListeners();
        // registerCommands();
        // initializeClasses();
    }

    @Override
    public void onDisable() {
        getLogger().info("WishSMP Disabled! Cleaning up...");
        // Save or cleanup systems when you expand
    }

    /**
     * Returns the main plugin instance.
     */
    public static WishSMP getInstance() {
        return instance;
    }

    // Optional expansion points
    // private void registerListeners() {}
    // private void registerCommands() {}
    // private void initializeClasses() {}
}
