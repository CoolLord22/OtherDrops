package com.gmail.zariust.otherdrops.drop;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

public class DropTypeTest {

    @Test
    public void testDropShortcutParsing() {
        // needs verbosity
        OtherDropsConfig.setVerbosity(Verbosity.EXTREME);

        testSplit("DIRT/2/50%", "2", "50");
        testSplit("DIRT/2/50.5", "2", "50.5");
        testSplit("DIRT/50.5%/2", "2", "50.5");
        testSplit("DIRT/50%", "", "50");
        testSplit("DIRT/2", "2", "");

        testSplit("DIRT/2-5", "2-5", "");
        testSplit("DIRT/99.3%/2-5", "2-5", "99.3");
    }

    private void testSplit(String testVal, String amountShouldBe,
            String chanceShouldBe) {
        System.out.println("Test split: '" + testVal + "'");
        String[] split = DropType.split(testVal);
        String name = split[0], amount = split[1], chance = split[2];
        assertTrue("Error, chance (" + chance + ") is not '" + chanceShouldBe
                + "'.", chance.equalsIgnoreCase(chanceShouldBe));
        assertTrue("Error, amount (" + amount + ") is not '" + amountShouldBe
                + "'.", amount.equalsIgnoreCase(amountShouldBe));

    }

    @Test
    public void testParse() {
        // needs verbosity
        OtherDropsConfig.setVerbosity(Verbosity.EXTREME);

        testParse("DYE@BLUE/1/50%", "INK_SACK@BLUE", "", "");
        testParse("diamond_sword@!~MyName", "DIAMOND_SWORD", "", "MyName");
        testParse("huge-MUSHROOM1", "HUGE_MUSHROOM_1", "", "");

        testParseMob("zombie@baby!!VILLAGER!!50h",
                "CREATURE_ZOMBIE@!!VILLAGER!!BABY%50.0h", "", "");
        testParseMob("zombie@baby!!VILLAGER!!50H",
                "CREATURE_ZOMBIE@!!VILLAGER!!BABY%50.0h", "", "");
        testParseMob("zombie@baby!!VILLAGER!!50Hp",
                "CREATURE_ZOMBIE@!!VILLAGER!!BABY%50.0h", "", "");
        testParseMob("zombie@baby!!VILLAGER!!50HP",
                "CREATURE_ZOMBIE@!!VILLAGER!!BABY%50.0h", "", "");
    }

    private void testParse(String testVal, String name, String data,
            String lorename) {
        System.out.println("Test parse: '" + testVal + "'");
        DropType split = DropType.parse(testVal, "");
        assertTrue("Error, mat (" + split.getName() + ") is not '" + name
                + "'.", split.getName().equalsIgnoreCase(name));
        if (!lorename.isEmpty())
        assertTrue("Error, lorename (" + split.displayName + ") is not '"
                + lorename + "'.", split.displayName.equals(lorename));
    }

    private void testParseMob(String testVal, String name, String data,
            String lorename) {
        System.out.println("Test parse: '" + testVal + "'");
        DropType split = DropType.parse(testVal, "");
        assertTrue("Error, mat (" + split.getName() + ") is not '" + name
                + "'.", split.getName().equalsIgnoreCase(name));

    }

}
