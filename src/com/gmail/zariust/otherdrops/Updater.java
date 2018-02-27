package com.gmail.zariust.otherdrops;

import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class Updater {
    public static Updater uc;

    public static Updater getInstance() {
        return uc;
    }

    final static String VERSION_URL = "https://api.spiget.org/v2/resources/51793/versions?size=" + Integer.MAX_VALUE
            + "&spiget__ua=SpigetDocs";
    final static String DESCRIPTION_URL = "https://api.spiget.org/v2/resources/51793/updates?size=" + Integer.MAX_VALUE
            + "&spiget__ua=SpigetDocs";

    public static Object[] getLastUpdate() {
        try {
            JSONArray versionsArray = (JSONArray) JSONValue
                    .parseWithException(IOUtils.toString(new URL(String.valueOf(VERSION_URL)), "UTF-8")); 
            String lastVersion = ((JSONObject) versionsArray.get(versionsArray.size() - 1)).get("name").toString().replace("-", "").replaceAll("[a-zA-Z]+", "");
            
            if(Integer.parseInt(lastVersion.replaceAll("\\.","")) > Integer.parseInt(OtherDrops.plugin.getDescription().getVersion().replaceAll("\\.",""))) {
                JSONArray updatesArray = (JSONArray) JSONValue
                        .parseWithException(IOUtils.toString(new URL(String.valueOf(DESCRIPTION_URL)), "UTF-8"));
                String updateName = ((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("title").toString();
                Object[] update = { lastVersion, updateName };
                return update;
            }
            
            if(Integer.parseInt(lastVersion.replaceAll("\\.","")) < Integer.parseInt(OtherDrops.plugin.getDescription().getVersion().replaceAll("\\.",""))) {
                JSONArray updatesArray = (JSONArray) JSONValue.parseWithException(IOUtils.toString(new URL(String.valueOf(DESCRIPTION_URL)), "UTF-8"));
                String updateName = ((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("title").toString();
                String wowzers = "Woah a beta version?";
                Object[] update = { lastVersion, updateName, wowzers };
                return update;
            }
            
        } catch (Exception exc) {
        	exc.printStackTrace();
            return new String[0];
        }
        return new String[0];
    }
    
    public static void runUpdateCheck() {
        Log.logInfoNoVerbosity(ChatColor.GREEN + "Checking for updates...");
    	Object[] updates = getLastUpdate();
    	
    	if(updates.length == 2) {
    		Log.logInfoNoVerbosity(ChatColor.RED + "Uh oh... Do you not like us D: You're running an older version!");
    		Log.logInfoNoVerbosity(ChatColor.RED + "Latest Version: " + updates[0]);
    		Log.logInfoNoVerbosity(ChatColor.RED + "Your Version: " + OtherDrops.plugin.getDescription().getVersion());
    		Log.logInfoNoVerbosity(ChatColor.RED + "What's changed: " + updates[1]);
    		Log.logInfoNoVerbosity(ChatColor.YELLOW + "Please download latest version from: " + ChatColor.GREEN + "https://www.spigotmc.org/resources/otherdrops-updated.51793/updates");
    	}
    	
    	else if(updates.length == 3) {
    		Log.logInfoNoVerbosity(ChatColor.GREEN + "Holy guacamole! Are you in kahoots with the Dev?");
    		Log.logInfoNoVerbosity(ChatColor.GREEN + "Latest Version: " + updates[0]);
    		Log.logInfoNoVerbosity(ChatColor.GREEN + "Your Version: " + OtherDrops.plugin.getDescription().getVersion());
    		Log.logInfoNoVerbosity(ChatColor.GREEN + "You are running a BETA version! Please be careful.");
    	}
    	
    	else {
    		Log.logInfoNoVerbosity(ChatColor.GREEN + "Hooray! You are running the latest version!");
    	}
    }
    
    public static void runPlayerUpdateCheck(Player player) throws InterruptedException {
    	Object[] updates = getLastUpdate();
    	
    	IChatBaseComponent updateLink = ChatSerializer.a("{\"text\":\" Download latest version here!\",\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/otherdrops-updated.51793/updates\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click to open Spigot page!\"}]}}}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(updateLink);

    	if(updates.length == 2) {
    		player.sendMessage(ChatColor.GREEN + "[OtherDrops] " + ChatColor.RED + "Your current version of OtherDrops is outdated. Available version: " + ChatColor.GREEN + updates[0] + ChatColor.RED + " Current version: " + ChatColor.GREEN +  OtherDrops.plugin.getDescription().getVersion());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
    	}
    	
    	if(updates.length == 3) {
    		player.sendMessage(ChatColor.GREEN + "[OtherDrops] " + ChatColor.RED + "Latest version: " + ChatColor.GREEN + updates[0] + ChatColor.RED + " Current version: " + ChatColor.GREEN +  OtherDrops.plugin.getDescription().getVersion() + ChatColor.RED + " BETA builds aren't always stable... Use with precaution!");
    	}
    }
}
