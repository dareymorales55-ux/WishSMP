package com.vessel.smp.listeners;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import com.vessel.smp.items.Essence;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

    private static final TextColor LIGHT_PURPLE = TextColor.color(0xE6CCFF);
    private static final TextColor PURPLE = TextColor.color(0xB266FF);

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

        Component message;
        if (wasAboveFloor) {
            message = gradient("ʏᴏᴜ ʟᴏꜱᴛ ᴀɴ ᴇꜱꜱᴇɴᴄᴇ, ʏᴏᴜ ᴀʀᴇ ɴᴏᴡ ᴀᴛ (", LIGHT_PURPLE, PURPLE, true)
                    .append(Component.text(String.valueOf(data.getEssenceLevel())))
                    .append(gradient(")", LIGHT_PURPLE, PURPLE, true));
        } else {
            message = gradient("ʏᴏᴜ ʟᴏꜱᴛ ᴀɴ ᴇꜱꜱᴇɴᴄᴇ, ʏᴏᴜ ʀᴇᴍᴀɪɴ ᴀᴛ (", LIGHT_PURPLE, PURPLE, true)
                    .append(Component.text(String.valueOf(PlayerData.FLOOR)))
                    .append(gradient(")", LIGHT_PURPLE, PURPLE, true));
        }

        player.sendMessage(message);
    }

    private static Component gradient(String text, TextColor start, TextColor end, boolean bold) {
        Component result = Component.empty();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);
            TextColor stepColor = TextColor.lerp(ratio, start, end);

            result = result.append(Component.text(String.valueOf(text.charAt(i)))
                    .color(stepColor)
                    .decoration(TextDecoration.BOLD, bold)
                    .decoration(TextDecoration.ITALIC, false));
        }

        return result;
    }
}
