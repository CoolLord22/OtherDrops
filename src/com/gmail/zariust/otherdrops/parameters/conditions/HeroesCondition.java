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
import com.herocraftonline.heroes.characters.CharacterManager;

public class HeroesCondition extends Condition {
    private enum Type {
        CLASS, LEVEL
    }

    static private Map<String, Type> matches = new HashMap<String, Type>();
    static {
        matches.put("class", Type.CLASS);
        matches.put("level", Type.LEVEL);
    }

    private final Type               type;
    private final String             parameterValue;

    public HeroesCondition(Object toParse, Type type) {
        this.type = type;
        this.parameterValue = (String) toParse; // FIXME: support lists (refer
                                                // to MessageAction)
        // TODO: even better might be to allow a list of class@level, eg.
        // [FARMER@1-5, WORKER@6, etc]
        // can use a Map<String, IntRange>
    }

    @Override
    protected boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        if (!Dependencies.hasHeroes())
            return false; // shouldn't happen, but just in case

        // Grab the character manager & check values against the saved
        // value/values
        CharacterManager manager = Dependencies.getHeroes()
                .getCharacterManager();
        switch (type) {
        case CLASS:
            return manager.getHero(occurrence.getPlayerAttacker())
                    .getHeroClass().toString().equalsIgnoreCase(parameterValue);
        case LEVEL:
            return String.valueOf(
                    manager.getHero(occurrence.getPlayerAttacker()).getLevel())
                    .equalsIgnoreCase(parameterValue);
        }
        return false;
    }

    // @Override
    public List<Condition> parse(ConfigurationNode parseMe) {
        List<Condition> conditions = new ArrayList<Condition>();

        // return the empty list if no heroes, no use scanning through the
        // parameters at all
        if (!Dependencies.hasHeroes())
            return conditions;

        // loop through valid parameters and add a new condition for each one
        // that exists in "parseMe"
        for (String key : matches.keySet()) {
            if (parseMe.get("heroes." + key) != null)
                conditions.add(new HeroesCondition(parseMe.get(key), matches
                        .get(key)));
        }
        return conditions;
    }
}
