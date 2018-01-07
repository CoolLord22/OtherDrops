package com.gmail.zariust.otherdrops.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.AbstractTestingBase;
import com.gmail.zariust.otherdrops.BukkitMock;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.drop.DropType;
import com.gmail.zariust.otherdrops.drop.ItemDrop;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.BlockTarget;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.Target;

public class CustomDropTest extends AbstractTestingBase {
    
    public static World testWorld   = BukkitMock.getTestWorld_TestWorld();
    public static World secondWorld = BukkitMock.getTestWorld_SecondWorld();

    // Test target parsing
    @Test
    public void testParseTargets() {
        // Simple tests first:
        // Test all materials
        for (Material mat : Material.values()) {
            if (mat.isBlock() && !mat.toString().equals("WATER")) {
                String key = mat.toString();
                if (key.equals("SKULL"))
                    key = "SKULL_BLOCK"; // has been deliberately re-aliased

                Target newTarg = OtherDropsConfig.parseTarget(key);
                assertTrue("Error, target (" + key + ") is null.", newTarg != null);
                assertTrue("Error, target (" + key + ") is not a blocktarget.",
                        newTarg instanceof BlockTarget);
            }
        }

        // Test all entities
        for (EntityType type : EntityType.values()) {
            String key = type.toString();
            if (!type.isAlive())
                key = "ENTITY_" + key;

            if (key.equals("PLAYER"))
                continue; // PLAYER is not a creaturesubject

            Target newTarg = OtherDropsConfig.parseTarget(key);
            assertTrue("Error, target (" + key + ") is null.", newTarg != null);
            assertTrue("Error, target (" + key + ") is not a creaturesubject.",
                    newTarg instanceof CreatureSubject);
        }

        // Creature Targets. Test reasons:
        List<String> testValues = Arrays.asList("IRON_GoLEM", // testing without
                                                              // CREATURE_
                "CREaTURE_CAVE_SPIDER", // testing with CREATURE_
                "CAvESPIdER", // testing with no underscores or CREATURE_
                "CREEPER@POWERED", // testing data values
                "MOO_SH rOOM", // testing aliases
                "SKELETON@WITHER", // testing witherskeletons
                "WITHERSKELETON", // testing witherskeleton by alias
                "ZOMBIE@EIBMOZ" // testing creature with invalid data
        );
        Target newTarg = null;
        for (String key : testValues) {
            newTarg = OtherDropsConfig.parseTarget(key);
            assertTrue("Error, target (" + key + ") is null.", newTarg != null);
            assertTrue("Error, target (" + key + ") is not a creaturesubject.",
                    newTarg instanceof CreatureSubject);
        }
        // Test an invalid creature
        newTarg = OtherDropsConfig.parseTarget("INVALID_CREATURE");
        assertTrue("Error, target (INVALID_CREATURE) is not null.",
                newTarg == null);

        // Block Targets. Test reasons:
        // DIRT = just a standard test for parsing block targets
        testValues = Arrays.asList("CROPS@0-6", "DIrT", "LeAVES@3", "LEAVES@JUnGLE", "3",
                "3@5", "LEavES:3", "3:3", "35@ReD");
        newTarg = null;
        for (String key : testValues) {
            newTarg = OtherDropsConfig.parseTarget(key);
            assertTrue("Error, target (" + key + ") is null.", newTarg != null);
            assertTrue("Error, target (" + key + ") is not a block target.",
                    newTarg instanceof BlockTarget);
        }
        newTarg = OtherDropsConfig.parseTarget("INVALID_TARGET");
        assertTrue("Error, target (INVALID_TARGET) is not null.",
                newTarg == null);

        // Test reasons:
        // PLAYER
        testValues = Arrays.asList("PLAyER");
        Target playerTarg = null;
        for (String key : testValues) {
            newTarg = OtherDropsConfig.parseTarget(key);
            assertTrue("Error, target (" + key + ") is null.", newTarg != null);
            assertTrue("Error, target (" + key + ") is not a playersubject.",
                    newTarg instanceof PlayerSubject);
        }
    }

    // Test drop type parsing

