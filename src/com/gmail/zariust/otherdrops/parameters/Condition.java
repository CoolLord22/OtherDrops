package com.gmail.zariust.otherdrops.parameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.conditions.CooldownCheck;
import com.gmail.zariust.otherdrops.parameters.conditions.DistanceCheck;
import com.gmail.zariust.otherdrops.parameters.conditions.LoreNameCheck;
import com.gmail.zariust.otherdrops.parameters.conditions.MobSpawnerCheck;
import com.gmail.zariust.otherdrops.parameters.conditions.PlayerSneakCheck;
import com.gmail.zariust.otherdrops.parameters.conditions.SpawnedCheck;

public abstract class Condition extends Parameter {
    String conditionName = "undefined";

    public final boolean check(CustomDrop drop, OccurredEvent occurrence) {
        boolean result = checkInstance(drop, occurrence);
        Log.logInfo("Condition '" + this.getClass().getSimpleName() + "' "
                + (result ? "passed" : "failed"), Verbosity.HIGHEST);
        return result;
    }

    protected abstract boolean checkInstance(CustomDrop drop,
            OccurredEvent occurrence);

    // protected abstract static List<Condition> parseInstance(ConfigurationNode
    // node);

    protected static Set<Condition> conditions = new HashSet<Condition>();

    // NOTE: currently this function is called before verbosity is loaded from the config file
    // (so debug messages based on Verbosity.HIGH etc. won't work)
    public static boolean registerCondition(Condition register) {
        if (register == null) {
            Log.logInfo("Condition - registering FAILED: " + register.toString());
        } else {
            conditions.add(register);
        }
        return false;
    }

    public static List<Condition> parseNodes(ConfigurationNode node) {
        List<Condition> conditionsReturn = new ArrayList<Condition>();
        List<Condition> conditionsFromParse = new ArrayList<Condition>();
        for (Condition condition : conditions) {
            conditionsFromParse = condition.parse(node);
            if (conditionsFromParse != null)
                conditionsReturn.addAll(conditionsFromParse);
        }
        return conditionsReturn;
    }

    abstract public List<Condition> parse(ConfigurationNode parseMe);

    public static void registerDefaultConditions() {
        registerCondition(new MobSpawnerCheck(null, null));
        registerCondition(new LoreNameCheck(null));
        registerCondition(new SpawnedCheck(null));
        registerCondition(new CooldownCheck(null, null));
        registerCondition(new PlayerSneakCheck(null));
        registerCondition(new DistanceCheck(null, null));
    }

}
