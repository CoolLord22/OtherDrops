package com.gmail.zariust.otherdrops.data.itemmeta;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.ItemData;
import com.gmail.zariust.otherdrops.subject.Target;

public abstract class OdItemMeta {

    public static OdItemMeta parse(String sub, ItemData.ItemMetaType metaType) {
        switch (metaType) {
        case BOOK:
            return OdBookMeta.parse(sub);
        case LEATHER:
            return OdLeatherArmorMeta.parse(sub);
        case SKULL:
            return OdSkullMeta.parse(sub);
        case ENCHANTED_BOOK:
            return OdEnchantedBookMeta.parse(sub);
        case FIREWORK:
            return OdFireworkMeta.parse(sub);
        default:
            break;

        }
        return null;
    }

    public abstract ItemStack setOn(ItemStack stack, Target source);

    // getColorFrom()
    // Copyright (C) 2013 Zarius Tularial
    //
    // This method released under Evil Software License v1.1
    // <http://fredrikvold.info/ESL.htm>

    /**
     * getColorFrom(string) - obtain "Rich" colors from org.bukkit.Color
     * otherwise match using DyeColor. (needed as there is no way to go from the
     * string directly to a "Color"). Support "R/G/B" format using hex format
     * eg. "#FF0000" = red
     * 
     * @author zarius
     * @param sub
     * @return
     */
    public static Color getColorFrom(String sub) {
        Log.dMsg("PARSING COLOR!" + sub);
        Color color = null;
        if (sub.matches("(?i)#[0-9A-F]{6}")) {
            sub = sub.substring(1);
            Log.dMsg("PARSING COLOR!" + sub.substring(0, 2) + "."
                    + sub.substring(2, 4) + "." + sub.substring(4));

            int red = Integer.valueOf(sub.substring(0, 2), 16);
            int blue = Integer.valueOf(sub.substring(2, 4), 16);
            int green = Integer.valueOf(sub.substring(4, 6), 16);
            if (blue > 255)
                blue = 255;
            if (red > 255)
                red = 255;
            if (green > 255)
                green = 255;

            color = Color.fromBGR(blue, green, red);
        } else if (sub.matches("(?i)RICH.*")) {
            if (sub.equalsIgnoreCase("RICHGREEN")) {
                color = Color.GREEN;
            } else if (sub.equalsIgnoreCase("RICHRED")) {
                color = Color.RED;
            } else if (sub.equalsIgnoreCase("RICHBLUE")) {
                color = Color.BLUE;
            } else if (sub.equalsIgnoreCase("RICHYELLOW")) {
                color = Color.YELLOW;
            }
        } else {
            // FIXME: add ability to use Color values too - they are
            // richer/stronger colors
            color = DyeColor.valueOf(sub.toUpperCase()).getColor();
        }
        return color;
    }

    // TODO:

    // add .matches & .parseFromItem to each class
}
