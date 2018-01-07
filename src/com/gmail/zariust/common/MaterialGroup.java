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

package com.gmail.zariust.common;

import static org.bukkit.Material.ARROW;
import static org.bukkit.Material.BOAT;
import static org.bukkit.Material.BOW;
import static org.bukkit.Material.BUCKET;
import static org.bukkit.Material.BURNING_FURNACE;
import static org.bukkit.Material.CHAINMAIL_BOOTS;
import static org.bukkit.Material.CHAINMAIL_CHESTPLATE;
import static org.bukkit.Material.CHAINMAIL_HELMET;
import static org.bukkit.Material.CHAINMAIL_LEGGINGS;
import static org.bukkit.Material.DETECTOR_RAIL;
import static org.bukkit.Material.DIAMOND_AXE;
import static org.bukkit.Material.DIAMOND_BOOTS;
import static org.bukkit.Material.DIAMOND_CHESTPLATE;
import static org.bukkit.Material.DIAMOND_HELMET;
import static org.bukkit.Material.DIAMOND_HOE;
import static org.bukkit.Material.DIAMOND_LEGGINGS;
import static org.bukkit.Material.DIAMOND_PICKAXE;
import static org.bukkit.Material.DIAMOND_SPADE;
import static org.bukkit.Material.DIAMOND_SWORD;
import static org.bukkit.Material.DIODE;
import static org.bukkit.Material.DIODE_BLOCK_OFF;
import static org.bukkit.Material.DIODE_BLOCK_ON;
import static org.bukkit.Material.EGG;
import static org.bukkit.Material.FIRE;
import static org.bukkit.Material.FISHING_ROD;
import static org.bukkit.Material.FLINT_AND_STEEL;
import static org.bukkit.Material.FURNACE;
import static org.bukkit.Material.GLOWING_REDSTONE_ORE;
import static org.bukkit.Material.GOLD_AXE;
import static org.bukkit.Material.GOLD_BOOTS;
import static org.bukkit.Material.GOLD_CHESTPLATE;
import static org.bukkit.Material.GOLD_HELMET;
import static org.bukkit.Material.GOLD_HOE;
import static org.bukkit.Material.GOLD_LEGGINGS;
import static org.bukkit.Material.GOLD_PICKAXE;
import static org.bukkit.Material.GOLD_RECORD;
import static org.bukkit.Material.GOLD_SPADE;
import static org.bukkit.Material.GOLD_SWORD;
import static org.bukkit.Material.GREEN_RECORD;
import static org.bukkit.Material.IRON_AXE;
import static org.bukkit.Material.IRON_BOOTS;
import static org.bukkit.Material.IRON_CHESTPLATE;
import static org.bukkit.Material.IRON_HELMET;
import static org.bukkit.Material.IRON_HOE;
import static org.bukkit.Material.IRON_LEGGINGS;
import static org.bukkit.Material.IRON_PICKAXE;
import static org.bukkit.Material.IRON_SPADE;
import static org.bukkit.Material.IRON_SWORD;
import static org.bukkit.Material.LAVA_BUCKET;
import static org.bukkit.Material.LEATHER_BOOTS;
import static org.bukkit.Material.LEATHER_CHESTPLATE;
import static org.bukkit.Material.LEATHER_HELMET;
import static org.bukkit.Material.LEATHER_LEGGINGS;
import static org.bukkit.Material.LEAVES_2;
import static org.bukkit.Material.MILK_BUCKET;
import static org.bukkit.Material.MINECART;
import static org.bukkit.Material.PISTON_BASE;
import static org.bukkit.Material.PISTON_EXTENSION;
import static org.bukkit.Material.PISTON_MOVING_PIECE;
import static org.bukkit.Material.PISTON_STICKY_BASE;
import static org.bukkit.Material.POWERED_MINECART;
import static org.bukkit.Material.POWERED_RAIL;
import static org.bukkit.Material.RAILS;
import static org.bukkit.Material.RECORD_10;
import static org.bukkit.Material.RECORD_11;
import static org.bukkit.Material.RECORD_12;
import static org.bukkit.Material.RECORD_3;
import static org.bukkit.Material.RECORD_4;
import static org.bukkit.Material.RECORD_5;
import static org.bukkit.Material.RECORD_6;
import static org.bukkit.Material.RECORD_7;
import static org.bukkit.Material.RECORD_8;
import static org.bukkit.Material.RECORD_9;
import static org.bukkit.Material.REDSTONE_ORE;
import static org.bukkit.Material.REDSTONE_TORCH_OFF;
import static org.bukkit.Material.REDSTONE_TORCH_ON;
import static org.bukkit.Material.SADDLE;
import static org.bukkit.Material.SIGN;
import static org.bukkit.Material.SIGN_POST;
import static org.bukkit.Material.SNOW_BALL;
import static org.bukkit.Material.STONE_AXE;
import static org.bukkit.Material.STONE_HOE;
import static org.bukkit.Material.STONE_PICKAXE;
import static org.bukkit.Material.STONE_SPADE;
import static org.bukkit.Material.STONE_SWORD;
import static org.bukkit.Material.STORAGE_MINECART;
import static org.bukkit.Material.WALL_SIGN;
import static org.bukkit.Material.WATER_BUCKET;
import static org.bukkit.Material.WOOD_AXE;
import static org.bukkit.Material.WOOD_HOE;
import static org.bukkit.Material.WOOD_PICKAXE;
import static org.bukkit.Material.WOOD_SPADE;
import static org.bukkit.Material.WOOD_SWORD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import com.gmail.zariust.otherdrops.OtherDrops;

