package com.gmail.zariust.otherdrops.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.Updater;

public class PlayerJoinUpdateChecker implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinUpdateCheck(PlayerJoinEvent evt) throws InterruptedException {
    	Player player = evt.getPlayer(); 
    	if(player.hasPermission("otherdrops.admin.updates") && OtherDropsConfig.globalUpdateChecking) {
    		Updater.runPlayerUpdateCheck(player);
    	}
    }

}
