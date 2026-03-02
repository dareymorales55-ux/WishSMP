package me.deejay.wishsmp;

import me.deejay.wishsmp.classes.ClassManager;
import me.deejay.wishsmp.listeners.PlayerJoinListener;
import me.deejay.wishsmp.items.Essence; // <-- ADD THIS
import org.bukkit.plugin.java.JavaPlugin;

public class WishSMP extends JavaPlugin {

    private ClassManager classManager;

    @Override
    public void onEnable() {

        // Initialize ClassManager
        this.classManager = new ClassManager(this);

        // Register join listener
        getServer().getPluginManager().registerEvents(
                new PlayerJoinListener(this),
                this
        );

        // Register Essence listener
        getServer().getPluginManager().registerEvents(
                new Essence(this),
                this
        );

        getLogger().info("WishSMP enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("WishSMP disabled!");
    }

    public ClassManager getClassManager() {
        return classManager;
    }
}
