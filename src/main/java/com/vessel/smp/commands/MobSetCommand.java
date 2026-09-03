package com.vessel.smp.commands;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import com.vessel.smp.data.SpiritType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /mobset <player> <mobname> -- admin-only. Forcibly sets a player's
 * Spirit directly, bypassing the normal Mob Reroller rules (no essence-3
 * requirement, no random-exclusion logic, no cost). Since this changes
 * WHICH Spirit's Passives apply (not just essence level), Apply.refresh()
 * is essential here -- it's what actually swaps the old Spirit's
 * passives out for the new one's.
 *
 * Works for online or offline players.
 */
public class MobSetCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;
    private final Apply apply;

    public MobSetCommand(PlayerDataManager dataManager, Apply apply) {
        this.dataManager = dataManager;
        this.apply = apply;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("vessel.admin")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /mobset <player> <mobname>");
            return true;
        }

        SpiritType newSpirit;
        try {
            newSpirit = SpiritType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException ex) {
            sender.sendMessage("Unknown Spirit. Valid options: Warden, Dolphin, Villager, Turtle.");
            return true;
        }

        PlayerData data = dataManager.getOffline(args[0]);
        if (data == null) {
            sender.sendMessage("No data found for that player.");
            return true;
        }

        data.setSpirit(newSpirit);
        dataManager.save(data);

        Player online = Bukkit.getPlayer(data.getUuid());
        if (online != null) {
            apply.refresh(online);
        }

        sender.sendMessage(data.getUsername() + "'s Spirit set to " + newSpirit.getDisplayName() + ".");
        return true;
    }
}
