package me.deejay.wishsmp.commands;

import me.deejay.wishsmp.WishSMP;
import me.deejay.wishsmp.essence.Essence;
import me.deejay.wishsmp.essence.SpeedEssence;
import me.deejay.wishsmp.essence.StrengthEssence;
import me.deejay.wishsmp.essence.HealthEssence;
import me.deejay.wishsmp.essence.HasteEssence;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WishCommand implements CommandExecutor {

    private final WishSMP plugin;

    public WishCommand(WishSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /wish <subcommand>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {

            case "give" -> handleGive(player, args);

            case "menu" -> {
                // TODO: open your GUI here
                player.sendMessage("GUI menu not implemented yet.");
            }

            default -> player.sendMessage("Unknown subcommand.");
        }

        return true;
    }

    private void handleGive(Player player, String[] args) {
        if (!player.hasPermission("wishsmp.use")) {
            player.sendMessage("§cYou do not have permission to use this command!");
            return;
        }

        if (args.length < 3) {
            player.sendMessage("Usage: /wish give <essenceType> <amount>");
            return;
        }

        String type = args[1].toLowerCase();
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage("§cAmount must be a positive number!");
            return;
        }

        ItemStack essenceItem;

        switch (type) {
            case "speed" -> essenceItem = SpeedEssence.create(amount);
            case "strength" -> essenceItem = StrengthEssence.create(amount);
            case "health" -> essenceItem = HealthEssence.create(amount);
            case "haste" -> essenceItem = HasteEssence.create(amount);
            case "essence" -> essenceItem = Essence.create(amount);
            default -> {
                player.sendMessage("§cInvalid essence type! Valid: speed, strength, health, haste, essence");
                return;
            }
        }

        player.getInventory().addItem(essenceItem);
        player.sendMessage("§aGave " + amount + " " + type + " essence!");
    }
}
