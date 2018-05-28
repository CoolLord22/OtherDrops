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

package com.gmail.zariust.otherdrops.options;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

public enum Adjacent {
    DOWN, UP;
    @SuppressWarnings("unused")
	private List<BlockFace>              blockfaceList;
    private static Map<String, Adjacent> nameLookup = new HashMap<String, Adjacent>();

    static {
        for (Adjacent storm : values())
            nameLookup.put(storm.name(), storm);
    }

    private Adjacent() {
    }

    public boolean matches(Block block, Material mat) {
        if (block == null)
            return false;

        boolean match = false;

        String faceName = "";
        if (this.name().equalsIgnoreCase("below")) {
            Location checkLoc = block.getLocation().clone().add(-1, -1, -1);
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    if (checkLoc.getBlock().getType() == mat) {
                        match = true;
                    }
                    checkLoc.add(1, 0, 0);
                }
                checkLoc.add(-3, 0, 1);
            }
        } else if (BlockFace.valueOf(faceName) != null) {
            if (block.getRelative(BlockFace.valueOf(faceName)).getType() == mat)
                match = true;
        }

        return match;
    }

    public static Adjacent parse(String storm) {
        return nameLookup.get(storm.toUpperCase());
    }

    public static Map<Adjacent, Boolean> parseFrom(ConfigurationNode node,
            Map<Adjacent, Boolean> def) {
        List<String> adjactentList = OtherDropsConfig.getMaybeList(node,
                "adjacent");
        if (adjactentList.isEmpty())
            return def;
        Map<Adjacent, Boolean> result = new HashMap<Adjacent, Boolean>();
        result.put(null, OtherDropsConfig.containsAll(adjactentList));
        for (String name : adjactentList) {
            String[] split = name.split("/");
            // BlockFace
            if (BlockFace.valueOf(split[0]) == null) {

            }

            Adjacent storm = parse(name);
            if (storm != null)
                result.put(storm, true);
            else if (name.startsWith("-")) {
                result.put(null, true);
                storm = parse(name.substring(1));
                if (storm == null) {
                    Log.logWarning("Invalid weather " + name + "; skipping...");
                    continue;
                }
                result.put(storm, false);
            }
        }
        if (result.isEmpty())
            return null;
        return result;
    }
}
