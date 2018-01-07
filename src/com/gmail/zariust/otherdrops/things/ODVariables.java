package com.gmail.zariust.otherdrops.things;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.options.IntRange;

public class ODVariables {
    Map<String, String> variables = new HashMap<String, String>();

    public ODVariables() {
        variables.put("%time", new SimpleDateFormat(OtherDropsConfig.gTimeFormat).format(Calendar.getInstance().getTime()));
        variables.put("%date", new SimpleDateFormat(OtherDropsConfig.gDateFormat).format(Calendar.getInstance().getTime()));
    }

    public ODVariables setDeathMessage(String val) {
        variables.put("%deathmessage", val);
        return this;
    }

    public ODVariables setPlayerName(String val) {
        variables.put("%p", val);
        variables.put("%P", val.toUpperCase());
        return this;
    }

    public ODVariables setVictimName(String val) {
        variables.put("%v", val);
        return this;
    }

    public ODVariables setDropName(String val) {
        variables.put("%d", val.replaceAll("[_-]", " ").toLowerCase());
        variables.put("%D", val.replaceAll("[_-]", " ").toUpperCase());
        return this;
    }

    public ODVariables setToolName(String val) {
        variables.put("%t", val.replaceAll("[_-]", " ").toLowerCase());
        variables.put("%T", val.replaceAll("[_-]", " ").toUpperCase());
        return this;
    }

    public ODVariables setloreName(String val) {
        variables.put("%displayname", val);
        variables.put("%lorename", val);
        return this;
    }

    public ODVariables setQuantity(String val) {
        variables.put("%q", val);
        variables.put("%Q", val);
        return this;
    }

    public ODVariables setLocation(Location loc) {
        if (loc == null)
            return this;
        variables.put("%loc.x", String.valueOf(loc.getX()));
        variables.put("%loc.y", String.valueOf(loc.getY()));
        variables.put("%loc.z", String.valueOf(loc.getZ()));
        variables.put("%loc.world", loc.getWorld().getName());
        return this;
    }

    public ODVariables setTargetName(String val) {
        variables.put("%targetname", val);
        return this;
    }

    public ODVariables custom(String key, String value) {
        variables.put(key, value);
        return this;
    }

    public String parse(String msg) {
        msg = parseMultipleOptions(msg);

        for (Entry<String, String> entrySet : variables.entrySet()) {
            msg = msg.replaceAll(entrySet.getKey(), entrySet.getValue());
        }

        return msg;
    }

    public static String parseVariables(String msg) {
        return new ODVariables().parse(msg);
    }

    public static List<String> parseVariables(List<String> stringList) {
        List<String> parsedStringList = new ArrayList<String>();
        for (String string : stringList) {
            parsedStringList.add(parseVariables(string));
        }
        return parsedStringList;
    }

    /**
     * PreTranslate is intended to parse any non-dynamic variables at the time
     * of config loading. This method parses each line of a List of Strings.
     * 
     * @param lines
     * @return parsed string
     */
    public static List<String> preParse(List<String> lines) {
        if (lines == null)
            return null;

        List<String> tmp = new ArrayList<String>();

        for (String str : lines) {
            tmp.add(ODVariables.preParse(str));
        }

        return tmp;
    }

    /**
     * PreTranslate is intended to parse any non-dynamic variables at the time
     * of config loading.
     * 
     * @param line
     * @return
     */
    public static String preParse(String line) {
        if (line == null)
            return null;

        return substituteColorCodes(line);
    }

    static String parseMultipleOptions(String msg) {
        if (msg.contains("|")) {
            // Select one of multiple options eg. <sword|mace|dagger> of +<1-4> damage =
            // random results, e.g. "sword of +1 damage" or "dagger of +3 damage"

            // Match expression is:
            // * any 2 values separated by a pipe, followed by optional further values, all surrounded by angle brackets
            // * e.g. <sword|mace> or <1|2|3>

            msg = new ODMatch(msg).match("<(([^|<>]+?[|][^|<>]+?[|]*)([^|<>]+?[|]*)*?)>", new ODMatchRunner() {
                @Override
                public String runMatch(String matched) {
                    Log.logInfo("MATCHED: "+matched);
                    String[] split = matched.split("\\|");
                    return split[OtherDrops.rng.nextInt(split.length)];
                }
            });
        }

        if (msg.contains("~") || msg.contains("-")) {
            // Select one of a given range of numbers eg. (3~7) -> number
            // between 3 & 7 (inclusive)
            msg = new ODMatch(msg).match("<([0-9]+(~|-)[0-9]+)>", new ODMatchRunner() {
                @Override
                public String runMatch(String matched) {
                    return IntRange.parse(matched).getRandomIn(OtherDrops.rng).toString();
                }
            });
        }

        return msg;
    }

    private static String substituteColorCodes(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        msg = msg.replace("&&", "&"); // replace "escaped" ampersand
        return msg;
    }

}
