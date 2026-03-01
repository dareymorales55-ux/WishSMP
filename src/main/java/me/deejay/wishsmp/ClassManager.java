package me.deejay.wishsmp.classes;

import me.deejay.wishsmp.WishSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClassManager {

    private final WishSMP plugin;

    // Main scoreboard
    private final Scoreboard scoreboard;

    // Yaml config file for storing player classes
    private final File playerClassFile;
    private final FileConfiguration playerClassConfig;

    private final Random random = new Random();

    // -----------------------------
    // Constructor: initializes ClassManager
    // -----------------------------
    public ClassManager(WishSMP plugin) {
        this.plugin = plugin;

        // Initialize scoreboard
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getMainScoreboard();

        // Initialize PlayerClasses.yml
        this.playerClassFile = new File(plugin.getDataFolder(), "PlayerClasses.yml");
        if (!playerClassFile.exists()) {
            playerClassFile.getParentFile().mkdirs();
            try {
                playerClassFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.playerClassConfig = YamlConfiguration.loadConfiguration(playerClassFile);

        // Create class teams
        createClassTeams();
    }

    // -----------------------------
    // Section 1: Team Creation
    // -----------------------------
    private void createClassTeams() {
        createTeam("Strength", ChatColor.GOLD);
        createTeam("Speed", ChatColor.AQUA);
        createTeam("Health", ChatColor.DARK_RED);
        createTeam("Haste", ChatColor.YELLOW);
    }

    private void createTeam(String name, ChatColor color) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }
        team.setColor(color);
        team.setDisplayName(name);
    }

    // -----------------------------
    // Section 2: Assigning Random Class on First Join
    // -----------------------------
    public void handleFirstJoin(Player player) {
        if (hasClass(player)) {
            // Player already has a class, just apply it
            applyClass(player);
            return;
        }

        // Arrays of class names and colors
        String[] classes = {"Speed", "Strength", "Health", "Haste"};
        ChatColor[] colors = {ChatColor.AQUA, ChatColor.GOLD, ChatColor.DARK_RED, ChatColor.YELLOW};

        // Cycle through classes with 0.5 sec interval
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < classes.length) {
                    // Display cycling class
                    player.sendTitle(
                            colors[index] + "" + ChatColor.BOLD + classes[index],
                            "",
                            0, 10, 0
                    );
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    index++;
                } else {
                    // Pick a random class
                    int r = random.nextInt(classes.length);
                    String chosen = classes[r];
                    ChatColor color = colors[r];

                    // Display chosen class
                    player.sendTitle(
                            color + "" + ChatColor.BOLD + chosen,
                            "",
                            0, 40, 0
                    );
                    player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 1f);

                    // Save class and add player to team
                    savePlayerClass(player, chosen);
                    addPlayerToTeam(player, chosen);

                    // Stop task
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // 10 ticks = 0.5 seconds
    }

    // -----------------------------
    // Section 3: Scoreboard Team Management
    // -----------------------------
    public void addPlayerToTeam(Player player, String className) {
        // Remove from other teams first
        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(player.getName())) {
                team.removeEntry(player.getName());
            }
        }
        // Add to selected team
        Team team = scoreboard.getTeam(className);
        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    // -----------------------------
    // Section 4: PlayerClasses.yml Handling
    // -----------------------------
    private void savePlayerClass(Player player, String className) {
        String path = player.getUniqueId().toString() + ".class";
        playerClassConfig.set(path, className);
        try {
            playerClassConfig.save(playerClassFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasClass(Player player) {
        String path = player.getUniqueId().toString() + ".class";
        return playerClassConfig.contains(path);
    }

    public String getPlayerClass(Player player) {
        String path = player.getUniqueId().toString() + ".class";
        return playerClassConfig.getString(path);
    }

    // -----------------------------
    // Section 5: Apply Stored Class on Join
    // -----------------------------
    public void applyClass(Player player) {
        if (!hasClass(player)) return;

        String className = getPlayerClass(player);
        addPlayerToTeam(player, className);

        // Here you can call the corresponding class file
        // Example:
        switch (className) {
            case "Strength" -> StrengthClass.applyEffects(player);
            case "Speed" -> SpeedClass.applyEffects(player);
            case "Health" -> HealthClass.applyEffects(player);
            case "Haste" -> HasteClass.applyEffects(player);
        }
    }
}
