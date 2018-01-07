// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
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

package com.gmail.zariust.otherdrops.parameters;

import static com.gmail.zariust.common.Verbosity.NORMAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

/**
 * Represents an action that can be taken to lead to a drop.
 */
public final class Trigger implements Comparable<Trigger> {
    /**
     * The basic action; breaking a block, or killing a creature.
     */
    public final static Trigger         BREAK          = new Trigger("BREAK");
    /**
     * Left clicking on the target (hitting)
     */
    public final static Trigger         HIT     = new Trigger(
                                                               "HIT");
    /**
     * Right clicking on the target.
     */
    public final static Trigger         RIGHT_CLICK    = new Trigger(
                                                               "RIGHT_CLICK");
    /**
     * The action of natural leaf decay.
     */
    public final static Trigger         LEAF_DECAY     = new Trigger(
                                                               "LEAF_DECAY");
    /**
     * Action of catching a fish.
     */
    public final static Trigger         FISH_CAUGHT    = new Trigger(
                                                               "FISH_CAUGHT");
    /**
     * Action of fishing: failure.
     */
    public final static Trigger         FISH_FAILED    = new Trigger(
                                                               "FISH_FAILED");
    /**
     * Triggered when a mob is spawned
     */
    public final static Trigger         MOB_SPAWN      = new Trigger(
                                                               "MOB_SPAWN");
    /**
     * Triggered when an entity hits another (EntityDamageEvent)
     */
    // No longer used - now using LEFTCLICK with alias (hit)
    //public final static Trigger         HIT            = new Trigger("HIT");
    /**
     * Triggered when redstone powers up on a block (including levels & wires)
     */
    public final static Trigger         POWER_UP       = new Trigger("POWER_UP");
    /**
     * Triggered when redstone powers down on a block (including levels & wires)
     */
    public final static Trigger         POWER_DOWN     = new Trigger(
                                                               "POWER_DOWN");
    /**
     * Triggered when player joins the server
     */
    public final static Trigger         PLAYER_JOIN    = new Trigger(
                                                               "PLAYER_JOIN");
    /**
     * Triggered when player respawns
     */
    public final static Trigger         PLAYER_RESPAWN = new Trigger(
                                                               "PLAYER_RESPAWN");
    /**
     * Triggered when player consumes an item (food/potion/milk-bucket)
     */
    public final static Trigger         CONSUME_ITEM   = new Trigger(
                                                               "CONSUME_ITEM");
    public final static Trigger         PLAYER_MOVE   = new Trigger(
            "PLAYER_MOVE");
    public final static Trigger         BLOCK_GROW   = new Trigger("BLOCK_GROW");
    
    public final static Trigger         PROJECTILE_HIT_BLOCK     = new Trigger(
            "PROJECTILE_HIT_BLOCK");

    public final static Trigger BLOCK_PLACE = new Trigger(
            "BLOCK_PLACE");
    
    // LinkedHashMap because I want to preserve order
    private static Map<String, Trigger> actions        = new LinkedHashMap<String, Trigger>();
    private static Map<String, Plugin>  owners         = new HashMap<String, Plugin>();
    private static int                  nextOrdinal    = 0;
    private final int                   ordinal;
    private final String                name;

    static {
        actions.put("BREAK", BREAK);
        //actions.put("LEFTCLICK", HIT);
        actions.put("RIGHTCLICK", RIGHT_CLICK);
        actions.put("LEAFDECAY", LEAF_DECAY);
        actions.put("FISHCAUGHT", FISH_CAUGHT);
        actions.put("FISHFAILED", FISH_FAILED);
        actions.put("MOBSPAWN", MOB_SPAWN);
        actions.put("HIT", HIT);
        actions.put("POWERUP", POWER_UP);
        actions.put("POWERDOWN", POWER_DOWN);
        actions.put("PLAYERJOIN", PLAYER_JOIN);
        actions.put("PLAYERRESPAWN", PLAYER_RESPAWN);
        actions.put("CONSUMEITEM", CONSUME_ITEM);
        actions.put("PLAYERMOVE", PLAYER_MOVE);
        actions.put("BLOCKGROW", BLOCK_GROW);
        actions.put("PROJECTILEHITBLOCK", PROJECTILE_HIT_BLOCK);
        actions.put("BLOCKPLACE", BLOCK_PLACE);
        owners.put("BREAK", OtherDrops.plugin);
        //owners.put("LEFTCLICK", OtherDrops.plugin);
        owners.put("RIGHTCLICK", OtherDrops.plugin);
        owners.put("LEAFDECAY", OtherDrops.plugin);
        owners.put("FISHCAUGHT", OtherDrops.plugin);
        owners.put("FISHFAILED", OtherDrops.plugin);
        owners.put("MOBSPAWN", OtherDrops.plugin);
        owners.put("HIT", OtherDrops.plugin);
        owners.put("POWERUP", OtherDrops.plugin);
        owners.put("POWERDOWN", OtherDrops.plugin);
        owners.put("PLAYERJOIN", OtherDrops.plugin);
        owners.put("PLAYERRESPAWN", OtherDrops.plugin);
        owners.put("CONSUMEITEM", OtherDrops.plugin);
        owners.put("PLAYERMOVE", OtherDrops.plugin);
        owners.put("BLOCKGROW", OtherDrops.plugin);
        owners.put("PROJECTILEHITBLOCK", OtherDrops.plugin);
        owners.put("BLOCKPLACE", OtherDrops.plugin);
    }

