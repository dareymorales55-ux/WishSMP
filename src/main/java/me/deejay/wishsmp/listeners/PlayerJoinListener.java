package me.deejay.wishsmp.listeners;

import me.deejay.wishsmp.WishSMP;
import me.deejay.wishsmp.classes.ClassManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ClassManager classManager;

    public PlayerJoinListener(WishSMP plugin) {
        this.classManager = plugin.getClassManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        classManager.handleFirstJoin(event.getPlayer());
    }
}
