package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Action;

public abstract class ActionMulti extends Action {
    public enum ActionType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER
    }

    protected ActionType actionType;
    protected double     radius = OtherDropsConfig.gActionRadius;

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {
        switch (actionType) {
        case ATTACKER:
            applyEffect(occurence.getPlayerAttacker());
            return false;
        case VICTIM:
            applyEffect(occurence.getVictim());
            return false;
        case RADIUS:
            // occurence.getLocation().getRadiusPlayers()? - how do we get
            // players around radius without an entity?
            Location loc = occurence.getLocation();
            for (Player player : loc.getWorld().getPlayers()) {
                if (player.getLocation().getX() > (loc.getX() - radius)
                        || player.getLocation().getX() < (loc.getX() + radius))
                    if (player.getLocation().getY() > (loc.getY() - radius)
                            || player.getLocation().getY() < (loc.getY() + radius))
                        if (player.getLocation().getZ() > (loc.getZ() - radius)
                                || player.getLocation().getZ() < (loc.getZ() + radius))
                            applyEffect(player);
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                applyEffect(player);
            }
            break;
        case WORLD:
            for (Player player : occurence.getLocation().getWorld()
                    .getPlayers()) {
                applyEffect(player);
            }
            break;
        // case STEAL:
        // stealMoney(occurence.getPlayerAttacker(),
        // occurence.getPlayerVictim());
        // break;
        default:
            break;
        }

        return false;
    }

    abstract protected void applyEffect(LivingEntity lEnt);

    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        Map<String, ActionType> matches = getMatches("potioneffect",
                "potioneffects");
        List<Action> actions = new ArrayList<Action>();
        /*
         * for (String key : matches.keySet()) { boolean onlyRemove; if
         * (parseMe.get(key) != null) { onlyRemove = false; actions.add(new
         * PotionAction(parseMe.get(key), matches.get(key), onlyRemove)); } else
         * if (parseMe.get(key + "." + suffix) != null) { onlyRemove = true;
         * actions.add(new PotionAction(parseMe.get(key + ".remove"),
         * matches.get(key), onlyRemove)); } }
         */
        return actions;
    }

    protected Map<String, Boolean> suffixes(String... strings) {
        Map<String, Boolean> suffixes = new HashMap<String, Boolean>();

        for (String string : strings) {
            suffixes.put(string, false);
        }

        return suffixes;
    }

    /**
     * @param parseMe
     * @return
     */
    protected Map<String, ActionType> getMatches(String... names) {
        Map<String, ActionType> matches = new HashMap<String, ActionType>();
        for (String name : names) {
            matches.put(name, ActionType.ATTACKER);
            matches.put(name + ".attacker", ActionType.ATTACKER);
            matches.put(name + ".victim", ActionType.VICTIM);
            matches.put(name + ".target", ActionType.VICTIM);
            matches.put(name + ".world", ActionType.WORLD);
            matches.put(name + ".server", ActionType.SERVER);
            matches.put(name + ".global", ActionType.SERVER);
            matches.put(name + ".all", ActionType.SERVER);
            matches.put(name + ".radius", ActionType.RADIUS);
        }

        return matches;
    }
}