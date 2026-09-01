package com.vessel.smp.listeners;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import com.vessel.smp.items.Essence;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

    private final PlayerDataManager dataManager;
    private final Apply apply;

    public DeathListener(PlayerDataManager dataManager, Apply apply) {
        this.dataManager = dataManager;
        this.apply = apply;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PlayerData data = dataManager.get(player);
        if (data == null) {
            return;
        }

        boolean wasAboveFloor = data.applyDeathPenalty();

        if (wasAboveFloor) {
            ItemStack essenceDrop = Essence.create();
            event.getDrops().add(essenceDrop);
        }

        dataManager.save(data);
        apply.refresh(player);
    }
}
