package com.vessel.smp.listeners;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FirstJoinListener implements Listener {

    private final PlayerDataManager dataManager;
    private final Apply apply;

    public FirstJoinListener(PlayerDataManager dataManager, Apply apply) {
        this.dataManager = dataManager;
        this.apply = apply;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = dataManager.loadOrCreate(player);
        apply.refresh(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        dataManager.unload(event.getPlayer());
    }
}
