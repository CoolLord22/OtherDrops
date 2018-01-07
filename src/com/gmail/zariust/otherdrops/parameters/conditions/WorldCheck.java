package com.gmail.zariust.otherdrops.parameters.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Condition;

public class WorldCheck extends Condition {
    private final Map<org.bukkit.World, Boolean> worlds;

    public WorldCheck(List<String> list) {
        this.worlds = null;
    }

    @Override
    public boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        org.bukkit.World world = occurrence.getWorld();

        return CustomDrop.checkList(world, worlds);
    }

    // @Override
    @SuppressWarnings("unchecked")
    public List<Condition> parseInstance(Object object) {
        if (object == null)
            return null;

        List<String> list = new ArrayList<String>();
        if (object instanceof List)
            list = (List<String>) object;
        else
            list = Collections.singletonList(object.toString());

        List<Condition> conditionList = new ArrayList<Condition>();
        conditionList.add(new WorldCheck(list));
        return conditionList;
    }

    protected static List<Condition> parseInstance(ConfigurationNode node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Condition> parse(ConfigurationNode parseMe) {
        // TODO Auto-generated method stub
        return null;
    }

}
