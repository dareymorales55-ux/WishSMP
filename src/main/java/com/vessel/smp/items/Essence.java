package com.vessel.smp.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Essence {

    private static final String IDENTIFIER_KEY = "vessel_essence";

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.GHAST_TEAR);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Essence")
                .color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.ITALIC, false));

        meta.setEnchantmentGlintOverride(true);

        meta.getPersistentDataContainer().set(
                new NamespacedKey("vessel", IDENTIFIER_KEY),
                org.bukkit.persistence.PersistentDataType.BOOLEAN,
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
                org.bukkit.persistence.PersistentDataType.BOOLEAN
        );
    }
}
