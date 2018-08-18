package com.gmail.zariust.otherdrops.data.entities;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.CreatureData;
import com.gmail.zariust.otherdrops.data.Data;

public class RabbitData extends CreatureData {
	Rabbit.Type rabbitType = null;
	
	AgeableData ageData = null;
	
	public RabbitData(Type rabbitType, AgeableData ageData) {
		this.rabbitType = rabbitType;
		this.ageData = ageData;
	}
	
	public RabbitData(String state) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setOn(Entity mob, Player owner) {
		if (mob instanceof Rabbit) {
			Rabbit z = (Rabbit) mob;
			if (rabbitType != null) 
				z.setRabbitType(rabbitType);

			ageData.setOn(mob, owner);
		}
	}
	
	@Override
	public boolean matches(Data d) {
		if (!(d instanceof RabbitData))
			return false;
		
		RabbitData vd = (RabbitData) d;
		
		if (!(ageData.matches(vd.ageData)))
			return false;
		if (this.rabbitType != null)
			if (this.rabbitType != vd.rabbitType)
				return false;
		
		return true;
	}
	
	public static CreatureData parseFromEntity(Entity entity) {
		if (entity instanceof Rabbit) 
			return new RabbitData(((Rabbit) entity).getRabbitType(), (AgeableData) AgeableData.parseFromEntity(entity));
		else {
			Log.logInfo("RabbitData: error, parseFromEntity given different creature - this shoudln't happen");
			return null;
		}
	}
	
	public static CreatureData parseFromString(String state) {
		
		Rabbit.Type thisType = null; //null = wildcard
		AgeableData ageData =  (AgeableData) AgeableData.parseFromString(state);
		
		if (!state.isEmpty() && !state.equals("0")) {
			String split[] = state.split(OtherDropsConfig.CreatureDataSeparator);
			
			for (String sub : split) {
				sub = sub.toLowerCase().replaceAll("[\\s-_]", "");
				if (sub.matches("typeblack"))
					thisType = Rabbit.Type.BLACK;
				if (sub.matches("typeblackandwhite"))
					thisType = Rabbit.Type.BLACK_AND_WHITE;
				if (sub.matches("typebrown"))
					thisType = Rabbit.Type.BROWN;
				if (sub.matches("typegold"))
					thisType = Rabbit.Type.GOLD;
				if (sub.matches("typesaltandpepper"))
					thisType = Rabbit.Type.SALT_AND_PEPPER;
				if (sub.matches("typekillerbunny"))
					thisType = Rabbit.Type.THE_KILLER_BUNNY;
				if (sub.matches("typewhite"))
					thisType = Rabbit.Type.WHITE;
			}
		}
		
		return new RabbitData(thisType, ageData);
	}
	
	@SuppressWarnings("unused")
	private static CreatureData getData(String state) {
		return new RabbitData(state);
	}
	
	@Override
	public String toString() {
		String val = "";
		if (rabbitType != null) {
			val += "!";
			val += rabbitType.toString();
		}
		val += ageData.toString();
		return val;
	}
	
	@Override
	public String get(Enum<?> creature) {
		if (creature instanceof EntityType) 
			return this.toString();
		return "";
	}
}
