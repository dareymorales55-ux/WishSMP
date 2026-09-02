package com.vessel.smp.commands;

import com.vessel.smp.data.PlayerData;
import com.vessel.smp.data.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /checktier <player> -- admin-only. Returns just the target's raw
 * Essence Level, no tier descriptions, no extra text.
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

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found or not online.");
            return true;
        }

        PlayerData data = dataManager.get(target);
        if (data == null) {
            sender.sendMessage("That player's data hasn't loaded yet.");
            return true;
        }

        sender.sendMessage(target.getName() + ": " + data.getEssenceLevel());
        return true;
    }
}
