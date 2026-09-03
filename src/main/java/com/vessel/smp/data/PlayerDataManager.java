package com.vessel.smp.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Single source of truth for player Spirit data.
 *
 * Design: all live ability/attribute checks read from the in-memory cache
 * (never hit disk mid-combat). Disk is only touched on join (load), on
 * quit (save), and immediately after any state change (absorb, death,
 * withdraw, recover, reroll, admin commands) so nothing is lost on a
 * crash.
 *
 * File format, per player, at playerdata/<uuid>.yml:
 *   Username: Steve
 *   Mob: Villager
 *   EssenceLevel: 4
 */
public class PlayerDataManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();
    private final File dataFolder;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    private File fileFor(UUID uuid) {
        return new File(dataFolder, uuid.toString() + ".yml");
    }

    /**
     * Call on PlayerJoinEvent. Loads existing data from disk into the
     * cache, or -- if this is the player's first join -- rolls a random
     * Spirit, sets EssenceLevel to 1 (the free starting attribute, per
     * the cold-start fix), and writes the new file.
     */
    public PlayerData loadOrCreate(Player player) {
        UUID uuid = player.getUniqueId();
        File file = fileFor(uuid);

        PlayerData data;
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String username = config.getString("Username", player.getName());
            String spiritName = config.getString("Mob", SpiritType.WARDEN.name());
            int essenceLevel = config.getInt("EssenceLevel", 1);

            SpiritType spirit;
            try {
                spirit = SpiritType.valueOf(spiritName.toUpperCase());
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().log(Level.WARNING, "Unknown Spirit '" + spiritName
                        + "' in file for " + uuid + ", defaulting to WARDEN");
                spirit = SpiritType.WARDEN;
            }

            data = new PlayerData(uuid, username, spirit, essenceLevel);
        } else {
            // First join: roll a Spirit, start at essence level 1.
            SpiritType spirit = SpiritType.random();
            data = new PlayerData(uuid, player.getName(), spirit, 1);
            save(data);
        }

        cache.put(uuid, data);
        return data;
    }

    /** Live lookup -- reads from memory only, never touches disk. */
    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    public PlayerData get(Player player) {
        return get(player.getUniqueId());
    }

    /**
     * Looks up a player's data by username, whether they're online or not.
     * Checks the in-memory cache first (covers online players); if not
     * found there, resolves the username to a UUID via Bukkit's offline
     * player lookup (uses the server's local usercache, no network call
     * needed for anyone who's joined before) and reads their file
     * straight from disk.
     *
     * Read-only use (e.g. /checktier) -- the result is NOT added to the
     * online cache, since caching offline players indefinitely would be
     * a memory leak. Returns null if no file exists for that username.
     */
    public PlayerData getOffline(String username) {
        for (PlayerData cached : cache.values()) {
            if (cached.getUsername().equalsIgnoreCase(username)) {
                return cached;
            }
        }

        UUID uuid = org.bukkit.Bukkit.getOfflinePlayer(username).getUniqueId();
        File file = fileFor(uuid);
        if (!file.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String storedUsername = config.getString("Username", username);
        String spiritName = config.getString("Mob", SpiritType.WARDEN.name());
        int essenceLevel = config.getInt("EssenceLevel", 1);

        SpiritType spirit;
        try {
            spirit = SpiritType.valueOf(spiritName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            spirit = SpiritType.WARDEN;
        }

        return new PlayerData(uuid, storedUsername, spirit, essenceLevel);
    }

    /** Call on PlayerQuitEvent to persist final state and free memory. */
    public void unload(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = cache.get(uuid);
        if (data != null) {
            save(data);
        }
        cache.remove(uuid);
    }

    /** Persists one player's current in-memory state to their YAML file. */
    public void save(PlayerData data) {
        File file = fileFor(data.getUuid());
        YamlConfiguration config = new YamlConfiguration();
        config.set("Username", data.getUsername());
        config.set("Mob", data.getSpirit().name());
        config.set("EssenceLevel", data.getEssenceLevel());
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + data.getUuid(), ex);
        }
    }
}
