package com.gmail.zariust.otherdrops.parameters.conditions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Condition;

/**
 * @author Bane
 * 
 */
public class PlayerSneakCheck extends Condition {

    String name = "PlayerSneakCheck";

    final private Boolean isEnabled;

    public PlayerSneakCheck(Boolean Enable)
    {
        isEnabled = Enable;
    }

    @Override
    public boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        Log.logInfo("Starting PlayerSneak check !", Verbosity.HIGHEST);

        Player aPlayer = occurrence.getPlayerAttacker();

        if (aPlayer instanceof Player && aPlayer.isSneaking() == isEnabled)
        {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Condition> parse(ConfigurationNode node) {
        Log.dMsg("PlayerSneakCheck.parse(): Checking if PlayerSneakcheck should be enabled! " + node.toString());

        Boolean sneakSetting = node.getBoolean("player.sneaking", null);

        Log.dMsg("PlayerSneakCheck.parse(): Player.sneaking=" + sneakSetting);
        List<Condition> conditionList = new ArrayList<Condition>();

        if (sneakSetting != null)
        {
            Log.logInfo("PlayerSneakCheck.parse(): Adding PlayerSneakcheck to list of valid conditions!", Verbosity.HIGHEST);
            conditionList.add(new PlayerSneakCheck(sneakSetting));
        }

        return conditionList;
    }

}
