package com.gmail.zariust.otherdrops.parameters.conditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Dependencies;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Condition;

public class McmmoCondition extends Condition {
    private enum Type {
        POWER, ACROBATICS, EXCAVATION, MINING, UNARMED, HERBALISM, FISHING, REPAIR, AXES, ARCHERY, WOODCUTTING, TAMING, SWORDS
    }

    static private Map<String, Type> matches = new HashMap<String, Type>();
    static {
        matches.put("power", Type.POWER);
        matches.put("acrobatics", Type.ACROBATICS);
        matches.put("excavation", Type.EXCAVATION);
        matches.put("mining", Type.MINING);
        matches.put("unarmed", Type.UNARMED);
        matches.put("herbalism", Type.HERBALISM);
        matches.put("fishing", Type.FISHING);
        matches.put("repair", Type.REPAIR);
        matches.put("axes", Type.AXES);
        matches.put("archery", Type.ARCHERY);
        matches.put("woodcutting", Type.WOODCUTTING);
        matches.put("taming", Type.TAMING);
        matches.put("swords", Type.SWORDS);
    }

    @SuppressWarnings("unused")
	private final Type               type;

    @SuppressWarnings("unused")
    private final String             parameterValue;

    public McmmoCondition(Object toParse, Type type) {
        this.type = type;
        this.parameterValue = (String) toParse; // FIXME: support lists (refer
                                                // to MessageAction)
        // TODO: even better might be to allow a list of class@level, eg.
        // [FARMER@1-5, WORKER@6, etc]
        // can use a Map<String, IntRange>
    }

    @Override
    protected boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        if (!Dependencies.hasMcmmo())
            return false; // shouldn't happen, but just in case

        // Grab the character manager & check values against the saved
        // value/values
/*        switch (type) {
        case CLASS:
            return manager.getHero(occurrence.getPlayerAttacker())
                    .getHeroClass().toString().equalsIgnoreCase(parameterValue);
        case LEVEL:
            return String.valueOf(
                    manager.getHero(occurrence.getPlayerAttacker()).getLevel())
                    .equalsIgnoreCase(parameterValue);
        }*/
        return false;
    }

    // @Override
    public List<Condition> parse(ConfigurationNode parseMe) {
        List<Condition> conditions = new ArrayList<Condition>();

        // allow: mcmmolevel: [ARCHERY@5+, EXCAVATION@1-4, etc]
        // support [] for AND & {} for OR?
        
        // return the empty list if no heroes, no use scanning through the
        // parameters at all
        if (!Dependencies.hasHeroes())
            return conditions;

        // loop through valid parameters and add a new condition for each one
        // that exists in "parseMe"
        for (String key : matches.keySet()) {
            if (parseMe.get("mcmmo." + key) != null)
                conditions.add(new McmmoCondition(parseMe.get(key), matches
                        .get(key)));
        }
        return conditions;
    }
}
