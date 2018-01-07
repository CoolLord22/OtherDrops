package com.gmail.zariust.otherdrops.data.itemmeta;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.subject.Target;

public class OdLeatherArmorMeta extends OdItemMeta {
    public Color color;

    public OdLeatherArmorMeta(Color color2) {
        this.color = color2;
    }

    @Override
    public ItemStack setOn(ItemStack stack, Target source) {
        if (color != null) {
            LeatherArmorMeta lam = (LeatherArmorMeta) stack.getItemMeta();
            lam.setColor(color);
            stack.setItemMeta(lam);
        }
        return stack;
    }

    public static OdItemMeta parse(String sub) {
        Color color = OdItemMeta.getColorFrom(sub.toUpperCase());
        if (color != null) {
            return new OdLeatherArmorMeta(color);
        } else {
            Log.logInfo("ItemDrop: error - leather armour color not valid.",
                    Verbosity.NORMAL);
            return null;
        }
    }
}
