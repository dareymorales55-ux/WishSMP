package me.deejay.wishsmp.test;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import me.deejay.wishsmp.WishSMP;

public class EssenceTexture implements Listener, CommandExecutor {

    private final NamespacedKey essenceKey = new NamespacedKey(WishSMP.getInstance(), "essence_item");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) return false;

        // Set display name
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Essence");

        // Add persistent tag to identify it
        meta.getPersistentDataContainer().set(essenceKey, PersistentDataType.BYTE, (byte)1);

        // Optionally: set custom model data to link to resource pack
        meta.setCustomModelData(123456); // change to your resource pack number

        head.setItemMeta(meta);

        player.getInventory().addItem(head);
        player.sendMessage(ChatColor.GREEN + "You received an Essence head!");

        return true;
    }

    // Prevent the essence head from being placed
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null || !item.hasItemMeta()) return;
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return;

        if (meta.getPersistentDataContainer().has(essenceKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
            if (event.getPlayer() != null) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot place this item!");
            }
        }
    }
}
