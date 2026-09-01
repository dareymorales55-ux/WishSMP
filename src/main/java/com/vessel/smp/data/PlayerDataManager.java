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
            SpiritType spirit = SpiritType.random();
            data = new PlayerData(uuid, player.getName(), spirit, 1);
            save(data);
        }

        cache.put(uuid, data);
        return data;
    }

    public PlayerData get(UUID uuid) {
        return cache.get(uuid);
    }

    public PlayerData get(Player player) {
        return get(player.getUniqueId());
    }

    public void unload(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = cache.get(uuid);
        if (data != null) {
            save(data);
        }
        cache.remove(uuid);
    }

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