    @Test
    public void testParseDropType() {
        createMockEnchantments();
        // Itemdrops. Test reasons:
        // FISH = alias for raw_fish
        // EGG = can be considered an entity or item, need to ensure it's an item
        List<String> testValues =
                Arrays.asList("STONE_SwoRD", "FIsH", "EGG",
                        "DIAmoND_SWORD@!DAMagE_ALL#1-5~Lorename");
        DropType dropType = null;
        for (String key : testValues) {
            dropType = DropType.parse(key, "");
            assertTrue("Error, target (" + key + ") is null.", dropType != null);
            assertTrue("Error, target (" + key + ") is not an itemdrop.", dropType instanceof ItemDrop);
        }
        // Test an invalid item:
        dropType = DropType.parse("INVALID_ITEM", "");
        assertTrue("Error, target (INVALID_ITEM) is not null.", dropType == null);

        // Lorename tests
        testValues = Arrays.asList("STONE_SWORD~&aLore name",
                "FISH@~&aLore name", "EGG@!~&aLore name",
                "DIAMONDSWORD@1-60!DAMAGE_ALL~&aLore name");
        dropType = null;
        for (String key : testValues) {
            dropType = DropType.parse(key, "");
            assertTrue("Error, target (" + key + ") is null.", dropType != null);
            assertTrue("Error, target (" + key + ") is not an itemdrop.", dropType instanceof ItemDrop);
            assertTrue("Error, target (" + key + "), lorename (" + dropType.getDisplayName() + ") is not '&aLore name'.",
                    (dropType.getDisplayName().equals("&aLore name") || dropType.getDisplayName().equals("§aLore name")));
        }
    }

    /**
     * For some reason Enchantments are not being created when testing is run,
     * therefore we need to create our own for testing.
     */
    private void createMockEnchantments() {
        Enchantment blah = new CustomEnchantment(16);
        try {
            Enchantment.registerEnchantment(blah);
        } catch (Exception ex) {

        }
    }

    // Test world conditions
    @Test
    public void testIsWorld() {
        World thisWorld = BukkitMock.getTestWorld_TestWorld(); // named TestWorld
        World notThisWorld = BukkitMock.getTestWorld_SecondWorld(); // named SecondWorld

        CustomDrop customDrop = new SimpleDrop(new BlockTarget(), Trigger.BREAK);
        Map<World, Boolean> worlds = new HashMap<World, Boolean>();

        // Test with a true match
        worlds.put(null, false); // ALL = false
        worlds.put(thisWorld, true);

        customDrop.setWorlds(worlds);
        assertTrue(customDrop.isWorld(thisWorld));

        // Test with a negative condition
        worlds.put(thisWorld, false); // -TestWorld
        worlds.put(null, true); // ALL = true (this gets set true for negative
                                // conditions)

        customDrop.setWorlds(worlds);
        assertTrue(customDrop.isWorld(notThisWorld));

        // Test with a false match
        worlds.put(null, false); // ALL = false
        worlds.put(notThisWorld, true); // [SecondWorld]
        customDrop.setWorlds(worlds);

        assertFalse(customDrop.isWorld(thisWorld)); // should not match
                                                    // "TestWorld"
    }

    @Test
    public void testIsRegion() {
        CustomDrop customDrop = new SimpleDrop(new BlockTarget(), Trigger.BREAK);

        // needs verbosity
        OtherDropsConfig.setVerbosity(Verbosity.EXTREME);

        Map<String, Boolean> areas = new HashMap<String, Boolean>();
        areas.put("testinside", true);
        areas.put("testinside1", true);
        areas.put("testinside2", true);
        // areas.put(null, false); // means not "all" or "any" condition
        customDrop.setRegions(areas);

        Set<String> inRegions = new HashSet<String>();
        inRegions.add("realregion");
        inRegions.add("realregion1");
        inRegions.add("testinside2");

        // test a position match
        assertTrue(customDrop.isRegion(inRegions));

        // test a negative match - this should fail as we are inside the region
        areas.put("-testinside2", false);
        // areas.put(null, true); // set true on negative conditions
        customDrop.setRegions(areas);
        assertFalse(customDrop.isRegion(inRegions));
    }


}
