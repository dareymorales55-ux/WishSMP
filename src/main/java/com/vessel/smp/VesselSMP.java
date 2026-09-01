package com.vessel.smp;

import com.vessel.smp.data.PlayerDataManager;
import com.vessel.smp.listeners.DeathListener;
import com.vessel.smp.listeners.FirstJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class VesselSMP extends JavaPlugin {

    private PlayerDataManager playerDataManager;
    private Apply apply;

    @Override
    public void onEnable() {
        this.playerDataManager = new PlayerDataManager(this);
        this.apply = new Apply(playerDataManager);

        registerListeners();
        registerCommands();

        getLogger().info("Vessel SMP enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Vessel SMP disabled.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new FirstJoinListener(playerDataManager, apply), this);
        getServer().getPluginManager().registerEvents(
                new DeathListener(playerDataManager, apply), this);
    }

    private void registerCommands() {
        // Command registrations added as each command file is written
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public Apply getApply() {
        return apply;
    }
}
