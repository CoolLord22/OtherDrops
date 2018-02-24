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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.SandstoneType;
import org.bukkit.TreeSpecies;
import org.bukkit.material.Step;

import com.gmail.zariust.otherdrops.Log;

public final class CommonMaterial {
    // Aliases definitions
    private static final Map<String, String> ALIASES;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("GLASS_PANE", "THIN_GLASS");
        aMap.put("WOODEN_SPADE", "WOOD_SPADE");
        aMap.put("WOODEN_AXE", "WOOD_AXE");
        aMap.put("WOODEN_HOE", "WOOD_HOE");
        aMap.put("WOODEN_PICKAXE", "WOOD_PICKAXE");
        aMap.put("WOODEN_SWORD", "WOOD_SWORD");
        aMap.put("GOLDEN_SPADE", "GOLD_SPADE");
        aMap.put("GOLDEN_AXE", "GOLD_AXE");
        aMap.put("GOLDEN_HOE", "GOLD_HOE");
        aMap.put("GOLDEN_PICKAXE", "GOLD_PICKAXE");
        aMap.put("GOLDEN_SWORD", "GOLD_SWORD");
        aMap.put("LEATHER_HELM", "LEATHER_HELMET");
        aMap.put("IRON_HELM", "IRON_HELMET");
        aMap.put("GOLD_HELM", "GOLD_HELMET");
        aMap.put("DIAMOND_HELM", "DIAMOND_HELMET");
        aMap.put("WOODEN_PLATE", "WOOD_PLATE");
        aMap.put("PLANK", "WOOD");
        aMap.put("WOODEN_PLANK", "WOOD");
        aMap.put("WOODEN_DOOR_ITEM", "WOOD_DOOR");
        aMap.put("WOOD_DOOR_ITEM", "WOOD_DOOR");
        aMap.put("WOOD_DOOR", "WOODEN_DOOR");
        aMap.put("STONE_PRESSUREPLATE", "STONE_PLATE");
        aMap.put("WOOD_PRESSUREPLATE", "WOOD_PLATE");
        aMap.put("WOODEN_PRESSUREPLATE", "WOOD_PLATE");
        aMap.put("HANDS", "AIR");
        aMap.put("HAND", "AIR");
        aMap.put("NOTHING", "AIR");

        aMap.put("DANDELION", "YELLOW_FLOWER");
        aMap.put("ROSE", "RED_ROSE");
        aMap.put("RED_FLOWER", "RED_ROSE");
        aMap.put("flower", "YELLOW_FLOWER"); // Yellow flower is described as
                                             // "flower" in game

        aMap.put("MOSS_STONE", "MOSSY_COBBLESTONE");
        aMap.put("MOSSY_COBBLE", "MOSSY_COBBLESTONE");
        aMap.put("GUNPOWDER", "SULPHUR");
        aMap.put("SULFUR", "SULPHUR");
        aMap.put("TRAPDOOR", "TRAP_DOOR");
        aMap.put("SLAB", "STEP");
        aMap.put("DOUBLE_SLAB", "DOUBLE_STEP");
        aMap.put("CRAFTING_TABLE", "WORKBENCH");
        aMap.put("FARMLAND", "SOIL");
        aMap.put("SEED", "SEEDS");
        aMap.put("WHEAT_SEEDS", "SEEDS");
        aMap.put("VINES", "VINE");
        aMap.put("STONE_BRICK", "SMOOTH_BRICK");
        aMap.put("DYE", "INK_SACK");
        aMap.put("TRACKS", "RAILS");
        aMap.put("TRACK", "RAILS");
        aMap.put("RAIL", "RAILS");
        aMap.put("ZOMBIE_FLESH", "ROTTEN_FLESH");
        aMap.put("SPAWN_EGG", "MONSTER_EGG");
        aMap.put("SPAWNEGG", "MONSTER_EGG");
        aMap.put("SPAWNER_EGG", "MONSTER_EGG");
        aMap.put("SPAWNEREGG", "MONSTER_EGG");
        aMap.put("GLISTERING_MELON", "SPECKLED_MELON");
        aMap.put("melonslice", "melon");
        aMap.put("uncookedbeef", "rawbeef");
        aMap.put("steak", "cookedbeef");
        aMap.put("netherwartseeds", "netherstalk");
        aMap.put("melonseed", "melonseeds");
        aMap.put("pumpkinseed", "pumpkinseeds");
        aMap.put("redstonerepeater", "diode");
        aMap.put("beditem", "bed");
        aMap.put("grilledfish", "cookedfish");
        aMap.put("fish", "rawfish");
        aMap.put("clock", "watch");
        aMap.put("cookedpork", "grilledpork");
        aMap.put("cookedporkchop", "grilledpork");
        aMap.put("grilledporkchop", "grilledpork");
        aMap.put("rabbit_meat", "rabbit");
        aMap.put("raw_rabbit", "rabbit");

