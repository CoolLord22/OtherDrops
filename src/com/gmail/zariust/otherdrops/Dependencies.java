// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Zarius Tularial
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops;

import static com.gmail.zariust.common.Verbosity.EXTREME;
import static com.gmail.zariust.common.Verbosity.HIGH;
import static com.gmail.zariust.common.Verbosity.HIGHEST;

import java.io.IOException;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionFactory;
import me.drakespirit.plugins.moneydrop.MoneyDrop;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bgsoftware.wildstacker.api.WildStacker;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.listeners.JobsPaymentListener;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.MobArenaHandler;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.listeners.BlockListener;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.metrics.Metrics;
import com.herocraftonline.heroes.Heroes;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.palmergames.bukkit.towny.Towny;

import fr.neatmonster.nocheatplus.NoCheatPlus;

@SuppressWarnings("unused")
public class Dependencies {
    // Plugin Dependencies
    private static CoreProtectAPI   coreProtect     = null; // for CoreProtect
                                                             // support
    private static WorldGuardPlugin worldGuard      = null; // for WorldGuard
                                                             // support
    private static Towny	 		towny 			= null;
    private static WildStacker	    wildStacker 	= null;
    private static Jobs	 			jobs 			= null;
    private static NoCheatPlus		ncp 			= null;
    private static GriefPrevention  gp 				= null;
    
    boolean                         enabled;
    private static MobArena         mobArena        = null;
    private static MobArenaHandler  mobArenaHandler = null; // for MobArena
    private static MoneyDrop        moneyDrop       = null; // for MoneyDrop

    
    private static Economy          vaultEcon       = null;
	private static Permission       vaultPerms      = null;

    static String                   foundPlugins;
    static String                   notFoundPlugins;
    private static Heroes           heroes;
    private static Prism            prism           = null;

    private static think.rpgitems.Plugin          rpgItems        = null;
    private static mcMMO            mcmmo           = null;

    public static void init() {
        try {
            foundPlugins = "";
            notFoundPlugins = ""; // need to reset variables to allow for
                                  // reloads
            worldGuard = (WorldGuardPlugin) getPlugin("WorldGuard");
        } catch (Exception e) {
            Log.logInfo("Failed to load one or more optional dependencies - continuing OtherDrops startup.");
            e.printStackTrace();
        }
        try {
            coreProtect = loadCoreProtect();
        } catch (Exception e) {
            Log.logInfo("Failed to load one or more optional dependencies - continuing OtherDrops startup.");
            e.printStackTrace();
        }
        try {
        	towny = (Towny) getPlugin("Towny");
        	wildStacker = (WildStacker) getPlugin("WildStacker");
        	gp = (GriefPrevention) getPlugin("GriefPrevention");
        	jobs = (Jobs) getPlugin("Jobs");
        	ncp = (NoCheatPlus) getPlugin("NoCheatPlus");
            mobArena = (MobArena) getPlugin("MobArena");
            moneyDrop = (MoneyDrop) getPlugin("MoneyDrop");
            heroes = (Heroes) getPlugin("Heroes");
            prism = (Prism) getPlugin("Prism");
            rpgItems = (think.rpgitems.Plugin) getPlugin("RPG Items");
            mcmmo = (mcMMO) getPlugin("mcMMO");
        } catch (Exception e) {
            Log.logInfo("Failed to load one or more optional dependencies - continuing OtherDrops startup.");
            e.printStackTrace();
        }

        try {
            setupVault();
        } catch (Exception e) {
            Log.logInfo("Failed to load one or more optional dependencies - continuing OtherDrops startup.");
            e.printStackTrace();
        }

        try {
            if (coreProtect != null) { // Ensure we have access to the API
                foundPlugins += ", CoreProtect";
                // coreProtect.testAPI(); //Will print out
                // "[CoreProtect] API Test Successful." in the console.
            }

            if (mobArena != null) {
                mobArenaHandler = new MobArenaHandler();
            }

        } catch (Exception e) {
            Log.logInfo("Failed to load one or more optional dependencies - continuing OtherDrops startup.");
            e.printStackTrace();
        }
            if (!foundPlugins.isEmpty())
                Log.logInfo("Found supported plugin(s): '" + foundPlugins + "'",
                        Verbosity.NORMAL);
            if (!notFoundPlugins.isEmpty())
                Log.logInfo("(Optional) plugin(s) not found: '" + notFoundPlugins
                        + "' (OtherDrops will continue to load)",
                        Verbosity.HIGHEST);
    }

    public static Plugin getPlugin(String name) {
        Plugin plugin = OtherDrops.plugin.getServer().getPluginManager()
                .getPlugin(name);

        if (plugin == null) {
            if (notFoundPlugins.isEmpty())
                notFoundPlugins += name;
            else
                notFoundPlugins += ", " + name;
        } else {
            if (foundPlugins.isEmpty())
                foundPlugins += name;
            else
                foundPlugins += ", " + name;
        }

        return plugin;
    }

    private static CoreProtectAPI loadCoreProtect() {
        Plugin plugin = OtherDrops.plugin.getServer().getPluginManager()
                .getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            return null;
        }

        // CoreProtect has changed the way API versions need to be checked for version 2.0+
        // As OtherDrops should work with any CoreProtect from 1.6+ we first check the old way
        // and if that fails we check the new way for 2.0+
        boolean checkVersionTwo = false;

        try {
            // Check that a compatible version of CoreProtect is loaded
            if (Double.parseDouble(plugin.getDescription().getVersion()) < 1.6) {
                return null;
            }
        } catch (Exception ex) {
            checkVersionTwo = true;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            return null;
        }

        if (checkVersionTwo) {
            try {
                // Check that a compatible version of the API is loaded
                if (CoreProtect.APIVersion() < 2) {
                    return null;
                }
            } catch (Exception ex) {
                return null;
            }
        }


