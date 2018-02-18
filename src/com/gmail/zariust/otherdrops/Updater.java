package com.gmail.zariust.otherdrops;

import java.net.URL;

import org.apache.commons.io.IOUtils;

import org.bukkit.ChatColor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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
            Double lastVersion = Double
                    .parseDouble(((JSONObject) versionsArray.get(versionsArray.size() - 1)).get("name").toString().replace("-", "").replaceAll("[a-zA-Z]+", ""));
            
            if (lastVersion > Double.parseDouble(OtherDrops.plugin.getDescription().getVersion())) {
                JSONArray updatesArray = (JSONArray) JSONValue
                        .parseWithException(IOUtils.toString(new URL(String.valueOf(DESCRIPTION_URL)), "UTF-8"));
                String updateName = ((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("title").toString();
                Object[] update = { lastVersion, updateName };
                return update;
            }
        } catch (Exception exc) {
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
    	else {
    		Log.logInfoNoVerbosity(ChatColor.GREEN + "Hooray! You are running the latest version!");
    	}
    }
}
