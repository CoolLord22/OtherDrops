package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.parameters.Action;

public class PlayerAction extends ActionMulti {

    public enum StatType {
        HUNGER, XP, SPEED, EXHAUSTION
    }

    protected double       radius = OtherDropsConfig.gActionRadius;
    private final StatType stat;
    private float          statValue;
    private boolean deduct;
    private boolean add;

    public PlayerAction(StatType stat, Object value, ActionType actionType) {
        this.stat = stat;
        this.actionType = actionType;

        if (value instanceof String) {
            String stringVal = (String) value;
            if (stringVal.startsWith("+")) {
                this.add = true;
                stringVal = stringVal.substring(1);
                Log.dMsg("ADD!!!");
                
            } else if (stringVal.startsWith("-")) {
                this.deduct = true;
                stringVal = stringVal.substring(1);
                Log.dMsg("REMOVE!!!");
            }
            statValue = Float.valueOf(stringVal);
        } else if (value instanceof Integer) {
            statValue = Float.valueOf(((Integer) value).toString());
        } else if (value instanceof Float) {
            statValue = (Float) value;
        } else if (value instanceof Double) {
            statValue = ((Double) value).floatValue();
        }
    }

    @Override
    protected void applyEffect(LivingEntity lEnt) {
        if (lEnt == null) {
            return;
        }

        if (lEnt instanceof Player) {
            Player player = (Player) lEnt;

            if (!this.add && !this.deduct) {
                setValue(player, statValue);
            } else {
                float val = getValue(player);
                
                if (this.add) {
                    val = val + statValue;
                } else if (this.deduct){
                    val = val - statValue;
                }
                
                setValue(player, val);
            }            
        }
    }

    private float getValue(Player player) {
        switch (stat) {
        case EXHAUSTION:
            return player.getExhaustion();
        case HUNGER:
            return player.getFoodLevel();
        case SPEED:
            return player.getWalkSpeed();
        case XP:
            return player.getExp();
        default:
            return 0;
        }
    }

    /**
     * @param player
     */
    private void setValue(Player player, float statVal) {
        switch (stat) {
        case EXHAUSTION:
            Log.dMsg("Setting exhaustion to: "+ statVal);
            player.setExhaustion(statVal);
            break;
        case HUNGER:
            player.setFoodLevel(Math.round(statVal));
            break;
        case SPEED:
            Log.dMsg("Setting walk speed to: " + statVal);
            player.setWalkSpeed(statVal);
            break;
        case XP:
            player.giveExp(Math.round(statVal));
            break;
        default:
            break;

        }
    }

    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        List<Action> actions = new ArrayList<Action>();

        // foodlevel, flyspeed, flight, level, saturation, walkspeed,
        Map<String, ActionType> matches = getMatches("pset.hunger");
        actions.addAll(parse(parseMe, matches, StatType.HUNGER));

        matches = getMatches("pset.exhaustion");
        actions.addAll(parse(parseMe, matches, StatType.EXHAUSTION));

        matches = getMatches("pset.xp");
        actions.addAll(parse(parseMe, matches, StatType.XP));

        matches = getMatches("pset.speed");
        actions.addAll(parse(parseMe, matches, StatType.SPEED));

        return actions;
    }

    private Collection<? extends Action> parse(ConfigurationNode parseMe,
            Map<String, ActionType> matches, StatType stat) {
        List<Action> actions = new ArrayList<Action>();
        if (parseMe == null || matches == null || stat == null) {
            return actions;
        }

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null) {
                actions.add(new PlayerAction(stat, parseMe.get(key), matches
                        .get(key)));
            }
        }

        return actions;

    }
}
