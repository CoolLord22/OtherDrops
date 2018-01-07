package com.gmail.zariust.otherdrops.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.otherdrops.ConfigurationNode;

// read custommob: in OtherDropsConfig
// store in map of name to custommob objects
// when checking drops - check if it's a valid custommob (_after_ other checks)
// if so replace dropString with "creatureType" & store custommob in drop
// when dropping, check for custommob attached
// set appropriate data on mob

public class CustomMob {
    String     customName;

    EntityType entityType;
    Boolean    isBaby;

    // for Sheep
    String     color;

    Material   equipmentHead;
    Material   equipmentChest;
    Material   equipmentLegs;
    Material   equipmentFeet;
    Material   equipmentHeld;

    String     health;
    String     maxHealth;
    String     damage;
    String     passenger;

    public Map<String, String> getDropChanceQuantity(String toParse) {
        Map<String, String> returnVal = new HashMap<String, String>();
        return returnVal;
    }

    public boolean parse(ConfigurationNode node, String nodeName) {
        customName = nodeName;
        // parse a dropchancequantity for each equipment slot
        Map<String, String> dropChanceQuantity = getDropChanceQuantity(node
                .getString("equipmenthead"));

        dropChanceQuantity.get("drop");
        return true;
    }

    public void setEquipment(LivingEntity le) {
        EntityEquipment equipment = le.getEquipment();
        equipment.setHelmet(new ItemStack(equipmentHead));

    }
}
