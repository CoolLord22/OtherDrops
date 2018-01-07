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
import com.gmail.zariust.otherdrops.parameters.actions.DamageAction;
import com.gmail.zariust.otherdrops.parameters.actions.MessageAction;
import com.gmail.zariust.otherdrops.parameters.actions.MoneyAction;
import com.gmail.zariust.otherdrops.parameters.actions.ParticleAction;
import com.gmail.zariust.otherdrops.parameters.actions.PlayerAction;
import com.gmail.zariust.otherdrops.parameters.actions.PotionAction;
import com.gmail.zariust.otherdrops.parameters.actions.SoundAction;

public abstract class Action extends Parameter {
    protected static Set<Action> actions = new HashSet<Action>();

    public abstract boolean act(CustomDrop drop, OccurredEvent occurence);

    public static boolean registerAction(Action register) {
        Log.logInfo("Actions - registering: " + register.toString(),
                Verbosity.EXTREME);
        actions.add(register);
        return false;
    }

    public static List<Action> parseNodes(ConfigurationNode node) {
        List<Action> actionsList = new ArrayList<Action>();
        for (Action action : actions) {
            actionsList.addAll(action.parse(node));
        }
        return actionsList;
    }

    abstract public List<Action> parse(ConfigurationNode parseMe);

    public static void registerDefaultActions() {
        registerAction(new DamageAction(null, null));
        registerAction(new MessageAction(null, null));
        registerAction(new MoneyAction(null, null));
        registerAction(new ParticleAction(null, null, true));
        registerAction(new PlayerAction(null, null, null));
        registerAction(new PotionAction(null, null, true));
        registerAction(new SoundAction(null, null));
    }

}
