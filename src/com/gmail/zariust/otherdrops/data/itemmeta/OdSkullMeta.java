package com.gmail.zariust.otherdrops.data.itemmeta;

import org.bukkit.SkullType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.gmail.zariust.common.CommonEntity;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.Target;

public class OdSkullMeta extends OdItemMeta {
    public String owner;

    public OdSkullMeta(String owner) {
        this.owner = owner;
    }

    @Override
    public ItemStack setOn(ItemStack stack, Target source) {
        if (owner == null)
            return null;
        owner = parseVariables(owner, stack, source);
        short skullData = 3;
        SkullType skullType = null;
        try {
            skullType = SkullType.valueOf(owner.toUpperCase());
        } catch (Exception e) {
            // do nothing
        }
        if (skullType != null) {
            switch (skullType) {
            case CREEPER:
                skullData = 4;
                break;
            case SKELETON:
                skullData = 0;
                break;
            case WITHER:
                skullData = 1;
                break;
            case ZOMBIE:
                skullData = 2;
                break;
            default:
                break;
            }
        }

        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        stack.setDurability(skullData);
        stack.setItemMeta(meta);
        return stack;
    }

    private String parseVariables(String owner2, ItemStack stack, Target source) {
        if (owner2.equalsIgnoreCase("%v") || owner2.equalsIgnoreCase("THIS")) {
            if (source instanceof PlayerSubject) {
                PlayerSubject ps = (PlayerSubject) source;
                return ps.getPlayer().getName();
            } else if (source instanceof CreatureSubject) {
                String[] dataSplit = source.toString().split("@");
                EntityType creatureType = CommonEntity
                        .getCreatureEntityType(dataSplit[0]);
                return creatureType.toString();
            }
        }
        return owner2;
    }

    public static OdItemMeta parse(String sub) {
        if (!sub.isEmpty()) {
            return new OdSkullMeta(sub);
        } else {
            return null;
        }
    }
}