        return CoreProtect;
    }

    public static boolean hasPermission(Permissible who, String permission) {
        if (who instanceof ConsoleCommandSender)
            return true;
        boolean perm = who.hasPermission(permission);
        if (!perm) {
            Log.logInfo("SuperPerms - permission (" + permission
                    + ") denied for " + who.toString(), HIGHEST);
        } else {
            Log.logInfo("SuperPerms - permission (" + permission
                    + ") allowed for " + who.toString(), HIGHEST);
        }
        return perm;
    }

    private static void setupVault() {
        if (OtherDrops.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            vaultEcon = null;
            Log.logInfo("Couldn't load Vault.", EXTREME); // Vault's not
                                                          // essential so no
                                                          // need to worry.
            return;
        }
        Log.logInfo("Hooked into Vault.", HIGH);
        RegisteredServiceProvider<Economy> rsp = OtherDrops.plugin.getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            vaultEcon = null;
            Log.logWarning("Found Vault but couldn't hook into Vault economy module (note: you need a separate economy plugin, eg. Essentials, iConomy, BosEconomy, etc.)",
                    Verbosity.NORMAL);
            return;
        }
        vaultEcon = rsp.getProvider();

        // RegistereredServiceProvider<Chat> rsp =
        // getServer().getServicesManager().getRegistration(Chat.class);
        // chat = rsp.getProvider();
        // return chat != null;

        RegisteredServiceProvider<Permission> rsp_perms = OtherDrops.plugin
                .getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (rsp_perms == null) {
            vaultPerms = null;
            Log.logWarning("...couldn't hook into Vault permissions module.",
                    Verbosity.NORMAL);
            return;
        }
        vaultPerms = rsp_perms.getProvider();
    }

    // If logblock plugin is available, inform it of the block destruction
    // before we change it
    @SuppressWarnings("deprecation")
	public static boolean queueBlockBreak(String playerName, Block block, BlockBreakEvent event) {
        if (block == null) {
            Log.logWarning(
                    "Queueblockbreak: block is null - this shouldn't happen (please advise developer).  Player = "
                            + playerName, HIGH);
            return false;
        }

        String message = playerName + "-broke-" + block.getType().toString();

        if (OtherDropsConfig.gcustomBlockBreakToMcmmo && Dependencies.hasMcmmo()) {
            Log.logInfo("Attempting to send BlockBreakEvent to mcMMO: " + message, HIGHEST);
            BlockListener bl = new BlockListener(Dependencies.getMcmmo());
            bl.onBlockBreak(event);
        }
        
        if (Dependencies.hasJobs()) {
        	Log.logInfo("Attempting to send BlockBreakEvent to Jobs: " + message, HIGHEST);
        	JobsPaymentListener jpl = new JobsPaymentListener(Dependencies.getJobs()); 
        	jpl.onBlockBreak(event);
        }

        if (Dependencies.hasCoreProtect()) {
            Log.logInfo("Attempting to log to CoreProtect: " + message, HIGHEST);
            Dependencies.getCoreProtect().logRemoval(playerName, block.getLocation(), block.getType(), block.getData());
        }

        if (hasPrism()) {
            Log.logInfo("Attempting to log to Prism (" + message + ")", HIGHEST);
            Prism.actionsRecorder.addToQueue(ActionFactory.create("block-break", block, playerName));
        }
        return true;
    }

    public static boolean hasTowny() {
        return Dependencies.towny != null;
    }
    
    public static boolean hasWildStacker() {
        return Dependencies.wildStacker != null;
    }
    
    public static Towny getTowny() {
        return Dependencies.towny;
    }

    public static boolean hasJobs() {
        return Dependencies.jobs != null;
    }
    
    public static Jobs getJobs() {
        return Dependencies.jobs;
    }

    public static boolean hasGriefPrevention() {
        return Dependencies.gp != null;
    }
    
    public static GriefPrevention getGriefPrevention() {
        return Dependencies.gp;
    }

    public static boolean hasNCP() {
        return Dependencies.ncp != null;
    }
    
    public static NoCheatPlus getNCP() {
        return Dependencies.ncp;
    }

    public static boolean hasMobArena() {
        return Dependencies.mobArena != null;
    }

    public static MobArenaHandler getMobArenaHandler() {
        return Dependencies.mobArenaHandler;
    }

    public static boolean hasWorldGuard() {
        return Dependencies.worldGuard != null;
    }

    public static WorldGuardPlugin getWorldGuard() {
        return Dependencies.worldGuard;
    }

    public static boolean hasVaultEcon() {
        return Dependencies.vaultEcon != null;
    }

    public static Economy getVaultEcon() {
        return Dependencies.vaultEcon;
    }

    public static boolean hasMoneyDrop() {
        return Dependencies.moneyDrop != null;
    }

    public static MoneyDrop getMoneyDrop() {
        return Dependencies.moneyDrop;
    }

    public static boolean hasCoreProtect() {
        return Dependencies.coreProtect != null;
    }

    public static CoreProtectAPI getCoreProtect() {
        return Dependencies.coreProtect;
    }

    public static boolean hasHeroes() {
        return Dependencies.heroes != null;
    }

    public static Heroes getHeroes() {
        return Dependencies.heroes;
    }

    public static Prism getPrism() {
        return prism;
    }

    public static boolean hasPrism() {
        return prism != null;
    }

    public static think.rpgitems.Plugin getRpgItems() {
        return rpgItems;
    }

    public static boolean hasRpgItems() {
        return rpgItems != null;
    }

    public static mcMMO getMcmmo() {
        return mcmmo;
    }

    public static boolean hasMcmmo() {
        return mcmmo != null;
    }
}
