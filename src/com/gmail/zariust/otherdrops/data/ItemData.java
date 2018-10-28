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

package com.gmail.zariust.otherdrops.data;

import static com.gmail.zariust.common.Verbosity.EXTREME;

import org.bukkit.CoalType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.data.itemmeta.OdItemMeta;

public class ItemData implements Data, RangeableData {
    private int       data;
    private String    dataString;
    public OdItemMeta itemMeta;

    public ItemData(int d) {
        data = d;
    }

    public ItemData(int d, String state) {
        data = d;
        setDataString(state);
    }

    public ItemData(ItemStack item) {
        data = item.getDurability();
    }

    public ItemData(String state) {
        dataString = state; // FIXME: needs more safety checks
    }

    public ItemData(int dataVal, OdItemMeta itemMeta) {
        data = dataVal;
        this.itemMeta = itemMeta;
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public void setData(int d) {
        data = d;
    }

    @Override
    public boolean matches(Data d) {
        return data == d.getData();
    }

    @Override
    public String get(Enum<?> mat) {
        if (mat instanceof Material)
            return get((Material) mat);
        return "";
    }

    /**
     * Called to retrieve the current data value stored in this class, as a
     * string
     * 
     * @param mat
     * @return
     */
    @SuppressWarnings({ "incomplete-switch", "deprecation" })
    private String get(Material mat) {
        if (data == -1)
            return "THIS";
        if (mat.isBlock())
            return CommonMaterial.getBlockOrItemData(mat, data);
        switch (mat) {
        case COAL:
            return CoalType.getByData((byte) data).toString();
        case INK_SACK:
            DyeColor dyeColorData = DyeColor.getByDyeData((byte) (0xF - data));
            if (dyeColorData != null)
                return dyeColorData.toString();
            break;
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
        case SKULL_ITEM:
            return dataString;
        }
        if (data > 0)
            return Integer.toString(data);
        return "";
    }

    @Override
    // Items aren't blocks, so nothing to do here
    public void setOn(BlockState state) {
    }

    @Override
    // Items aren't entities, so nothing to do here
    public void setOn(Entity entity, Player witness) {
    }

    /**
     * Called to create a ItemData from a given config string.
     * 
     * @param mat
     * @param state
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("deprecation")
	public static Data parse(Material mat, String state)
            throws IllegalArgumentException {
        if (mat == null || state == null || state.isEmpty())
            return null;
        if (state.startsWith("RANGE") || state.matches("[0-9]+-[0-9]+"))
            return RangeData.parse(state);
        Integer data = 0;
        switch (mat) {
        case INK_SACK:
            DyeColor dye = DyeColor.valueOf(state.toUpperCase());
            if (dye != null)
                data = CommonMaterial.getDyeColor(dye);
            break;
        case COAL:
            CoalType coal = CoalType.valueOf(state.toUpperCase());
            if (coal != null)
                data = Integer.valueOf(coal.getData());
            break;
        case MOB_SPAWNER:
        case MONSTER_EGG: // spawn eggs
            return SpawnerData.parse(state);
        case LEATHER_BOOTS:
        case LEATHER_CHESTPLATE:
        case LEATHER_HELMET:
        case LEATHER_LEGGINGS:
            return parseItemMeta(state, ItemMetaType.LEATHER);
        case SKULL_ITEM:
            return parseItemMeta(state, ItemMetaType.SKULL);
        case WRITTEN_BOOK:
            return parseItemMeta(state, ItemMetaType.BOOK);
        case ENCHANTED_BOOK:
            return parseItemMeta(state, ItemMetaType.ENCHANTED_BOOK);
        case FIREWORK:
            return parseItemMeta(state, ItemMetaType.FIREWORK);
        default:
            if (mat.isBlock()) {
                data = CommonMaterial.parseBlockOrItemData(mat, state);
                break;
            }
            if (!state.isEmpty())
                throw new IllegalArgumentException("Illegal data for " + mat
                        + ": " + state);
        }
        if (state.equalsIgnoreCase("THIS"))
            return new ItemData(-1, state);

        return (data == null) ? null : new ItemData(data, state);
    }

    public enum ItemMetaType {
        LEATHER, SKULL, BOOK, ENCHANTED_BOOK, FIREWORK
    };

    private static Data parseItemMeta(String state, ItemMetaType metaType) {
        // FIXME: add a safety check here
        Log.logInfo("Parsing for possible metadata: " + state + " type="
                + metaType.toString(), Verbosity.HIGH);
        int dataVal = 0;

        if (!state.isEmpty() && !state.equals("0")) {
            String separator = "=";
            String split[] = state.split(separator);
            String subMinusDurability = "";

            for (String sub : split) {
                if (sub.matches("[0-9]+")) { // need to check numbers before any
                                             // .toLowerCase()
                    dataVal = Integer.valueOf(sub);
                } else {
                    subMinusDurability += sub + separator;
                }
            }
            return new ItemData(dataVal, OdItemMeta.parse(subMinusDurability
                    .substring(0, subMinusDurability.length() - 1), metaType));
        }

        return new ItemData(dataVal, state);
    }

    @Override
    public String toString() {
        // TODO: Should probably make sure this is not used, and always use the
        // get method instead
        Log.logWarning("ItemData.toString() was called! Is this right?",
                EXTREME);
        Log.stackTrace();
        return String.valueOf(data);
    }

    @Override
    public int hashCode() {
        return data;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public String getDataString() {
        return dataString;
    }

    @Override
    public Boolean getSheared() {
        // TODO Auto-generated method stub
        return null;
    }
}
