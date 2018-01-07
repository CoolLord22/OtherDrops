package com.gmail.zariust.otherdrops.things;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.OtherDropsConfig;

public class ODVariablesTest {

    @Test
    public void testDropShortcutParsing() {
        // needs verbosity
        OtherDropsConfig.setVerbosity(Verbosity.EXTREME);

        testMultiTextChoice("<1|2>", 1);
        testMultiTextChoice("<1|2|3>", 1);
        testMultiTextChoice("<1|2|3|4>", 1);
        testMultiTextChoice("<1|2|3|4|5>", 1);
        testMultiTextChoice("<1~4>", 1);
        testMultiTextChoice("<1-4>", 1);
        testMultiTextChoice("<dagger|swords|snails> of +<1~6> damage", 19);
    }

    private void testMultiTextChoice(String testVal, int lengthShouldBe) {
        System.out.println("Test: '" + testVal + "'");
        String result = ODVariables.parseMultipleOptions(testVal);
        System.out.println("Result: '" + result + "'");
        int amount = result.length();
        assertTrue("Error, length (" + amount + ") is not '" + lengthShouldBe
                + "' (test: " + testVal + ")", amount == lengthShouldBe);

    }


}
