package com.gmail.zariust.common;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.options.IntRange;

public class CMEnchantment {
    private Enchantment ench;
    private IntRange    level;
    boolean             enchRandom  = false;
    boolean             levelRandom = false;
    private boolean noEnch;

    /**
     * Get enchantment - don't process random
     * 
     * @return Enchantment - if null assume random
     */
    public Enchantment getEnchRaw() {
        if (ench == null)
            return CommonEnchantments.getRandomEnchantment(null);
        return ench;
    }

    /**
     * Get enchantment
     * 
     * @return Enchantment - if null will return a random enchantment
     */
    public Enchantment getEnch() {
        return getEnch(null);
    }

    /**
     * Get enchantment based on given stack (only used if set to random)
     * 
     * @param stack
     * @return
     */
    public Enchantment getEnch(ItemStack stack) {
        if (enchRandom)
            ench = CommonEnchantments.getRandomEnchantment(stack);
        return ench;
    }

    /**
     * Set enchantment, if null assumes random
     * 
     * @param ench
     */
    public void setEnch(Enchantment ench) {
        if (ench == null)
            enchRandom = true;
        this.ench = ench;
    }

    /**
     * Get enchantment level as a intrange - if null assume random
     * 
     * @return
     */
    public int getLevel() {
        if (levelRandom) {
            if (ench == null)
                return 1;
            return new IntRange(ench.getStartLevel(), ench.getMaxLevel())
                    .getRandomIn(OtherDrops.rng);
        }
        return level.getRandomIn(OtherDrops.rng);
    }

    /**
     * Returns the actual IntRange
     * 
     * @return
     */
    public IntRange getLevelRange() {
        return level;
    }

    /**
     * Set enchantment level using IntRange - if null assumes random
     * 
     * @param level
     */
    public void setLevelRange(IntRange level) {
        if (level == null)
            levelRandom = true;
        this.level = level;
    }

    public void setNoEnch(boolean b) {
        this.noEnch = b;
    }

    public boolean getNoEnch() {
        return this.noEnch;
    }
}
