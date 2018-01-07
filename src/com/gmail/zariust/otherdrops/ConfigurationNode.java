// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Zarius Tularial
//
// This file released under Evil Software License v1.1
// <http://fredrikvold.info/ESL.htm>

package com.gmail.zariust.otherdrops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigurationNode {

    Map<String, Object> nodeMap;

    public ConfigurationNode(ConfigurationSection configurationSection) {
        // FIXME: doesn't do anything - used by "events" reading in config?
    }

    // Example input: {drop=SULPHUR, chance=100, message=[Boom!]}
    @SuppressWarnings("unchecked")
    public ConfigurationNode(Map<?, ?> map) {
        // TODO Auto-generated constructor stub
        nodeMap = (Map<String, Object>) map;
    }

    /**
     * Parses a list of maps from the YamlConfig into "fake" ConfigurationNode
     * list. Example input: [{drop=SULPHUR, chance=100, message=[Boom!]},
     * {tool=ANY_SPADE, action=LEFT_CLICK, drop=EGG}]
     * 
     * @param mapList
     *            from YamlConfiguration.getConfigurationSection().getMapList()
     * @return a list of ConfigurationNode's
     */
    public static List<ConfigurationNode> parse(List<Map<?, ?>> mapList) {
        // OtherDrops.logInfo(mapList.toString());
        List<ConfigurationNode> nodeList = new ArrayList<ConfigurationNode>();

        for (Map<?, ?> map : mapList)
            nodeList.add(new ConfigurationNode(map));

        return nodeList;
    }

    public List<String> getKeys() {
        if (nodeMap == null)
            return null;

        List<String> stringList = new ArrayList<String>();
        stringList.addAll(nodeMap.keySet());
        return stringList;
    }

    public String getString(String string) {
        if (nodeMap == null)
            return null;
        Object obj = nodeMap.get(string);
        if (obj == null)
            return null;

        if (obj instanceof String)
            return (String) nodeMap.get(string);
        if (obj instanceof Integer)
            return ((Integer) nodeMap.get(string)).toString();
        if (obj instanceof Double)
            return ((Double) nodeMap.get(string)).toString();
        return null;
    }

    public List<String> getStringList(String key) {
        if (nodeMap == null)
            return null;
        List<String> returnList = null;
        if (nodeMap.get(key) instanceof List<?>) {
            returnList = new ArrayList<String>();
            for (Object value : (List<?>) nodeMap.get(key)) {
                if (value instanceof String) {
                    returnList.add((String) value);
                } else if (value instanceof Integer) {
                    returnList.add(String.valueOf(value));
                }
            }
        }

        return returnList;
    }

    // get property
    public Object get(String key) {
        if (nodeMap == null)
            return null;
        return nodeMap.get(key);
    }

    // get property
    public Object get(String... keys) {
        if (nodeMap == null)
            return null;
        Object object;
        for (String key : keys) {
            object = nodeMap.get(key);
            if (object != null)
                return object;
        }
        return null;
    }

    // get property
    public Integer getInteger(String... keys) {
        if (nodeMap == null)
            return null;
        Object object;
        for (String key : keys) {
            object = nodeMap.get(key);
            if (object != null) {
                if (object instanceof Integer)
                    return (Integer) object;
                if (object instanceof String)
                    return Integer.valueOf((String) object);
                if (object instanceof Double)
                    return ((Double) object).intValue();
                if (object instanceof Float)
                    return ((Float) object).intValue();
            }
        }
        return null;
    }

    public String getString(String key, String defaultVal) {
        if (nodeMap == null)
            return null;
        Object obj = nodeMap.get(key);
        if (obj == null)
            return defaultVal;

        if (obj instanceof String || obj instanceof Integer
                || obj instanceof Double || obj instanceof Float
                || obj instanceof Boolean)
            return nodeMap.get(key).toString();
        return defaultVal;
    }

    // example:
    // [{dropgroup=fishcaught, action=FISH_CAUGHT, drops=[{drop=IRON_HELM@50,
    // message=Bonus!You found an iron helmet - a little rusty but still ok.,
    // chance=90%, exclusive=1}, {drop={DIAMOND=null, GOLD_ORE=null,
    // OBSIDIAN=null}, chance=90%, exclusive=1, message=Woo, you hooked a
    // precious stone!}]}]
    public List<ConfigurationNode> getNodeList(String key, Object defaultVal) {
        if (nodeMap == null)
            return null;
        if (nodeMap.get(key) instanceof List<?>)
            if (((List<?>) nodeMap.get(key)).get(0) instanceof Map) {
                List<ConfigurationNode> nodeList = new ArrayList<ConfigurationNode>();

                @SuppressWarnings("unchecked")
                List<Map<?, ?>> mapList = (List<Map<?, ?>>) nodeMap.get(key);
                for (Map<?, ?> map : mapList)
                    nodeList.add(new ConfigurationNode(map));

                return nodeList;
            }

        return null;
    }

    public Boolean getBoolean(String string, Boolean b) {
        if (nodeMap == null)
            return b;
        if (nodeMap.get(string) instanceof Boolean)
            return (Boolean) nodeMap.get(string);
        return b;
    }

    public Double getDouble(String string, int b) {
        return getDouble(string, Double.valueOf(b));
    }

    public Double getDouble(String string, Double b) {
        if (nodeMap == null)
            return b;

        Object object;
        object = nodeMap.get(string);
        if (object != null) {
            if (object instanceof Integer)
                return Double.valueOf((Integer) object);
            if (object instanceof String)
                return Double.valueOf((String) object);
            if (object instanceof Double)
                return ((Double) object);
            if (object instanceof Float)
                return ((Float) object).doubleValue();
        }

        return b;
    }

    // get property
    public Integer getInt(String string, int i) {
        if (nodeMap == null)
            return null;
        Object object;
        object = nodeMap.get(string);
        if (object != null) {
            if (object instanceof Integer)
                return (Integer) object;
            if (object instanceof String)
                return Integer.valueOf((String) object);
            if (object instanceof Double)
                return ((Double) object).intValue();
            if (object instanceof Float)
                return ((Float) object).intValue();
        }
        return i;
    }

    public ConfigurationNode getConfigurationNode(String name) {
        // TODO Auto-generated method stub
        if (nodeMap == null)
            return null;

        if (nodeMap.get(name) instanceof Map)
            return new ConfigurationNode((Map<?, ?>) nodeMap.get(name));
        else
            return null;
    }

    public void set(String name, HashMap<String, Object> hashMap) {
        if (nodeMap == null)
            nodeMap = new HashMap<String, Object>();
        nodeMap.put(name, hashMap);
    }

    @Override
    public String toString() {
        if (nodeMap != null)
            return nodeMap.toString();
        else
            return "{-Nodemap is null-}";
    }
}
