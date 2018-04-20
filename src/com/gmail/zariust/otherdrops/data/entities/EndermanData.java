package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.data.SimpleData;

public class EndermanData extends CreatureData {
    private MaterialData md       = null;
    private Boolean      canCarry = null;
    LivingEntityData     leData   = null;

    public EndermanData(MaterialData type, Boolean canCarry,
            LivingEntityData leData) {
        this.md = type;
        this.canCarry = canCarry;
        this.leData = leData;
    }

    @SuppressWarnings("unused")
	@Override
    public void setOn(Entity mob, Player owner) {
        if (mob instanceof Enderman) {
            Enderman z = (Enderman) mob;
            if (this.md != null)
                ((Enderman) mob).setCarriedMaterial(md);
            if (this.canCarry != null)
                ((Enderman) mob).setCanPickupItems(canCarry);

            leData.setOn(mob, owner);
        }
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof EndermanData))
            return false;
        EndermanData vd = (EndermanData) d;

        if (this.md != null)
            if (this.md != vd.md)
                return false;

        if (this.canCarry != null)
            if (this.canCarry != vd.canCarry)
                return false;

        if (!leData.matches(vd.leData))
            return false;

        return true;
    }

    public static CreatureData parseFromEntity(Entity entity) {
        if (entity instanceof Enderman) {
            return new EndermanData(((Enderman) entity).getCarriedMaterial(),
                    ((Enderman) entity).getCanPickupItems(),
                    (LivingEntityData) LivingEntityData.parseFromEntity(entity));
        } else {
            Log.logInfo("EndermanData: error, parseFromEntity given different creature - this shouldn't happen.");
            return null;
        }

    }

    @SuppressWarnings("deprecation")
	public static CreatureData parseFromString(String state) {

        Log.logInfo("EndermanData: parsing from string.", Verbosity.HIGHEST);
        MaterialData materialData = null;
        Boolean canCarry = null;

        LivingEntityData leData = (LivingEntityData) LivingEntityData
                .parseFromString(state);

        if (!state.isEmpty() && !state.equals("0")) {
            String split[] = state
                    .split(OtherDropsConfig.CreatureDataSeparator);

            for (String sub : split) {
                sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
                if (sub.equalsIgnoreCase("carry"))
                    canCarry = true;
                else if (sub.equalsIgnoreCase("nocarry"))
                    canCarry = false;
                else {
                    // nothing else to check so assume material
                    Material material = null;
                    Data data = new SimpleData();
                    if (sub.contains("@")) {
                        String[] split2 = sub.split("@", 2);
                        material = CommonMaterial.matchMaterial(split2[0]);
                        data = SimpleData.parse(material, split2[1]);
                    } else {
                        material = CommonMaterial.matchMaterial(sub);
                    }
                    if (material != null && data == null)
                        materialData = material.getNewData((byte) 0);
                    else if (material != null)
                        materialData = material.getNewData((byte) data.getData());

                }
            }
        }
        return new EndermanData(materialData, canCarry, leData);
    }

    /*
     * split = state.split("/"); Material material =
     * Material.getMaterial(split[0]); if (material == null) { try { material =
     * Material.getMaterial(Integer.parseInt(split[0])); }
     * catch(NumberFormatException e) { return new CreatureData(0); } } Data
     * data = new SimpleData(); if(split.length > 1) data =
     * SimpleData.parse(material, split[1]); else data = new SimpleData(); int
     * md = (data.getData() << 8) | material.getId(); return new
     * CreatureData(md);
     */

	@SuppressWarnings("deprecation")
	@Override
    public String toString() {
        String val = "";
        if (md != null) {
            val += "!!" + md.getItemType().toString() + "@" + md.getData();
        }
        if (canCarry != null) {
            val += (canCarry ? "!!carry" : "!!nocarry");
        }
        val += leData.toString();
        return val;
    }

    /*
     * if(data > 0) { int id = data & 0xF, d = data >> 8; Material material =
     * Material.getMaterial(id); Data data = new SimpleData(d); String dataStr =
     * data.get(material); result = material.toString(); if(!dataStr.isEmpty())
     * result += "/" + dataStr; return result; }
     */

    @Override
    public String get(Enum<?> creature) {
        if (creature instanceof EntityType)
            return this.toString();
        return "";
    }

}
