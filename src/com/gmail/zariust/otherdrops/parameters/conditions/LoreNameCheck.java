package com.gmail.zariust.otherdrops.parameters.conditions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Condition;
import com.gmail.zariust.otherdrops.parameters.actions.MessageAction;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.ProjectileAgent;
import com.gmail.zariust.otherdrops.things.ODVariables;

public class LoreNameCheck extends Condition {

    String               name = "LoreNameCheck";
    private final String loreName;

    public LoreNameCheck(String loreName) {
        this.loreName = ODVariables.preParse(loreName);
    }

    @Override
    public boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        String parsedLorename = MessageAction.parseVariables(loreName, drop,
                occurrence, -1);
        Log.logInfo("Starting lorename check (" + parsedLorename + ")",
                Verbosity.HIGHEST);
        if (occurrence.getTool() instanceof PlayerSubject) {
            return checkLoreName((PlayerSubject) occurrence.getTool(),
                    parsedLorename);
        } else if (occurrence.getTool() instanceof ProjectileAgent) {
            ProjectileAgent pa = (ProjectileAgent) occurrence.getTool();
            if (pa.getShooter() instanceof PlayerSubject) {
                return checkLoreName((PlayerSubject) pa.getShooter(),
                        parsedLorename);
            }
        }
        return false;
    }

    private boolean checkLoreName(PlayerSubject player, String parsedLorename) {
        ItemStack item = player.getPlayer().getItemInHand();
        if (item == null)
            return false; // not sure when item would be null but it can be

        Log.logInfo("tool name = " + item.getType().name(), Verbosity.HIGHEST);
        if (item.hasItemMeta()) {
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName != null) {
                Log.logInfo("Checking for lorename condition... '"
                        + displayName + "' == '" + parsedLorename + "'",
                        Verbosity.HIGHEST);
                if (displayName.equalsIgnoreCase(parsedLorename))
                    return true;
            } else {
                Log.logInfo("Displayname is null.", Verbosity.HIGHEST);
            }
        }
        return false;
    }

    @Override
    public List<Condition> parse(ConfigurationNode node) {
        String loreName = node.getString("lorename");
        if (loreName == null) {
            loreName = node.getString("displayname");
            if (loreName == null)
                return null;
        }

        List<Condition> conditionList = new ArrayList<Condition>();
        conditionList.add(new LoreNameCheck(loreName));
        return conditionList;
    }

}