        aMap.put("goldpants", "goldleggings");
        aMap.put("goldvest", "goldchestplate");
        aMap.put("goldchest", "goldchestplate");
        aMap.put("chainmail", "mail");
        aMap.put("wheatseeds", "seeds");
        aMap.put("mushroomstew", "mushroomsoup");
        aMap.put("flintandtinder", "flintandsteel");
        aMap.put("ironbars", "ironfence");
        aMap.put("glowstoneblock", "glowstone");
        aMap.put("netherrock", "netherrack");
        aMap.put("woodfence", "fence");
        aMap.put("woodenfence", "fence");
        aMap.put("cacti", "cactus");
        aMap.put("cobweb", "web");
        aMap.put("poweredtracks", "powerrail");
        aMap.put("detectortracks", "detectorrail");

        aMap.put("leathercap", "leatherhelmet");
        aMap.put("leathertunic", "leatherchestplate");
        aMap.put("leathervest", "leatherchestplate");
        aMap.put("leatherchest", "leatherchestplate");
        aMap.put("leatherpants", "leatherleggings");

        aMap.put("chainmailvest", "chainmailchestplate");
        aMap.put("chainmailchest", "chainmailchestplate");
        aMap.put("chainmailpants", "chainmailleggings");

        aMap.put("ironvest", "ironchestplate");
        aMap.put("ironchest", "ironchestplate");
        aMap.put("ironpants", "ironleggings");

        aMap.put("diamondvest", "diamondchestplate");
        aMap.put("diamondchest", "diamondchestplate");
        aMap.put("diamondpants", "diamondleggings");

        aMap.put("diamondshovel", "diamondspade");
        aMap.put("goldshovel", "goldspade");
        aMap.put("ironshovel", "ironspade");
        aMap.put("stoneshovel", "stonespade");
        aMap.put("woodshovel", "woodspade");

        aMap.put("goldapple", "goldenapple");
        aMap.put("redapple", "apple");
        aMap.put("sticks", "stick");

        aMap.put("grassblock", "grass");
        aMap.put("tallgrass", "longgrass");
        aMap.put("wildgrass", "longgrass");

        aMap.put("saplings", "sapling");
        aMap.put("lapislazuliore", "lapisore");
        aMap.put("lapislazuliblock", "lapisblock");
        aMap.put("stickypiston", "pistonstickybase");
        aMap.put("piston", "pistonbase");
        aMap.put("bricks", "brick");
        aMap.put("hiddensilverfish", "monstereggs");
        aMap.put("mycelium", "mycel");
        aMap.put("lilypad", "waterlily");
        aMap.put("netherbrickfence", "netherfence");

        aMap.put("stonebricks", "smoothbrick");
        aMap.put("stonebrickstairs", "smoothstairs");

        aMap.put("endportal", "enderportal");
        aMap.put("endportalframe", "enderportalframe");
        aMap.put("endstone", "enderstone");

        aMap.put("rawporkchop", "pork");
        aMap.put("chickenegg", "egg");
        aMap.put("netherwart", "netherwarts");

