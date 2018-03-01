// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops.options;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;

public class ToolDamage {
    private ShortRange durabilityRange;
    private IntRange   consumeRange;
    private Material   replace;
    private int        replaceQuantity;

    public ToolDamage() {
        this(null, 1);
    }

    public ToolDamage(Integer damage) {
        this(damage, 1);
    }

    public ToolDamage(Integer damage, int replaceQuantity) {
        if (damage != null)
            durabilityRange = ShortRange.parse(String.valueOf(damage));
        this.replaceQuantity = replaceQuantity;
    }

    public boolean apply(ItemStack stack, Random rng) {
        boolean fullyConsumed = false;
        short maxDurability = stack.getType().getMaxDurability();
        if (maxDurability > 0 && durabilityRange != null) {
        	
            short durability = stack.getDurability();
            short damage = durabilityRange.getRandomIn(rng);
            
            if (durability + damage >= maxDurability)
                fullyConsumed = true;
            else if (stack.containsEnchantment(Enchantment.DURABILITY)) {
            	int durabilityLevel = (stack.getEnchantmentLevel(Enchantment.DURABILITY) + 1);
            	
            	Random rand = new Random();
            	int n = rand.nextInt(100) + 1;
            	
            	double chanceOfDamage = 100/(durabilityLevel);
            	boolean shouldDamage = (n < chanceOfDamage);
            	
            	if(shouldDamage) {
                    stack.setDurability((short) (durability + damage));
                    Log.logInfo("Tool with unbreaking damaged.", Verbosity.HIGH);
            	}
            }
            else {
                stack.setDurability((short) (durability + damage));
                Log.logInfo("Tool damaged.", Verbosity.HIGH);
            }
        }
        if (consumeRange != null && (fullyConsumed || durabilityRange == null)) {
            if (fullyConsumed) {
                fullyConsumed = false;
                stack.setDurability((short) 0);
            }
            int count = stack.getAmount();
            int take = consumeRange.getRandomIn(rng);
            if (count <= take)
                fullyConsumed = true;
            else
                stack.setAmount(count - take);
            Log.logInfo("Tool consume: " + take + "x " + stack.toString()
                    + " consumed (" + (count - take) + ") remaining.",
                    Verbosity.HIGH);
        }
        if (replace != null && fullyConsumed) {
            fullyConsumed = false;
            stack.setDurability((short) 0);
            stack.setAmount(1);
            stack.setType(replace);
            stack = new ItemStack(replace, replaceQuantity);
            Log.logInfo("Tool replaced.", Verbosity.HIGH);
        } else if (durabilityRange == null && consumeRange == null) {
            fullyConsumed = false;
            stack.setDurability((short) 0);
            stack.setType(replace);
            stack.setAmount(replaceQuantity);
            Log.logInfo("Tool replaced.", Verbosity.HIGH);
        }
        return fullyConsumed;
    }

    public static ToolDamage parseFrom(ConfigurationNode node) {
        ToolDamage damage = new ToolDamage();
        // Durability
        String durability = node.getString("damagetool");
        if (durability != null)
            damage.durabilityRange = ShortRange.parse(durability);
        else {
            durability = node.getString("fixtool");
            if (durability != null) {
                ShortRange range = ShortRange.parse(durability);
                damage.durabilityRange = range.negate(range);
            }
        }
        // Amount
        String consume = node.getString("consumetool");
        if (consume != null)
            damage.consumeRange = IntRange.parse(consume);
        else {
            consume = node.getString("growtool");
            if (consume != null) {
                IntRange range = IntRange.parse(consume);
                damage.consumeRange = range.negate(range);
            }
        }
        // Replace
        String replace = node.getString("replacetool");
        if (replace != null) {
            String[] replaceSplit = replace.split("/");
            replace = replaceSplit[0];
            int replaceQuantity = 1;
            try {
                if (replaceSplit.length > 1)
                    replaceQuantity = Integer.parseInt(replaceSplit[1]);
            } catch (NumberFormatException e) {
            } // no need to do anything, default quantity is 1

            damage.replace = CommonMaterial.matchMaterial(replace);
            damage.replaceQuantity = replaceQuantity;
            Log.logInfo("...tool will be replaced by " + damage.replace,
                    Verbosity.NORMAL);
        }
        if (damage.durabilityRange != null || damage.consumeRange != null
                || damage.replace != null)
            return damage;
        return null;
    }

    public boolean isReplacement() {
        return (this.replace != null) ? true : false;
    }

    @Override
    public String toString() {
        StringBuilder dmg = new StringBuilder("{");
        dmg.append("damage: ");
        dmg.append(durabilityRange);
        dmg.append("quantity: ");
        dmg.append(consumeRange);
        dmg.append("replace: ");
        dmg.append(replace);
        dmg.append("}");
        return dmg.toString();
    }
}
