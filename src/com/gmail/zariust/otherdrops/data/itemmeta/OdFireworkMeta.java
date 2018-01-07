package com.gmail.zariust.otherdrops.data.itemmeta;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.gmail.zariust.otherdrops.subject.Target;

public class OdFireworkMeta extends OdItemMeta {
    public String owner;

    public OdFireworkMeta(String owner) {
        this.owner = owner;
    }

    @Override
    public ItemStack setOn(ItemStack stack, Target source) {
        if (owner == null)
            return null;
        FireworkMeta meta = (FireworkMeta) stack.getItemMeta();
        // FIXME: allow for custom details
        Color color = OdItemMeta.getColorFrom(owner);
        if (color != null) {
            meta.addEffect(FireworkEffect.builder().trail(false).flicker(false)
                    .withColor(color).build());
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public static OdItemMeta parse(String sub) {
        if (!sub.isEmpty()) {
            return new OdFireworkMeta(sub);
        } else {
            return null;
        }
    }
}
