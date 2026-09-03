package com.vessel.smp.commands;

import com.vessel.smp.Apply;
import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /essenceset <player> <amount> -- admin-only. Sets essence level
 * directly via PlayerData.setEssenceLevel(), which has NO clamp --
 * this deliberately bypasses both the floor (-2) and the max (5), per
 * the confirmed design: admin commands are true raw overrides.
 *
 * Works for online or offline players.
 */
public class EssenceSetCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;
    private final Apply apply;

    public EssenceSetCommand(PlayerDataManager dataManager, Apply apply) {
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
            sender.sendMessage("Usage: /essenceset <player> <amount>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sender.sendMessage("Amount must be a whole number.");
            return true;
        }

        PlayerData data = dataManager.getOffline(args[0]);
        if (data == null) {
            sender.sendMessage("No data found for that player.");
            return true;
        }

        int clamped = Math.max(PlayerData.FLOOR, Math.min(PlayerData.MAX_ESSENCE, amount));
        data.setEssenceLevel(clamped);
        dataManager.save(data);

        Player online = Bukkit.getPlayer(data.getUuid());
        if (online != null) {
            apply.refresh(online);
        }

        sender.sendMessage(data.getUsername() + "'s essence set to " + clamped + ".");
        return true;
    }
}
