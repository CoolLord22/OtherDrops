package com.gmail.zariust.otherdrops.parameters.conditions;

import java.util.ArrayList;
import java.util.List;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Condition;
import com.gmail.zariust.otherdrops.parameters.actions.MessageAction;

public class CooldownCheck extends Condition {
    private String       cooldown;
    private final Double time;

    String               name = "CooldownCheck";

    public CooldownCheck(String cooldown, Double time) {
        this.cooldown = cooldown;
        this.time = time;
    }

    @Override
    public boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        cooldown = MessageAction.parseVariables(cooldown, drop, occurrence, 1);

        PlayerCooldown pc = Cooldown.getCooldown(cooldown);
        if (pc == null) // The player hasn't activated a cooldown for this yet
        {
            // name for cooldown, length of cooldown in milliseconds
            Cooldown.addCooldown(cooldown, (long) (time * 1000));
            return true;
        } else {
            if (pc.isOver()) {
                pc.reset();
                return true;
            } else {
                Log.dMsg("Cooldown '" + cooldown + "' has: "
                        + ((double) pc.getTimeLeft() / 1000) + "Seconds left");
                return false;
            }
        }
    }

    @Override
    public List<Condition> parse(ConfigurationNode node) {
        String cooldown = node.getString("cooldown");
        Double time = 2.0;

        if (cooldown == null)
            return null;

        String[] split = cooldown.split("@");
        cooldown = split[0];
        if (split.length > 1)
            time = Double.valueOf(split[1]);

        List<Condition> conditionList = new ArrayList<Condition>();
        conditionList.add(new CooldownCheck(cooldown, time));
        return conditionList;
    }

}
