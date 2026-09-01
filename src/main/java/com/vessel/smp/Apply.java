package com.vessel.smp;

import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import com.vessel.smp.data.SpiritType;
import org.bukkit.entity.Player;

public class Apply {

    private final PlayerDataManager dataManager;

    public Apply(PlayerDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void refresh(Player player) {
        PlayerData data = dataManager.get(player);
        if (data == null) {
            return;
        }

        SpiritType spirit = data.getSpirit();

        switch (spirit) {
            case WARDEN -> {
                // WardenPassives.apply(player, data);
            }
            case DOLPHIN -> {
                // DolphinPassives.apply(player, data);
            }
            case VILLAGER -> {
                // VillagerPassives.apply(player, data);
            }
            case TURTLE -> {
                // TurtlePassives.apply(player, data);
            }
        }
    }
}