public enum MaterialGroup {
    // Blocks
    ANY_FURNACE(FURNACE, BURNING_FURNACE), ANY_SIGN(SIGN_POST, WALL_SIGN, SIGN), ANY_REDSTONE_ORE(
            REDSTONE_ORE, GLOWING_REDSTONE_ORE), ANY_REDSTONE_TORCH(
            REDSTONE_TORCH_ON, REDSTONE_TORCH_OFF), ANY_DIODE(DIODE_BLOCK_ON,
            DIODE_BLOCK_OFF, DIODE), ANY_PISTON(PISTON_BASE,
            PISTON_STICKY_BASE, PISTON_EXTENSION, PISTON_MOVING_PIECE),
    ANY_LEAVES(org.bukkit.Material.LEAVES, LEAVES_2),
    // Records
    ANY_RECORD(GOLD_RECORD, GREEN_RECORD, RECORD_3, RECORD_4, RECORD_5,
            RECORD_6, RECORD_7, RECORD_8, RECORD_9, RECORD_10, RECORD_11,
            RECORD_12),
    // Tools
    ANY_SPADE(WOOD_SPADE, STONE_SPADE, GOLD_SPADE, IRON_SPADE, DIAMOND_SPADE), ANY_AXE(
            WOOD_AXE, STONE_AXE, GOLD_AXE, IRON_AXE, DIAMOND_AXE), ANY_HOE(
            WOOD_HOE, STONE_HOE, GOLD_HOE, IRON_HOE, DIAMOND_HOE), ANY_PICKAXE(
            WOOD_PICKAXE, STONE_PICKAXE, GOLD_PICKAXE, IRON_PICKAXE,
            DIAMOND_PICKAXE), ANY_SWORD(WOOD_SWORD, STONE_SWORD, GOLD_SWORD,
            IRON_SWORD, DIAMOND_SWORD), ANY_BUCKET(BUCKET, LAVA_BUCKET,
            WATER_BUCKET, MILK_BUCKET),
    // Armour
    ANY_HELMET(LEATHER_HELMET, CHAINMAIL_HELMET, GOLD_HELMET, IRON_HELMET,
            DIAMOND_HELMET), ANY_CHESTPLATE(LEATHER_CHESTPLATE,
            CHAINMAIL_CHESTPLATE, GOLD_CHESTPLATE, IRON_CHESTPLATE,
            DIAMOND_CHESTPLATE), ANY_LEGGINGS(LEATHER_LEGGINGS,
            CHAINMAIL_LEGGINGS, GOLD_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS), ANY_BOOTS(
            LEATHER_BOOTS, CHAINMAIL_BOOTS, GOLD_BOOTS, IRON_BOOTS,
            DIAMOND_BOOTS),
    // Minecarts
    ANY_MINECART(MINECART, POWERED_MINECART, STORAGE_MINECART), ANY_RAIL(RAILS,
            POWERED_RAIL, DETECTOR_RAIL),
    // Wildcards
    ANY_VEHICLE(Arrays.asList(BOAT), ANY_MINECART), ANY_TOOL(Arrays.asList(
            FLINT_AND_STEEL, BOW, FISHING_ROD, SADDLE), ANY_SPADE, ANY_AXE,
            ANY_HOE, ANY_PICKAXE, ANY_SWORD, ANY_BUCKET), ANY_WEAPON(Arrays
            .asList(BOW, ARROW), ANY_SWORD), ANY_ARMOR(ANY_HELMET,
            ANY_CHESTPLATE, ANY_LEGGINGS, ANY_BOOTS), ANY_ARMOUR(ANY_ARMOR), ANY_PROJECTILE(
            FIRE, SNOW_BALL, EGG, ARROW, FISHING_ROD),
    // Add any new ones before this line
    ANY_ITEM, ANY_BLOCK, ANY_OBJECT;
    private static Map<String, MaterialGroup> lookup = new HashMap<String, MaterialGroup>();
    private ArrayList<Material>               mat;

    static {
        for (Material mat : Material.values()) {
            ANY_OBJECT.mat.add(mat);
            if (mat.isBlock())
                ANY_BLOCK.mat.add(mat);
            else
                ANY_ITEM.mat.add(mat);
        }
        for (MaterialGroup group : values())
            lookup.put(group.name(), group);
    }

    private void add(List<Material> materials) {
        mat.addAll(materials);
    }

    private MaterialGroup(Material... materials) {
        this();
        add(Arrays.asList(materials));
    }

    private MaterialGroup(MaterialGroup... merge) {
        this();
        for (MaterialGroup group : merge)
            add(group.mat);
    }

    private MaterialGroup(List<Material> materials, MaterialGroup... merge) {
        this(merge);
        add(materials);
    }

    private MaterialGroup() {
        mat = new ArrayList<Material>();
    }

    @SuppressWarnings("unchecked")
    public List<Material> materials() {
        return (List<Material>) mat.clone();
    }

    public static MaterialGroup get(String string) {
        return lookup.get(string.toUpperCase());
    }

    public static Set<String> all() {
        return lookup.keySet();
    }

    public static boolean isValid(String string) {
        return lookup.containsKey(string);
    }

    public boolean isBlock() {
        for (Material obj : mat)
            if (obj.isBlock())
                return true;
        return false;
    }

    public boolean isItem() {
        for (Material obj : mat)
            if (!obj.isBlock())
                return true;
        return false;
    }

    public boolean contains(Material material) {
        return mat.contains(material);
    }

    public Material getOneRandom() {
        double select = OtherDrops.rng.nextDouble() * mat.size(), cumul = 0;
        for (Material singleMat : mat) {
            cumul++;
            if (select <= cumul) {
                return singleMat;
            }
        }
        return null;
    }
}
