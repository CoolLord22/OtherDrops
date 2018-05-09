package com.gmail.zariust.otherdrops.things;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.gmail.zariust.common.CMEnchantment;
import com.gmail.zariust.common.CommonEnchantments;
import com.gmail.zariust.otherdrops.ItemIDReplacer;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.data.ItemData;

public class ODItem {
    public String               name;
    private String              dataString;
    public String               enchantmentString;
    public List<CMEnchantment>  enchantments = new ArrayList<CMEnchantment>();
    public String               displayname;
    public final List<String>   lore         = new ArrayList<String>();
    private Material            material;
    private Data                data;

    /**
     * @param drop
     * @param defaultData
     * @param loreName
     * @return
     */
    public static ODItem parseItem(String drop, String defaultData) {
        ODItem item = new ODItem();
        item.dataString = defaultData;

        String[] firstSplit = drop.split("[@:;~]", 2);
        if (firstSplit.length > 1) {
            // if extra fields are found, parse them - firstly separating out the type of "thing" this is
            item.name = firstSplit[0];
            String firstChar = drop.substring(item.name.length(),
                    item.name.length() + 1);
            if (firstChar.matches("[^~]")) {
                // only want to use a semi-colon rather than @ or : but preserve the ~
                firstChar = ";"; 
            } else if (firstChar.matches("~")) {
                item.displayname = "";
            }
            drop = firstChar + firstSplit[1];

            // check for initial data value and enchantment to support old format
            if (drop.matches("([;])([^;!]+)!.*")) {
                Log.dMsg("PARSING INTIAL DATA");
                String[] dataEnchSplit = drop.split("!", 2);
                item.dataString = dataEnchSplit[0].substring(1);
                drop = ";"+dataEnchSplit[1];
                
            }

            // then, loop through each ";<value>" or "~<value>" pair and parse accordingly
            Pattern p = Pattern.compile("([~;])([^~;]+)");
            Matcher m = p.matcher(drop);
            while (m.find()) {
                String key = m.group(1);
                String value = m.group(2);

                if (key != null && value != null) {
                    if (key.equals("~")) {
                        item.displayname = ChatColor.translateAlternateColorCodes('&', value);
                    } else if (item.displayname != null
                            && !item.displayname.isEmpty()) {
                        // displayname found, treat next as lore
                        item.lore.add(value);
                    } else {
                        // first check for enchantment
                        List<CMEnchantment> ench = CommonEnchantments
                                .parseEnchantments(value);
                        if (ench == null || ench.isEmpty()) {
                            // otherwise assume data
                            item.dataString = value;
                        } else {
                            item.enchantments.addAll(ench);
                        }
                    }
                }
            }
        } else {
            item.name = drop;
        }

        return item;
    }

    /**
     * @param name
     * @return
     */
    @SuppressWarnings("deprecation")
	public Material getMaterial() {
        if (this.material == null) {
        	if(this.name.matches("[0-9]+")) {
                Log.logWarning("Error while parsing: " + this.name + ". Support for numerical IDs has been dropped! Locating item ID...");
            	Log.logWarning("Please replace the occurence of '" + this.name + "' with '" + Material.getMaterial(Integer.parseInt(this.name)).toString() + "'");
            	ItemIDReplacer.replaceFile(Integer.parseInt(name), Material.getMaterial(Integer.parseInt(name)).toString());
            	Log.logWarning("The drop has been disabled to prevent issues!");
        	}
        }
        return this.material;
    }

    public String getDataString() {
        return (dataString == null ? "" : dataString);
    }

    /**
     * @param item
     * @param mat
     * @return
     */
    public Data getData() {
        if (data == null && dataString != null) {
            if (dataString.equals("!"))
                dataString = "";

            // Parse data, which could be an integer or an appropriate enum
            // name
            this.data = parseDataFromString(this.dataString);
        }
        return data;
    }

    /**
     * @return
     * 
     */
    public Data parseDataFromString(String dataString) {
        Data returnVal = null;
        try {
            int d = Integer.parseInt(dataString);
            returnVal = new ItemData(d);
        } catch (NumberFormatException e) {
        }
        if (returnVal == null) {
            try {
                returnVal = ItemData.parse(this.getMaterial(), dataString);
                if (returnVal == null)
                    returnVal = new ItemData(0);
            } catch (IllegalArgumentException e) {
                Log.logWarning(e.getMessage());
                returnVal = null;
            }
        }
        return returnVal;
    }

    public String getDisplayName() {
        return displayname;
    }

    public List<CMEnchantment> getEnchantments() {
        if (enchantments == null) {
            if (enchantmentString == null) {
                enchantments = new ArrayList<CMEnchantment>();
            } else {
                enchantments = CommonEnchantments
                        .parseEnchantments(enchantmentString);
            }
        }
        return enchantments;
    }

    public static ODItem parseItem(String blockName) {
        return parseItem(blockName, "");
    }

}