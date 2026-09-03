package com.vessel.smp.commands;

import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * /checktier <player> -- admin-only. Returns just the target's raw
 * Essence Level, no tier descriptions, no extra text. Works for both
 * online and offline players by username.
 */
public class CheckTierCommand implements CommandExecutor {

    private final PlayerDataManager dataManager;

    public CheckTierCommand(PlayerDataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("vessel.admin")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /checktier <player>");
            return true;
        }

        PlayerData data = dataManager.getOffline(args[0]);
        if (data == null) {
            sender.sendMessage("No data found for that player.");
            return true;
        }

        sender.sendMessage(data.getUsername() + ": " + data.getEssenceLevel());
        return true;
    }
}
