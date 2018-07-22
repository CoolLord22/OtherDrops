package com.gmail.zariust.otherdrops.data.itemmeta;

import org.bukkit.Bukkit;
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
        String tempOwner = parseVariables(owner, stack, source);
        short skullData = 3;
        SkullType skullType = null;
        try {
            skullType = SkullType.valueOf(tempOwner.toUpperCase());
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
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(tempOwner));
        stack.setDurability(skullData);
        stack.setItemMeta(meta);
        return stack;
    }

    private String parseVariables(String owner2, ItemStack stack, Target source) {
    	String valueToReturn;
    	valueToReturn = owner2;
        if (owner2.equalsIgnoreCase("%v") || owner2.equalsIgnoreCase("THIS")) {
            if (source instanceof PlayerSubject) {
                PlayerSubject ps = (PlayerSubject) source;
                valueToReturn =  ps.getPlayer().getName();
            } else if (source instanceof CreatureSubject) {
                String[] dataSplit = source.toString().split("@");
                EntityType creatureType = CommonEntity.getCreatureEntityType(dataSplit[0]);
                valueToReturn = creatureType.toString();
                
                if(creatureType == EntityType.BLAZE || creatureType == EntityType.CHICKEN ||
                		creatureType == EntityType.COW || creatureType == EntityType.CREEPER ||
                		creatureType == EntityType.ENDERMAN || creatureType == EntityType.GHAST ||
                		creatureType == EntityType.OCELOT || creatureType == EntityType.PIG ||
                		creatureType == EntityType.SHEEP || creatureType == EntityType.SKELETON ||
                		creatureType == EntityType.SKELETON || creatureType == EntityType.SLIME ||
                		creatureType == EntityType.SPIDER || creatureType == EntityType.SQUID ||
                		creatureType == EntityType.VILLAGER || creatureType == EntityType.ZOMBIE ||
                		creatureType == EntityType.CAVE_SPIDER || creatureType == EntityType.PIG_ZOMBIE ||
                		creatureType == EntityType.MUSHROOM_COW)
                    valueToReturn = "MHF_" + creatureType.toString().replaceAll("[\\s-_]", "");
                if(creatureType == EntityType.IRON_GOLEM)
                	valueToReturn = "MHF_Golem";
                if(creatureType == EntityType.MAGMA_CUBE)
                	valueToReturn = "MHF_LavaSlime";
                if(creatureType == EntityType.WITHER_SKELETON)
                	valueToReturn = "MHF_WSkeleton";
            }
        }
        return valueToReturn;
    }

    public static OdItemMeta parse(String sub) {
        if (!sub.isEmpty()) {
            return new OdSkullMeta(sub);
        } else {
            return null;
        }
    }
}
