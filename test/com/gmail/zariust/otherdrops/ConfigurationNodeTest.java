package com.gmail.zariust.otherdrops;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationNodeTest {

    @Test
    public void testParameterLoading() {

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("chance", new Double(50.5));
        map.put("quantity", new Integer(10));

        ConfigurationNode node = new ConfigurationNode(map);

        String value = node.getString("chance", null);
        assertTrue(value + " is null", value != null);
        assertTrue(value + " not parsed correctly. =" + value,
                value.equals("50.5"));

        value = node.getString("quantity");
        assertTrue(value + " is null", value != null);
        assertTrue(value + " not parsed correctly. =" + value,
                value.equals("10"));

    }

}
