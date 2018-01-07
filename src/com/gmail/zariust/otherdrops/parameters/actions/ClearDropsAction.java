package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.List;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Action;

public class ClearDropsAction extends Action {

    // private Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
    public enum ClearType {
        XP, DROP, EQUIPMENT
    }

    private final ClearType clearType;

    public ClearDropsAction(ClearType cType) {
        this.clearType = cType;
    }

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {

        return true;
    }

    static List<Action> getClearXpAction() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ClearDropsAction(ClearType.XP));
        return actions;

    }

    static List<Action> getClearDropsAction() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ClearDropsAction(ClearType.DROP));
        return actions;
    }

    static List<Action> getClearEquipmentAction() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ClearDropsAction(ClearType.EQUIPMENT));
        return actions;
    }

    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        // TODO Auto-generated method stub
        return null;
    }
}
