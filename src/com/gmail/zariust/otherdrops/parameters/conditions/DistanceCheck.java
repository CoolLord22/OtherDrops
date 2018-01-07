package com.gmail.zariust.otherdrops.parameters.conditions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.parameters.Condition;

public class DistanceCheck extends Condition {

    String name = "DistanceCheck";
    String key = "distance";

    private Location locCheck = null;
    private Integer distance = 0;

    public DistanceCheck(Integer distance, Location locCheck) {
        this.distance = distance;
        this.locCheck = locCheck;
    }

    @Override
    public boolean checkInstance(CustomDrop drop, OccurredEvent occurrence) {
        if (locCheck == null)
            return false;
        Location loc = occurrence.getLocation();

        Log.logInfo("DistanceCheck - start", Verbosity.HIGHEST);

        Log.logInfo("DistanceCheck - " + loc.toString() + " vs " + locCheck, Verbosity.HIGH);

        Double actualDistance = check2dDistance(loc.getX(), loc.getZ(), locCheck.getX(), locCheck.getZ());
        if (actualDistance > distance) {
            return true;
        } else {
            return false;
        }
    }

    private Double check2dDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    @Override
    public List<Condition> parse(ConfigurationNode node) {
        Location locationToMeasureAgainst = new Location(null, 0, 0, 0);
        String loreName = node.getString("lorename");
        String getConfig = node.getString("distance");
        Log.logInfo("Loading distance condition: " + getConfig + " lorename: " + loreName, Verbosity.HIGHEST);
        if (getConfig == null)
            return null;

        String[] split = getConfig.split("@");
        if (split.length > 1) {
            String[] split2 = split[1].split(";");
            locationToMeasureAgainst = new Location(null, Double.valueOf(split2[0]), Double.valueOf(split2[1]), Double.valueOf(split2[2]));
        }

        List<Condition> conditionList = new ArrayList<Condition>();
        conditionList.add(new DistanceCheck(Integer.valueOf(split[0]), locationToMeasureAgainst));
        return conditionList;
    }

}
