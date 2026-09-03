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

/**
 * The Essence currency item. Placeholder visual: an enchanted Ghast Tear,
 * until the real custom player-head textures are ready.
 *
 * Right-click absorbs one Essence item at a time (per the confirmed
 * design -- no /absorb command for now, since there's no class-specific
 * essence to protect against accidental conversion). Blocked if the
 * player is floored, or already at max essence.
 */
public class Essence implements Listener {

    private static final String IDENTIFIER_KEY = "vessel_essence";

    private static final TextColor NAME_START = TextColor.color(0xB266FF); // purple
    private static final TextColor NAME_END = TextColor.color(0x4B0082);   // dark purple

    private static final TextColor LORE_START = TextColor.color(0xE6CCFF); // light purple
    private static final TextColor LORE_END = TextColor.color(0xC299FF);   // less light purple

    private final PlayerDataManager dataManager;
    private final Apply apply;

    public Essence(PlayerDataManager dataManager, Apply apply) {
        this.dataManager = dataManager;
        this.apply = apply;
    }

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(gradient("Essence", NAME_START, NAME_END, true));
        meta.lore(List.of(gradient("Right-click to gain +1 Essence", LORE_START, LORE_END, false)));

        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(
                new NamespacedKey("vessel", IDENTIFIER_KEY),
                PersistentDataType.BOOLEAN,
                true
        );

        item.setItemMeta(meta);
        return item;
    }

    /** Checks whether a given item is a real Essence item, not just a plain Ghast Tear. */
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
            return; // avoid double-firing for main hand + off hand
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
            player.sendMessage("You're disconnected from your Spirit -- recover first.");
            return;
        }

        boolean absorbed = data.absorb();
        if (!absorbed) {
            player.sendMessage("You're already at maximum Essence.");
            return;
        }

        if (held.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            held.setAmount(held.getAmount() - 1);
        }

        dataManager.save(data);
        apply.refresh(player);
    }

    /** Builds a per-character color-interpolated gradient Component. */
    private static Component gradient(String text, TextColor start, TextColor end, boolean bold) {
        Component result = Component.empty();
        int length = text.length();

        for (int i = 0; i < length; i++) {
            float ratio = length == 1 ? 0 : (float) i / (length - 1);
            TextColor stepColor = TextColor.lerp(ratio, start, end);

            Component charComponent = Component.text(String.valueOf(text.charAt(i)))
                    .color(stepColor)
                    .decoration(TextDecoration.BOLD, bold)
                    .decoration(TextDecoration.ITALIC, false);

            result = result.append(charComponent);
        }

        return result;
    }
}