        aMap.put("cauldron_block", "cauldron");
        aMap.put("brewing_stand_block", "brewing_stand");

        aMap.put("bucket of milk", "milk bucket");

        aMap.put("inksac", "inksack");

        aMap.put("firecharge", "fireball");

        aMap.put("skull", "skull_item");
        aMap.put("skullblock", "skull");

        // Records
        aMap.put("13disc", "goldrecord");
        aMap.put("catdisc", "greenrecord");
        aMap.put("blocksdisc", "record3");
        aMap.put("chirpdisc", "record4");
        aMap.put("fardisc", "record5");
        aMap.put("malldisc", "record6");
        aMap.put("mellohidisc", "record7");
        aMap.put("staldisc", "record8");
        aMap.put("straddisc", "record9");
        aMap.put("warddisc", "record10");
        aMap.put("11disc", "record11");
        aMap.put("waitdisc", "record12");

        aMap.put("commandblock", "command");

        // 1.6.1 mats
        aMap.put("lead", "leash");
        aMap.put("haybale", "hay_block");
        aMap.put("ironhorsearmour", "ironbarding");
        aMap.put("goldhorsearmour", "goldbarding");
        aMap.put("diamondhorsearmour", "diamondbarding");
        aMap.put("ironhorsearmor", "ironbarding");
        aMap.put("goldhorsearmor", "goldbarding");
        aMap.put("diamondhorsearmor", "diamondbarding");
        aMap.put("blockofcoal", "coalblock");
        aMap.put("hardenedclay", "hardclay");

        aMap.put("leaves_1", "leaves");

