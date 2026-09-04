package com.vessel.smp.items;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class Essence implements Listener {

    private static final String IDENTIFIER_KEY = "vessel_essence";

    private static final TextColor LIGHT_PURPLE = TextColor.color(0xE6CCFF);
    private static final TextColor PURPLE = TextColor.color(0xB266FF);
    private static final TextColor DARK_PURPLE = TextColor.color(0x4B0082);

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

    public Essence(PlayerDataManager dataManager, Apply apply) {
        this.dataManager = dataManager;
        this.apply = apply;
    }

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(gradient("Essence", PURPLE, DARK_PURPLE, true));
        meta.lore(List.of(gradient("Right-click to gain +1 Essence", LIGHT_PURPLE, PURPLE, false)));

        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(
                new NamespacedKey("vessel", IDENTIFIER_KEY),
                PersistentDataType.BOOLEAN,
                true
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isEssence(ItemStack item) {
        if (item == null || item.getType() != Material.GHAST_TEAR || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey("vessel", IDENTIFIER_KEY),
                PersistentDataType.BOOLEAN
        );
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack held = event.getItem();
        if (!isEssence(held)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerData data = dataManager.get(player);
        if (data == null) {
            return;
        }

        event.setCancelled(true);

        if (data.isFloored()) {
            player.sendMessage(smallCapsGradient(
                    "You're disconnected from your Spirit -- recover first.", LIGHT_PURPLE, PURPLE, true));
            return;
        }

        boolean absorbed = data.absorb();
        if (!absorbed) {
            player.sendMessage(smallCapsGradient(
                    "You're already at maximum Essence.", LIGHT_PURPLE, PURPLE, true));
            return;
        }

        if (held.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            held.setAmount(held.getAmount() - 1);
        }

        dataManager.save(data);
        apply.refresh(player);

        player.sendMessage(smallCapsGradient(
                "You gained an essence, you are now at (" + data.getEssenceLevel() + ")",
                LIGHT_PURPLE, PURPLE, true));
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
