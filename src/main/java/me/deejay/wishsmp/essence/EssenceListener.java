package me.deejay.wishsmp.essence;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EssenceListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() == null) return;

        ItemStack item = event.getItem();

        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        EssenceType type = getEssenceType(name);

        if (type == null) return;

        event.getPlayer().sendMessage("You clicked: " + type.name());
    }

    private EssenceType getEssenceType(String name) {

        if (name.contains("Speed")) return EssenceType.SPEED;
        if (name.contains("Strength")) return EssenceType.STRENGTH;
        if (name.contains("Health")) return EssenceType.HEALTH;
        if (name.contains("Haste")) return EssenceType.HASTE;
        if (name.equalsIgnoreCase("Essence")) return EssenceType.ESSENCE;

        return null;
    }
}
