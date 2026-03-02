package me.deejay.wishsmp.essence;

import me.deejay.wishsmp.WishSMP;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class StrengthEssence implements Listener {

    private final WishSMP plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public StrengthEssence(WishSMP plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // -----------------------------
    // Method to create the item
    // -----------------------------
    public static ItemStack create(int amount, WishSMP plugin) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        // Name
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Strength Essence");

        // Lore
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Right-click for +2 attack damage",
                ChatColor.GRAY + "Lasts for 15 seconds",
                ChatColor.GOLD + "2 minute cooldown"
        ));

        // Glow effect
        meta.addEnchant(Enchantment.LUCK, 1, true);

        // Custom skin
        meta.setOwnerProfile(PluginUtils.createCustomProfile(
                "d1e985bdf21849d0bae3eb0b436fa1b",
                "ewogICJ0aW1lc3RhbXAiIDogMTc3MjQxNTI2NTY2MywKICAicHJvZmlsZUlkIiA6ICJkMWU5ODViZGYyMTg0OWQwYmFlZTNlYjBiNDM2ZmExYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSb3NleUNoYW40MjAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc0ZDQzOGY5NGMxY2U1YTI0MjA3OWUxYmMzOTJhNmNkZTk4ZDI0ZDM0YWU2NjJlMTViMjdiN2M1YjM1NzExYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
        ));

        // Tag item with enum
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "essence_type"),
                PersistentDataType.STRING,
                EssenceType.STRENGTH.name()
        );

        item.setItemMeta(meta);
        return item;
    }

    // -----------------------------
    // Right-click usage
    // -----------------------------
    @EventHandler
    public void onUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;

        if (!item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(plugin, "essence_type"), PersistentDataType.STRING)) return;

        String type = item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(plugin, "essence_type"), PersistentDataType.STRING);

        if (!type.equals(EssenceType.STRENGTH.name())) return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldown = 2 * 60 * 1000; // 2 min

        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < cooldown) {
            long remaining = (cooldown - (now - cooldowns.get(uuid))) / 1000;
            player.sendMessage(ChatColor.RED + "Cooldown Time Remaining: " + ChatColor.WHITE + remaining + "s");
            return;
        }

        // Apply +2 attack damage for 15 seconds
        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() + 2);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue() - 2);
            }
        }.runTaskLater(plugin, 15 * 20L);

        cooldowns.put(uuid, now);
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 1f);
    }

    // -----------------------------
    // Particle trail (DUST_TRANSITION)
    // -----------------------------
    @EventHandler
    public void onHold(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItem(e.getNewSlot());
        if (item == null || !item.hasItemMeta()) return;

        if (!item.getItemMeta().getPersistentDataContainer().has(
                new NamespacedKey(plugin, "essence_type"), PersistentDataType.STRING)) return;

        String type = item.getItemMeta().getPersistentDataContainer().get(
                new NamespacedKey(plugin, "essence_type"), PersistentDataType.STRING);

        if (!type.equals(EssenceType.STRENGTH.name())) return;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ > 20 * 5) { // 5 seconds max
                    cancel();
                    return;
                }

                // DUST_TRANSITION gold → orange
                player.getWorld().spawnParticle(
                        Particle.DUST_TRANSITION,
                        player.getEyeLocation().add(0, -0.5, 0),
                        5,
                        0.2, 0.5, 0.2,
                        new Particle.DustTransition(Color.fromRGB(255, 215, 0), Color.fromRGB(255, 165, 0), 1)
                );
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    // -----------------------------
    // Drop on death if in Strength team
    // -----------------------------
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player dead = e.getEntity();
        if (!plugin.getClassManager().getPlayerClass(dead).equals("Strength")) return;

        dead.getWorld().dropItemNaturally(dead.getLocation(), create(1, plugin));
    }
}
