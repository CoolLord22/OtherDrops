package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class CreatureEquipment {
    public ItemStack head;
    public Float     headChance  = 10F;
    public ItemStack chest;
    public Float     chestChance = 10F;
    public ItemStack legs;
    public Float     legsChance  = 10F;
    public ItemStack handsMain;
    public ItemStack handsOff;
    public Float     handsMainChance = 10F;
    public Float     handsOffChance = 10F;
    public ItemStack boots;
    public Float     bootsChance = 10F;

    public CreatureEquipment() {
        // used if manually setting eq values
    }

    public CreatureEquipment(EntityEquipment eq) {
        if (eq.getHelmet() != null)
            this.head = eq.getHelmet();
        if (eq.getItemInMainHand() != null)
            this.handsMain = eq.getItemInMainHand();
        if (eq.getItemInOffHand() != null)
            this.handsOff = eq.getItemInOffHand();
        if (eq.getChestplate() != null)
            this.chest = eq.getChestplate();
        if (eq.getLeggings() != null)
            this.legs = eq.getLeggings();
        if (eq.getBoots() != null)
            this.boots = eq.getBoots();
    }

    @Override
    public String toString() {
        String msg = "";

        if (head != null)
            msg += "!!" + head.toString() + "%" + headChance.toString() + "%";
        if (chest != null)
            msg += "!!" + chest.toString() + "%" + chestChance.toString() + "%";
        if (legs != null)
            msg += "!!" + legs.toString() + "%" + legsChance.toString() + "%";
        if (handsMain != null)
            msg += "!!" + handsMain.toString() + "%" + handsMainChance.toString() + "%";
        if (handsOff != null)
            msg += "!!" + handsOff.toString() + "%" + handsOffChance.toString() + "%";
        if (boots != null)
            msg += "!!" + boots.toString() + "%" + bootsChance.toString() + "%";

        return msg;
    }

    public boolean matches(CreatureEquipment equip) {
        if (equip == null)
            return false;

        if (head != null) {
            if (head.getType() != equip.head.getType()) {
                return false;
            }
        }
        if (chest != null)
            if (chest.getType() != equip.chest.getType())
                return false;
        if (legs != null)
            if (legs.getType() != equip.legs.getType())
                return false;
        if (handsMain != null)
            if (handsMain.getType() != equip.handsMain.getType())
                return false;
        if (handsOff != null)
            if (handsOff.getType() != equip.handsOff.getType())
                return false;
        if (boots != null)
            if (boots.getType() != equip.boots.getType())
                return false;

        return true;
    }

    public static CreatureEquipment parseFromEntity(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) entity;
            EntityEquipment eq = le.getEquipment();
            if (eq != null) {
                return new CreatureEquipment(eq);
            }
        }
        return null;
    }
}
