package com.gmail.zariust.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.options.IntRange;

public class CommonEnchantments {
    // aliases

    public static List<CMEnchantment> parseEnchantments(String enchantments) {
        List<CMEnchantment> enchList = new ArrayList<CMEnchantment>();

        if (!enchantments.isEmpty()) {
            String[] split3 = enchantments.split("!");
            Log.logInfo("CommonEnch: processing enchantment: " + enchantments,
                    Verbosity.HIGHEST);
            for (String loopEnchantment : split3) {
                CMEnchantment cmEnch = parseFromString(loopEnchantment);
                if (cmEnch != null)
                    enchList.add(cmEnch);
            }
        }

        return enchList;
    }

    /**
     * @param input
     * @return
     */
    private static CMEnchantment parseFromString(String input) {
        String[] enchSplit = input.split("#");
        String enchString = enchSplit[0].trim().toLowerCase();

        // check for no enchantment match (note string already lowercase)
        if (enchString.equals("noench")) {
            CMEnchantment cmEnch = new CMEnchantment();
            cmEnch.setNoEnch(true);
            return cmEnch;
        }

        String enchLevel = "";
        if (enchSplit.length > 1)
            enchLevel = enchSplit[1];
        IntRange enchLevelInt = null;

        try {
            if (!enchLevel.isEmpty() && enchLevel.matches("[0-9-~]*"))
                enchLevelInt = IntRange.parse(enchLevel);
        } catch (NumberFormatException x) {
            // do nothing - default enchLevelInt of 1 is fine (the drop itself
            // will set this to ench.getStartLevel())
            enchLevelInt = null;
        }

        Enchantment ench = getEnchantment(enchString);

        if (ench == null && !enchString.equalsIgnoreCase("random")) {
            Log.logInfo("Enchantment (" + input + "=>" + enchString
                    + ") not valid - this isn't necessarily a problem as other data values as checked as enchantments first.", Verbosity.HIGH);
            return null;
        }

        if (ench != null) {
            if (enchLevelInt == null && !enchLevel.equals("?")) {
                enchLevelInt = IntRange.parse("1");

                if (!OtherDropsConfig.enchantmentsIgnoreLevel) {
                    if (enchLevelInt.getMin() < ench.getStartLevel())
                        enchLevelInt.setMin(ench.getStartLevel());
                    else if (enchLevelInt.getMax() > ench.getMaxLevel())
                        enchLevelInt.setMax(ench.getMaxLevel());
                }
            }
        }

        CMEnchantment cmEnch = new CMEnchantment();
        cmEnch.setEnch(ench);

        if (enchLevel.equals("?"))
            cmEnch.setLevelRange(null);
        else
            cmEnch.setLevelRange(enchLevelInt);

        return cmEnch;
    }

    static Map<String, String> aliases = new HashMap<String, String>();
    static {
        aliases.put("aspectfire", "fireaspect");
        aliases.put("sharpness", "damageall");
        aliases.put("smite", "damageundead");
        aliases.put("punch", "arrowknockback");
        aliases.put("looting", "lootbonusmobs");
        aliases.put("fortune", "lootbonusblocks");
        aliases.put("baneofarthropods", "damageundead");
        aliases.put("power", "arrowdamage");
        aliases.put("flame", "arrowfire");
        aliases.put("infinity", "arrowinfinite");
        aliases.put("unbreaking", "durability");
        aliases.put("efficiency", "digspeed");
        aliases.put("smite", "damageundead");
    }

    /**
     * Takes a enchantment name by string and matches to an enchantment value
     * using a little fuzzy matching (strip any space, underscore or dash and
     * case doesn't matter)
     * 
     * @author zarius
     * @param enchString
     * @return matching Enchantment or null if none found
     */
    private static Enchantment getEnchantment(String enchString) {
        // Clean up string - make lowercase and strip space/dash/underscore
        enchString = enchString.toLowerCase().replaceAll("[\\s_-]", "");

        // Set up aliases (this could probably be done outside the function so
        // we only do it once (eg. in a support class init or read from a file)

        // If an alias exists, use it
        String alias = aliases.get(enchString);
        if (alias != null)
            enchString = alias;

        // Loop through all enchantments and match (case insensitive and
        // ignoring space,
        // underscore and dashes
        for (Enchantment value : Enchantment.values()) {
            if (enchString.equalsIgnoreCase(value.getName().replaceAll(
                    "[\\s_-]", ""))) {
                return value;
            }
        }

        return null; // nothing found.
    }

    public static boolean containsEnchantment(String enchantments,
            List<String> enchList) {
        return false;
    }

    public static ItemStack applyEnchantments(ItemStack stack,
            List<CMEnchantment> enchantments) {
        if (enchantments == null)
            return stack;

        if (!(enchantments.isEmpty())) {
            for (CMEnchantment cmEnch : enchantments) {
                Enchantment ench = cmEnch.getEnch(stack);
                int level = cmEnch.getLevel();

                try {
                    if (OtherDropsConfig.enchantmentsUseUnsafe) {
                        stack.addUnsafeEnchantment(ench, level);
                    } else {
                        stack.addEnchantment(ench, level);
                    }
                    Log.logInfo("Enchantment (" + ench.getStartLevel() + "-"
                            + ench.getMaxLevel() + "): " + ench.getName() + "#"
                            + level + " applied.", Verbosity.HIGHEST);
                } catch (IllegalArgumentException ex) {
                    Log.logInfo("Enchantment (" + ench.getStartLevel() + "-"
                            + ench.getMaxLevel() + "): " + ench.getName() + "#"
                            + level + " cannot be applied (" + ex.getMessage()
                            + ").", Verbosity.HIGHEST);
                }
            }
        }
        return stack;
    }

    /**
     * @param stack
     * @return
     */
    public static Enchantment getRandomEnchantment(ItemStack stack) {
        Enchantment ench;
        int length = Enchantment.values().length;
        ench = Enchantment.values()[OtherDrops.rng.nextInt(length - 1)];
        int count = 0;
        if (!OtherDropsConfig.enchantmentsUseUnsafe) {
            while ((stack == null || !ench.canEnchantItem(stack)) && count < 50) {
                ench = Enchantment.values()[OtherDrops.rng.nextInt(length - 1)];
                count++; // try only a limited number of times
            }
        }
        return ench;
    }

    // eg. damage_all, d_arach = d_arach, damage_all
    public static boolean matches(List<CMEnchantment> customEnchs,
            Map<Enchantment, Integer> toolEnchs) {
        int matchCount = 0;
        for (CMEnchantment ench : customEnchs) {
            if (ench.getNoEnch()) {
                if (!toolEnchs.isEmpty()) {
                    return false;
                } else {
                    return true;
                }
            }
            for (Entry<Enchantment, Integer> entry : toolEnchs.entrySet()) {
                if (ench.getEnchRaw() != null)
                    if (ench.getEnchRaw() == entry.getKey()) {
                        if (ench.getLevelRange().contains(entry.getValue()))
                            matchCount++;
                    }
            }
        }

        if (matchCount != customEnchs.size())
            return false;

        return true;
    }

}
