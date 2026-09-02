package com.vessel.smp.commands;

import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /tier -- self-facing only, no target argument. Shows the player's full
 * essence ladder (Essence 1-5 descriptions for their current Spirit) plus
 * their current Essence Level.
 *
 * All display text lives directly in this file for now, rather than
 * being sourced from each Spirit's own Passives/Events files.
 */
public class TierCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;

    public TierCommand(PlayerDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        PlayerData data = dataManager.get(player);
        if (data == null) {
            player.sendMessage("Your data hasn't loaded yet -- try again in a moment.");
            return true;
        }

        switch (data.getSpirit()) {
            case WARDEN -> sendWardenTiers(player);
            case DOLPHIN -> sendDolphinTiers(player);
            case VILLAGER -> sendVillagerTiers(player);
            case TURTLE -> sendTurtleTiers(player);
        }

        player.sendMessage("Essence Level: " + data.getEssenceLevel());
        return true;
    }

    private void sendWardenTiers(Player player) {
        player.sendMessage("Essence 1: +1.5 Attack Damage");
        player.sendMessage("Essence 2: +2s Shield Disable Duration (on hit)");
        player.sendMessage("Essence 3: +1 durability damage to 2 random armor pieces on hit");
        player.sendMessage("Essence 4: Piercing Shriek - instant, first hit: 4.5 hearts, "
                + "Blindness 10s, Shield Disable 10s. Cooldown: 3 minutes");
        player.sendMessage("Essence 5: Soul's Hand - melee-only, 10s duration; every melee hit "
                + "deals normal damage + Darkness 5s + 20% delayed follow-up 1s later. Cooldown: 2 minutes");
    }

    private void sendDolphinTiers(Player player) {
        player.sendMessage("Essence 1: +15% Movement Speed");
        player.sendMessage("Essence 2: 50% Fall Damage Reduction");
        player.sendMessage("Essence 3: +10% Attack Speed");
        player.sendMessage("Essence 4: Slippery - immune to cobweb/soul sand slow, +3% speed per hit "
                + "(max +18%). Duration 10s. Cooldown: 3 minutes");
        player.sendMessage("Essence 5: Torpedo - 6-block lunge, 2s invincibility, nearby enemies "
                + "drowned for 5s on landing. Cooldown: 1 minute 45 seconds");
    }

    private void sendVillagerTiers(Player player) {
        player.sendMessage("Essence 1: +50% cheaper villager trades");
        player.sendMessage("Essence 2: 50% chance on hit taken to refund 2 armor durability");
        player.sendMessage("Essence 3: +25% more XP from bottles");
        player.sendMessage("Essence 4: Preservation - 2x XP from all sources, 50% chance food/wind "
                + "charges not consumed, 35% faster food/wind charge use. Duration 15s. Cooldown: 4 minutes");
        player.sendMessage("Essence 5: Greed - every hit landed causes 50% amplified armor wear on "
                + "target; bonus portion heals user's armor. Duration 15s. Cooldown: 3 minutes 15 seconds");
    }

    private void sendTurtleTiers(Player player) {
        player.sendMessage("Essence 1: +4 hearts");
        player.sendMessage("Essence 2: +2 saturation per food item eaten");
        player.sendMessage("Essence 3: +50% knockback reduction");
        player.sendMessage("Essence 4: Exchange - steals 0.5 hearts (max health) per hit, target floor "
                + "5 hearts, user cap 20 hearts. Duration 20s. Cooldown: 2 minutes");
        player.sendMessage("Essence 5: Defense Mirror - 5s window, Resistance II; 100% of damage dealt "
                + "reflected per-attacker, capped at 10 hearts, in 3 staggered bursts over 8s after. "
                + "Cooldown: 2 minutes 30 seconds");
    }
}