        ALIASES = Collections.unmodifiableMap(aMap);
    }

    public static Material matchMaterial(String mat) {
        // Aliases defined here override those in Material; the only example
        // here is WOODEN_DOOR
        // You can remove it if you prefer not to break the occasional config
        // file.
        // (I doubt many people assign drops to wooden doors, though, and
        // including the BLOCK makes it less confusing.)

        // remove any trailing data (eg. from tool [item]/[quantity])
        String[] split = mat.split("/");
        mat = split[0];

        if (mat.matches("[0-9]+")) {
            return Material.getMaterial(Integer.valueOf(mat));
        }
        // CommonMaterial material = enumValue(CommonMaterial.class, mat);
        mat = mat.toLowerCase().replaceAll("[\\s-_]", "");

        for (String loopAlias : ALIASES.keySet()) {
            if (mat.equalsIgnoreCase(loopAlias.toLowerCase().replaceAll(
                    "[\\s-_]", "")))
                mat = ALIASES.get(loopAlias).toLowerCase()
                        .replaceAll("[\\s-_]", "");
        }

        Material matchedMat = null;
        for (Material loopMat : Material.values()) {
            if (mat.equalsIgnoreCase(loopMat.name().toLowerCase()
                    .replaceAll("[\\s-_]", "")))
                matchedMat = loopMat;
        }

        if (matchedMat == null) {
            Material defaultMat = Material.getMaterial(mat);
            if (defaultMat == null) {
                if (!(mat.equalsIgnoreCase("default"))) {
                    Log.logInfo("Error: unknown material (" + mat + ").",
                            Verbosity.LOW); // low verbosity as only appears at
                                            // config loading and admin needs to
                                            // know materials not working
                }
            }
        }
        return matchedMat;
    }

    // Colors
    public static int getWoolColor(DyeColor color) {
        return color.getWoolData();
    }

    public static int getDyeColor(DyeColor color) {
        return color.getDyeData();
    }

    @SuppressWarnings("incomplete-switch")
    public static Integer parseBlockOrItemData(Material mat, String state)
            throws IllegalArgumentException {
        Log.logInfo("Checking block data for " + mat.toString() + "@" + state,
                Verbosity.HIGH);
        state = state.toUpperCase();
        if (state.equalsIgnoreCase("this"))
            return -1;
        switch (mat) {
        case LOG:
        case LEAVES:
        case SAPLING:
        case WOOD:
        case WOOD_STEP:
        case WOOD_DOUBLE_STEP:
            // if (state.equals("JUNGLE")) return 3;
            TreeSpecies species = TreeSpecies.valueOf(state);
            if (species != null)
                return (int) species.getData();
            break;
        case WOOL:
        case STAINED_GLASS:
        case STAINED_GLASS_PANE:
        case STAINED_CLAY:
            if (state.contains("!")) {
                for (String statePart : state.split("!")) {
                    try {
                    if (DyeColor.valueOf(statePart) != null) {
                        state = statePart;
                    }
                    } catch (Exception exception) {}
                }
            }
            if (state.contains("!")) {
                throw new IllegalArgumentException("Illegal block colour: "
                        + state);
            }
            DyeColor wool = DyeColor.valueOf(state);
            if (wool != null)
                return getWoolColor(wool);
            break;
        case SMOOTH_BRICK:
            String upper = state.toUpperCase();
            if (state.equalsIgnoreCase("NORMAL"))
                return 0;
            if (state.equalsIgnoreCase("MOSSY"))
                return 1;
            if (state.equalsIgnoreCase("CRACKED"))
                return 2;
            if (upper.matches("(CIRCLE|CHISELED)"))
                return 3;
            Material brick = Material.valueOf(state);
            if (brick == null)
                throw new IllegalArgumentException("Unknown material " + state);
            switch (brick) {
            case STONE:
                return 0;
            case COBBLESTONE:
                return 2;
            case MOSSY_COBBLESTONE:
                return 5;
            default:
                throw new IllegalArgumentException("Illegal step material "
                        + state);
            }
        case COBBLE_WALL:
            String upperState = state.toUpperCase();
            if (upperState.matches("COBBLE(STONE)*"))
                return 0;
            if (upperState.matches("MOSS(Y)*(STONE)*"))
                return 1;
            break;
        case DOUBLE_STEP:
        case STEP:
            Material step = Material.valueOf(state);
            if (step == null)
                throw new IllegalArgumentException("Unknown material " + state);
            switch (step) {
            case STONE:
                return 0;
            case SANDSTONE:
                return 1;
            case WOOD:
                return 2;
            case COBBLESTONE:
                return 3;
            case BRICK:
                return 4;
            case SMOOTH_BRICK:
                return 5;
            case NETHER_BRICK:
                return 6;
            default:
                throw new IllegalArgumentException("Illegal step material "
                        + state);
            }
        case LONG_GRASS:
            GrassSpecies grass = GrassSpecies.valueOf(state);
            if (grass != null)
                return (int) grass.getData();
            break;
        case SKULL:
            String upperState2 = state.toUpperCase();
            if (upperState2.matches("SKELETON"))
                return 0;
            if (upperState2.matches("(WITHER|WITHERSKELETON)"))
                return 1;
            if (upperState2.matches("ZOMBIE"))
                return 2;
            if (upperState2.matches("(HUMAN|PLAYER)"))
                return 3;
            if (upperState2.matches("CREEPER"))
                return 4;
            break;
        case SANDSTONE:
            SandstoneType ssType = SandstoneType.valueOf(state);
            if (ssType != null)
                return (int) ssType.getData();
            break;
        }
        return null;
    }

    @SuppressWarnings("incomplete-switch")
    public static String getBlockOrItemData(Material mat, int data) {
        try {
            switch (mat) {
            case LOG:
            case LEAVES:
            case SAPLING:
            case WOOD:
            case WOOD_STEP:
            case WOOD_DOUBLE_STEP:
                // if ((byte)((0x3) & data) == 3) return "JUNGLE";
                return TreeSpecies.getByData((byte) ((0x3) & data)).toString(); // (0x3)
                                                                                // &
                                                                                // data
                                                                                // to
                                                                                // remove
                                                                                // leaf
                                                                                // decay
                                                                                // flag
            case WOOL:
            case STAINED_GLASS:
            case STAINED_GLASS_PANE:
            case STAINED_CLAY:
                return DyeColor.getByDyeData((byte) data).toString();
            case SMOOTH_BRICK:
                switch (data) {
                case 0:
                    return "NORMAL";
                case 1:
                    return "MOSSY";
                case 2:
                    return "CRACKED";
                case 3:
                    return "CHISELED";
                }
            case COBBLE_WALL:
                switch (data) {
                case 0:
                    return "COBBLESTONE";
                case 1:
                    return "MOSSSTONE";
                }
            case DOUBLE_STEP:
            case STEP:
                Step step = new Step(mat, (byte) data);
                return step.getMaterial().toString();
            case LONG_GRASS:
                return GrassSpecies.getByData((byte) data).toString();
            case SKULL:
                switch (data) {
                case 0:
                    return "SKELETON";
                case 1:
                    return "WITHERSKELETON";
                case 2:
                    return "ZOMBIE";
                case 3:
                    return "HUMAN";
                case 4:
                    return "CREEPER";
                }
            case SANDSTONE:
                return SandstoneType.getByData((byte) data).toString();
            }

            if (data > 0)
                return Integer.toString(data);
            return "";
        } catch (NullPointerException ex) {
            Log.logWarning(
                    "CommonMaterial.getBlockOrItemData() failed. Material: "
                            + mat.toString() + ", Data: " + data,
                    Verbosity.NORMAL);
            return "";
        }
    }

    public static String substituteAlias(String drop) {
        Map<String, String> a2Map = new HashMap<String, String>();

        // note: aliases (on left) need to be uppercase with no spaces, dashes or underscores
        a2Map.put("ANYSHOVEL", "ANY_SPADE");
        a2Map.put("LAPISLAZULI", "DYE@BLUE");
        
        // TODO: DISABLED until issues it causes with LAPIS_ORE are fixed
        //a2Map.put("LAPIS([^A-Z]?)", "DYE@BLUE$1"); // only lapis as a singular word, otherwise lapis_ore becomes lapisdye@blueore
        
        a2Map.put("BONEMEAL", "DYE@WHITE");
        a2Map.put("COCOABEANS", "DYE@BROWN");

        a2Map.put("SKELETONHEAD", "SKULL_ITEM@0");
        a2Map.put("SKELETONSKULL", "SKULL_ITEM@0");
        a2Map.put("ZOMBIEHEAD", "SKULL_ITEM@1");
        a2Map.put("WITHERHEAD", "SKULL_ITEM@2");
        a2Map.put("PLAYERHEAD", "SKULL_ITEM@3");
        a2Map.put("CREEPERHEAD", "SKULL_ITEM@4");
        a2Map.put("HEAD", "SKULL_ITEM@3");

        a2Map.put("WITHERSKELETON", "SKELETON@WITHER");

        String tmpDrop = drop.toUpperCase().replaceAll("[ _-]", "");
        for (String alias : a2Map.keySet()) {
            if (tmpDrop.toUpperCase().replaceAll("[ _-]", "")
                    .matches(alias + ".*")) {
                String[] nameSplit = tmpDrop.split("~", 2);
                tmpDrop = nameSplit[0].replaceAll("@", "!");
                String[] nameSplit2 = tmpDrop.split("!", 2);
                tmpDrop = nameSplit2[0];
                tmpDrop = tmpDrop.toUpperCase().replaceAll("[ _-]", "").replaceAll("(?i)" + alias, a2Map.get(alias));
                if (nameSplit.length > 1) tmpDrop += "~"+nameSplit[1];
                if (nameSplit2.length > 1) tmpDrop += "!"+nameSplit2[1];
                return tmpDrop; // we only want to replace the first found result,
                             // so return
            }
        }

        return drop;
    }

    public static boolean fuzzyMatchString(String one, String two) {

        if (one.toLowerCase().replaceAll("[\\s-_]", "")
                .equals(two.toLowerCase().replaceAll("[\\s-_]", "")))
            return true;
        return false;
    }
}
