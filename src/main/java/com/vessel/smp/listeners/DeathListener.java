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

import java.util.Map;

public class DeathListener implements Listener {

    private static final TextColor LIGHT_PURPLE = TextColor.color(0xE6CCFF);
    private static final TextColor PURPLE = TextColor.color(0xB266FF);

    private static final Map<Character, Character> SMALL_CAPS = Map.ofEntries(
            Map.entry('a', 'ᴀ'), Map.entry('b', 'ʙ'), Map.entry('c', 'ᴄ'),
            Map.entry('d', 'ᴅ'), Map.entry('e', 'ᴇ'), Map.entry('f', 'ꜰ'),
            Map.entry('g', 'ɢ'), Map.entry('h', 'ʜ'), Map.entry('i', 'ɪ'),
            Map.entry('j', 'ᴊ'), Map.entry('k', 'ᴋ'), Map.entry('l', 'ʟ'),
            Map.entry('m', 'ᴍ'), Map.entry('n', 'ɴ'), Map.entry('o', 'ᴏ'),
            Map.entry('p', 'ᴘ'), Map.entry('q', 'ǫ'), Map.entry('r', 'ʀ'),
            Map.entry('s', 'ꜱ'), Map.entry('t', 'ᴛ'), Map.entry('u', 'ᴜ'),
            Map.entry('v', 'ᴠ'), Map.entry('w', 'ᴡ'), Map.entry('x', 'x'),
            Map.entry('y', 'ʏ'), Map.entry('z', 'ᴢ')
    );

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

        String message = wasAboveFloor
                ? "You lost an essence, you are now at (" + data.getEssenceLevel() + ")"
                : "You lost an essence, you remain at (" + PlayerData.FLOOR + ")";

        player.sendMessage(smallCapsGradient(message, LIGHT_PURPLE, PURPLE, true));
    }

    private static String toSmallCaps(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(SMALL_CAPS.getOrDefault(Character.toLowerCase(c), c));
        }
        return result.toString();
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

    private static Component smallCapsGradient(String text, TextColor start, TextColor end, boolean bold) {
        return gradient(toSmallCaps(text), start, end, bold);
    }
}
