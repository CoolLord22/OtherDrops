// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops.subject;

import static com.gmail.zariust.common.Verbosity.HIGHEST;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.common.CMEnchantment;
import com.gmail.zariust.common.CommonEnchantments;
import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.data.ItemData;
import com.gmail.zariust.otherdrops.options.ConfigOnly;
import com.gmail.zariust.otherdrops.options.ToolDamage;

@ConfigOnly(PlayerSubject.class)
public class ToolAgent implements Agent {
    private ItemStack           actualTool;
    private final Material      id;
    private final Data          data;
    private List<CMEnchantment> enchantments;
    public int                  quantityRequired;
    private String              loreName;

    public ToolAgent() {
        this((Material) null);
    }

    public ToolAgent(Material tool) {
        this(tool, null, 1);
    }

    public ToolAgent(Material tool, int d, List<CMEnchantment> enchantment,
            int quantity) {
        this(tool, new ItemData(d), quantity);
        enchantments = enchantment;
    }

    public ToolAgent(Material tool, int d) {
        this(tool, new ItemData(d), 1);
    }

    public ToolAgent(ItemStack item) {
        this(item == null ? null : item.getType(), item == null ? null
                : new ItemData(item), item == null ? 1 : item.getAmount());

        actualTool = item;
        if (item != null && item.getItemMeta() != null)
            loreName = item.getItemMeta().getDisplayName();
    }

    public ToolAgent(Material tool, Data d, List<CMEnchantment> enchList,
            int quantity, String loreName) {
        id = tool;
        data = d;
        enchantments = enchList;
        this.quantityRequired = quantity;
        this.loreName = loreName;
    }

    public ToolAgent(Material tool, Data d, List<CMEnchantment> enchList,
            int quantity) {
        id = tool;
        data = d;
        enchantments = enchList;
        this.quantityRequired = quantity;
    }

    public ToolAgent(Material tool, Data d, int quantity) {
        id = tool;
        data = d;
        this.quantityRequired = quantity;
    }

    private boolean isEqual(ToolAgent tool) {
        if (tool == null)
            return false;
        if (id == null)
            return true; // null means ANY_OBJECT
        if (data == null)
            return (id == tool.id); // no data to check (wildcard) so just check
                                    // id versus tool.id
        return id == tool.id && data.equals(tool.data);
    }

    private boolean isMatch(ToolAgent tool) {
        if (tool == null)
            return false;
        return id == tool.id && data.matches(tool.data);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ToolAgent))
            return false;
        ToolAgent tool = (ToolAgent) other;
        return isEqual(tool);
    }

    @Override
    public boolean matches(Subject other) {
        // example of data passed:
        // this: id=DIAMOND_SPADE, data=null (data is null unless specified in
        // the config)
        // other=PLAYER@Xarqn with DIAMOND_SPADE@4

        // Only players can hold & use tools - fail match if not a PlayerSubject
        if (!(other instanceof PlayerSubject))
            return false;
        // Find the tool that the player is holding
        PlayerSubject tool = (PlayerSubject) other;

        Log.logInfo("tool agent check : id=" + id.toString() + " gettool="
                + tool.getTool() + " material=" + tool.getMaterial()
                + " id=mat:" + (id == tool.getMaterial()), Verbosity.EXTREME);
        if (!enchantments.isEmpty()) {
            boolean match = false;
            match = CommonEnchantments.matches(enchantments,
                    tool.getTool().actualTool.getEnchantments());
            if (!match)
                return false;
        }

        if (loreName != null && !loreName.isEmpty()) {
            if (tool.getTool().loreName == null)
                return false;
            if (!this.loreName.equals(tool.getTool().loreName))
                return false;
        }

        if (id == null)
            return true;
        else if (quantityRequired > tool.getTool().quantityRequired
                && id.toString() != "AIR") {
            Log.logInfo("Toolagent check: quantity required failed.",
                    Verbosity.HIGHEST);
            return false;
        } else if (data == null)
            return id == tool.getMaterial();
        else
            return isMatch(tool.getTool());
    }

    public Material getMaterial() {
        return id;
    }

    @Override
    public int hashCode() {
        return new HashCode(this).get(id);
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public ItemCategory getType() {
        return ItemCategory.PLAYER;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    public static Agent parse(String name, String state) {
        return parse(name, state, null, "");
    }

    public static Agent parse(String name, String state,
            List<CMEnchantment> enchPass, String loreName) {
        name = name.toUpperCase();
        state = state.toUpperCase();

        int quantityRequired = getToolQuantity(name, state);

        Material mat = CommonMaterial.matchMaterial(name);
        if (mat == null) {
            Log.logInfo("Unrecognized tool: " + name
                    + (state.isEmpty() ? "" : "@" + state), HIGHEST);
            return null;
        }

        // If "state" is empty then no data defined, make sure we don't use 0 as
        // data otherwise later matching fails
        if (state.isEmpty())
            return new ToolAgent(mat, null, enchPass, quantityRequired,
                    loreName);

        // Parse data, which could be an integer or an appropriate enum name
        try {
            int d = Integer.parseInt(state);
            return new ToolAgent(mat, d, enchPass, quantityRequired);
        } catch (NumberFormatException e) {
        }
        Data data = null;
        try {
            data = ItemData.parse(mat, state);
        } catch (IllegalArgumentException e) {
            Log.logWarning(e.getMessage());
            return null;
        }
        if (data != null)
            return new ToolAgent(mat, data, enchPass, quantityRequired,
                    loreName);
        return new ToolAgent(mat, null, enchPass, quantityRequired, loreName);
    }

    private static int getToolQuantity(String name, String state) {
        String[] nameSplit = name.split("/");
        String[] stateSplit = state.split("/");

        if (nameSplit.length > 1)
            return Integer.parseInt(nameSplit[1]);
        else if (stateSplit.length > 1)
            return Integer.parseInt(stateSplit[1]);
        else
            return 1;
    }

    @Override
    public void damage(int amount) {
    }

    @Override
    public void damageTool(ToolDamage amount, Random rng) {
    }

    @Override
    public String toString() {
        if (id == null)
            return "ANY_OBJECT";
        String ret = id.toString();
        // TODO: Will data ever be null, or will it just be 0?
        if (data != null)
            ret += "@" + data.get(id);
        ret += "/" + quantityRequired;
        return ret;
    }

    @Override
    public String getReadableName() {
        if (id == null)
            return "ANY_OBJECT";
        return id.toString().toLowerCase().replace("_", " ");
    }

}
