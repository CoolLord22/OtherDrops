package com.gmail.zariust.otherdrops.event;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantment extends Enchantment {

    public CustomEnchantment(int id) {
        super(id);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean canEnchantItem(ItemStack arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMaxLevel() {
        // TODO Auto-generated method stub
        return 5;
    }

    @Override
    public String getName() {
        return "DAMAGE_ALL";
    }

    @Override
    public int getStartLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

}