    private Trigger(String tag) {
        name = tag;
        ordinal = nextOrdinal;
        nextOrdinal++;
    }

    /**
     * Convert an interact action into a drop action.
     * 
     * @param action
     *            The interact action.
     * @return The drop action, or null if none applies.
     */
    public static Trigger fromInteract(org.bukkit.event.block.Action action) {
        switch (action) {
        case LEFT_CLICK_AIR:
        case LEFT_CLICK_BLOCK:
            return HIT;
        case RIGHT_CLICK_AIR:
        case RIGHT_CLICK_BLOCK:
            return RIGHT_CLICK;
        default:
            return null;
        }
    }

    /**
     * Register a new action to your plugin.
     * 
     * @param plugin
     *            Your plugin.
     * @param tag
     *            The action tag name. This can be used in the config file or to
     *            fetch it again later.
     */
    public static void register(Plugin plugin, String tag) {
        if (plugin == null || plugin instanceof OtherDrops)
            throw new IllegalArgumentException(
                    "Use your own plugin for registering an action!");
        actions.put(tag, new Trigger(tag));
        owners.put(tag, plugin);
    }

    /**
     * Unregister a previously registered action.
     * 
     * @param plugin
     *            The plugin that registered the action (preferably your
     *            plugin).
     * @param tag
     *            The action tag name.
     */
    public static void unregister(Plugin plugin, String tag) {
        Plugin check = owners.get(tag);
        if (!check.getClass().equals(plugin.getClass()))
            throw new IllegalArgumentException(
                    "You didn't register that action!");
        owners.remove(tag);
        actions.remove(tag);
    }

    public static List<Trigger> parseFrom(ConfigurationNode dropNode,
            List<Trigger> def) {
        List<String> chosenActions = OtherDropsConfig.getMaybeList(dropNode,
                "action", "actions");
        if (chosenActions == null || chosenActions.isEmpty()) {
            chosenActions = OtherDropsConfig.getMaybeList(dropNode, "trigger",
                    "triggers", "trig");
        } else {
            OtherDropsConfig.actionParameterFound = true;
        }

        List<Trigger> result = new ArrayList<Trigger>();
        for (String action : chosenActions) {
            action = action.replaceAll("[ _-]", "").toUpperCase();

            // Set up trigger aliases
            if (action.equalsIgnoreCase("BLOCKBREAK"))
                action = "BREAK";
            if (action.equalsIgnoreCase("BLOCKDAMAGED")
                    || action.equalsIgnoreCase("LEFTCLICK")
                    || action.equalsIgnoreCase("HITBLOCK")
                    || action.equalsIgnoreCase("HITMOB"))
                action = "HIT";
            if (action.equalsIgnoreCase("SPAWNMOB"))
                action = "MOBSPAWN";
            if (action.equalsIgnoreCase("PLACE"))
                action = "BLOCKPLACE";
            if (action.equalsIgnoreCase("FISHSUCCESS"))
                action = "FISHCAUGHT";
            if (action.equals("GROW") || action.equals("GROWTH")) {
                action = "BLOCKGROW";
            }
            if (action.equalsIgnoreCase("EAT")
                    || action.equalsIgnoreCase("DRINK")
                    || action.equalsIgnoreCase("PLAYERCONSUME")
                || action.equalsIgnoreCase("ITEMCONSUME"))
                action = "CONSUMEITEM";

            Trigger act = actions.get(action.toUpperCase());
            if (act != null)
                result.add(act);
            else
                Log.logWarning("Invalid action " + action + " (known actions: "
                        + getValidActions().toString() + ")", NORMAL);
        }
        if (result.isEmpty()) {
            if (def == null) {
                def = new ArrayList<Trigger>();
                def.add(BREAK);
            }
            return def;
        }
        return result;
    }

    @Override
    public int compareTo(Trigger other) {
        return Integer.valueOf(ordinal).compareTo(other.ordinal);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Trigger))
            return false;
        return ordinal == ((Trigger) other).ordinal;
    }

    @Override
    public int hashCode() {
        return ordinal;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Return a list of all valid actions.
     * 
     * @return All actions.
     */
    public static Trigger[] values() {
        return actions.values().toArray(new Trigger[0]);
    }

    /**
     * Return a list of all valid action names.
     * 
     * @return All actions.
     */
    public static Set<String> getValidActions() {
        return actions.keySet();
    }

    /**
     * Get an action by name.
     * 
     * @param key
     *            The action tag name.*
     * @return The action, or null if it does not exist.
     */
    public static Trigger valueOf(String key) {
        return actions.get(key);
    }
}
