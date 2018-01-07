package com.gmail.zariust.otherdrops.drop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;

public class DropResult {
    private int         quantity;
    public List<Entity> droppedEntities   = new ArrayList<Entity>();
    private boolean     overrideDefault;
    private boolean     overrideDefaultXp = false;                  // default
                                                                     // to false
    private boolean     overrideEquipment = false;

    public DropResult() {
        quantity = 0;
    }

    public DropResult(int quant) {
        quantity = quant;
    }

    public DropResult(boolean overrideDefault2) {
        this.overrideDefault = overrideDefault2;
        this.quantity = 0;
    }

    static DropResult fromQuantity(int quant) {
        return new DropResult(quant);
    }

    public static DropResult fromOverride(boolean overrideDefault2) {
        return new DropResult(overrideDefault2);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getOverrideDefault() {
        return overrideDefault;
    }

    public void setOverrideDefault(boolean overrideDefault) {
        this.overrideDefault = overrideDefault;
    }

    public boolean getOverrideDefaultXp() {
        return overrideDefaultXp;
    }

    public void setOverrideDefaultXp(boolean b) {
        overrideDefaultXp = b;

    }

    public void addDropped(Entity ent) {
        droppedEntities.add(ent);
    }

    public void addDropped(List<Entity> ent) {
        droppedEntities.addAll(ent);
    }

    public List<Entity> getDropped() {
        return droppedEntities;
    }

    public String getDroppedString() {
        String val = "[";
        for (Entity ent : droppedEntities) {
            if (ent instanceof Item) {
                val += ((Item) ent).getItemStack().toString();
            } else if (ent instanceof LivingEntity) {
                val += ((LivingEntity) ent).toString();
            } else
                val += ent.toString() + ",";
        }
        if (val.length() > 1)
            val.subSequence(0, val.length() - 1);
        val += "]";
        return val;
    }

    public void add(DropResult drop) {
        this.quantity = drop.getQuantity();
        this.addDropped(drop.getDropped());
        if (drop.getOverrideDefault())
            this.setOverrideDefault(drop.getOverrideDefault());
        if (drop.getOverrideDefaultXp())
            this.setOverrideDefaultXp(drop.getOverrideDefaultXp());
    }

    public void addWithoutOverride(DropResult drop) {
        this.quantity = drop.getQuantity();
        this.addDropped(drop.getDropped());
    }

    public static DropResult getFromOverrideDefault(boolean overrideDefault2) {
        return new DropResult(overrideDefault2);

    }

    public boolean isOverrideEquipment() {
        return overrideEquipment;
    }

    public void setOverrideEquipment(boolean overrideEquipment) {
        this.overrideEquipment = overrideEquipment;
    }

}
