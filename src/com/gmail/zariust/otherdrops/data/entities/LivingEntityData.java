package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.EntityWrapper;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.drop.ItemDrop;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.things.ODVariables;

public class LivingEntityData extends CreatureData {
    Double maxHealth = null;
    CreatureEquipment equip      = null;
    String            customName = null;

    public LivingEntityData(Double maxHealth, CreatureEquipment equip,
            String customName) {
        this.maxHealth = maxHealth;
        this.equip = equip;
        this.customName = ODVariables.preParse(customName);
    }

    @Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof LivingEntity) {
            LivingEntity z = (LivingEntity) mob;

            // Maxhealth wrapped in a try/catch so equipment setting below will
            // continue even if setting the max health fails.
            // (maxhealth failed with some values on an older version of Bukkit)
            try {
                if (maxHealth != null) {
                    EntityWrapper.setMaxHealth(z, maxHealth);
                    EntityWrapper.setHealth(z, maxHealth);
                }
            } catch (Exception e) {

            }

            if (equip != null) {
                if (equip.head != null)
                    z.getEquipment().setHelmet(equip.head);
                if (equip.headChance != null)
                    z.getEquipment().setHelmetDropChance(equip.headChance);
                if (equip.hands != null)
                    z.getEquipment().setItemInHand(equip.hands);
                if (equip.handsChance != null)
                    z.getEquipment().setItemInHandDropChance(equip.handsChance);
                if (equip.chest != null)
                    z.getEquipment().setChestplate(equip.chest);
                if (equip.chestChance != null)
                    z.getEquipment().setChestplateDropChance(equip.chestChance);
                if (equip.legs != null)
                    z.getEquipment().setLeggings(equip.legs);
                if (equip.legsChance != null)
                    z.getEquipment().setLeggingsDropChance(equip.legsChance);
                if (equip.boots != null)
                    z.getEquipment().setBoots(equip.boots);
                if (equip.bootsChance != null)
                    z.getEquipment().setBootsDropChance(equip.bootsChance);

            } else {
            }
            setDefaultEq((LivingEntity) mob);

            // not currently used - refer to CreatureDrop instead
            if (customName != null) {
                String parsedCustomName = new ODVariables()
                .setPlayerName(owner.getName())
                .parse(customName);

                z.setCustomName(parsedCustomName);
            }

        }
    }

    private void setDefaultEq(LivingEntity mob) {
        if (mob instanceof Skeleton) {
            Skeleton skellie = (Skeleton) mob;
            if (skellie.getSkeletonType() == SkeletonType.WITHER) {
                if (equip == null || equip.hands == null)
                    skellie.getEquipment().setItemInHand(
                            new ItemStack(Material.STONE_SWORD));
            } else {
                if (equip == null || equip.hands == null)
                    skellie.getEquipment().setItemInHand(
                            new ItemStack(Material.BOW));
            }
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof LivingEntityData)) {
            Log.logInfo("Checking LivingEntityData: target data not LivingEntityData. d=" + d.toString() + " (type: " + d.getClass().getName() + ")", Verbosity.EXTREME);
            return false;
        }
        LivingEntityData vd = (LivingEntityData) d;

        if (this.maxHealth != null)
            if (!this.maxHealth.equals(vd.maxHealth))
            {
                Log.logInfo("Checking LivingEntityData: maxHealth failed.", Verbosity.EXTREME);
                return false;
            }

        // compare equipment
        if (this.equip != null) {
            if (!this.equip.matches(vd.equip))
            {
                Log.logInfo("Checking LivingEntityData: equipment failed.", Verbosity.EXTREME);
                return false;
            }
        }

        if (this.customName != null) {
            if (this.customName.isEmpty()) {
                // if this.customName is empty it means "match only mobs with no name"
                if (!(vd.customName == null)) { // this means the mob has a name, so fail
                    Log.logInfo("Checking LivingEntityData: customname failed.", Verbosity.EXTREME);
                    return false;
                }
            } else if (this.customName.equals("*")) {
                // * is a wildcard = match any name (except none) so fail if no mob name
                if (vd.customName == null) {
                    Log.logInfo("Checking LivingEntityData: customname failed.", Verbosity.EXTREME);
                    return false;
                }
            } else {
                // not empty & not wildcard - check for name match
                if (vd.customName == null || !vd.customName.equals(this.customName))
                    Log.logInfo("Checking LivingEntityData: customname failed.", Verbosity.EXTREME);
                    return false;
            }
        }

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof LivingEntity) {
            return new LivingEntityData(EntityWrapper.getMaxHealth((LivingEntity) entity),
                    CreatureEquipment.parseFromEntity(entity),
                    ((LivingEntity) entity).getCustomName());
        } else {
            Log.logInfo("LivingEntityData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    public static CreatureData parseFromString(String state) {
        Double maxHealth = null;
        CreatureEquipment equip = null;
        String customName = null;

        if (!state.isEmpty() && !state.equals("0")) {
            if (state.contains("~")) customName = ""; // to support "ZOMBIE~" meaning Zombie with no custom name
            
            String customNameSplit[] = state.split("~", 2);
            state = customNameSplit[0];
            if (customNameSplit.length > 1)
                customName = customNameSplit[1];

            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {

                if (sub.matches("(?i)[0-9.]+hp?")) { // need to check numbers
                                                    // before any .toLowerCase()
                    maxHealth = Double.valueOf(sub.replaceAll("[^0-9.]", ""));
                } else {
                    sub = sub.replaceAll("[\\s-_]", "");
                    if (sub.matches("(?i)eq:.*")) {
                        if (equip == null)
                            equip = new CreatureEquipment();
                        equip = parseEquipmentString(sub, equip);
                    }
                }
            }
        }

        return new LivingEntityData(maxHealth, equip, customName);
    }

    private static CreatureEquipment parseEquipmentString(String sub,
            CreatureEquipment passEquip) {
        CreatureEquipment equip = passEquip;
        String subSplit[] = sub.split(":", 3);

        if (subSplit.length == 3) {
            String split[] = subSplit[2].split("%"); // split out the drop
                                                     // chance, if any
            String slot = split[0];
            float chance = 100; // default to 100% drop chance
            if (split.length > 1) {
                chance = Float.valueOf(split[1]) / 100;
            }

            if (subSplit[1].matches("(?i)(head|helmet)")) {
                equip.head = getItemStack(slot);
                equip.headChance = chance;
            } else if (subSplit[1].matches("(?i)(hands|holding)")) {
                equip.hands = getItemStack(slot);
                equip.handsChance = chance;
            } else if (subSplit[1].matches("(?i)(chest|chestplate|body)")) {
                equip.chest = getItemStack(slot);
                equip.chestChance = chance;
            } else if (subSplit[1].matches("(?i)(legs|leggings|legplate)")) {
                equip.legs = getItemStack(slot);
                equip.legsChance = chance;
            } else if (subSplit[1].matches("(?i)(feet|boots)")) {
                equip.boots = getItemStack(slot);
                equip.bootsChance = chance;
            }
        }
        return equip;

    }

    private static ItemStack getItemStack(String slot) {

        // this section doesn't work yet - need to save a list of itemstacks and
        // choose one at spawn time
        /*
         * if (slot.startsWith("any")) { // material group
         * Log.logInfo("Checking materialgroup..."); MaterialGroup group =
         * MaterialGroup.get(slot); if (group != null) { Material mat =
         * group.getOneRandom(); if (mat != null) {
         * Log.logInfo("Checking materialgroup...MAT = "+mat.toString()); return
         * new ItemStack(mat); } } } else {
         */
        ItemDrop item = (ItemDrop) ItemDrop.parse(slot, "", new IntRange(1),
                100);
        if (item != null)
            return item.getItem();
        // }

        return null;
    }

    @Override
    public String toString() {
        String val = "";
        if (equip != null) {
            val += "!!" + equip.toString();
        }
        if (maxHealth != null) {
            val += "%" + maxHealth.toString() + "h";
        }
        return val;
    }

    @Override
    public String get(Enum<?> creature) {
        if (creature instanceof EntityType)
            return this.toString();
        return "";
    }

}
