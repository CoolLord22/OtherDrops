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

package com.gmail.zariust.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.EntityType;
import static org.bukkit.entity.EntityType.*;

public enum CreatureGroup {
    CREATURE_HOSTILE(BLAZE, CREEPER, ELDER_GUARDIAN, ENDERMITE, EVOKER, GHAST, GUARDIAN, HUSK, MAGMA_CUBE, SILVERFISH, SKELETON,
    		SLIME, STRAY, VEX, VINDICATOR, WITCH, WITHER_SKELETON, ZOMBIE, ZOMBIE_VILLAGER), 
    
    CREATURE_FRIENDLY(BAT, CHICKEN, COW, MUSHROOM_COW, PIG, RABBIT, SHEEP, SQUID, DONKEY, MULE, HORSE, PARROT, OCELOT), 
    
    CREATURE_NEUTRAL(PIG_ZOMBIE, WOLF, ENDERMAN, POLAR_BEAR, LLAMA), 
    
    CREATURE_ANIMAL(COW, CHICKEN, PIG, SHEEP, WOLF, BAT, DONKEY, HORSE, MULE, MUSHROOM_COW, OCELOT, RABBIT, POLAR_BEAR, LLAMA, PARROT), 
    
    CREATURE_UNDEAD(PIG_ZOMBIE, ZOMBIE, SKELETON, ENDERMAN, ZOMBIE_VILLAGER, HUSK, STRAY), 
    
    CREATURE_BUG(SPIDER, CAVE_SPIDER, SILVERFISH, ENDERMITE), 
    
    CREATURE_WATER(SQUID),
    
    // FIXME:: (Zarius) Lazy aliases - find a better way that covers both with
    // CREATURE_ and without?
    CREATURE_LAVASLIME(MAGMA_CUBE), 
    CREATURE_MOOSHROOM(MUSHROOM_COW), 
    CREATURE_SNOW_MAN(SNOWMAN), 
    CREATURE_ENDERDRAGON(ENDER_DRAGON),
    CREATURE_VINDICATOR(VINDICATOR),

    // Add any new ones before this line
    CREATURE_ANY;
    private static Map<String, CreatureGroup> lookup = new HashMap<String, CreatureGroup>();
    private ArrayList<EntityType>             mob;

    static {
        for (EntityType mob : EntityType.values()) {
            CREATURE_ANY.mob.add(mob);
        }
        for (CreatureGroup group : values())
            lookup.put(group.name(), group);
    }

    private void add(List<EntityType> materials) {
        mob.addAll(materials);
    }

    private CreatureGroup(EntityType... materials) {
        this();
        add(Arrays.asList(materials));
    }

    private CreatureGroup(CreatureGroup... merge) {
        this();
        for (CreatureGroup group : merge)
            add(group.mob);
    }

    private CreatureGroup(List<EntityType> materials, CreatureGroup... merge) {
        this(merge);
        add(materials);
    }

    private CreatureGroup() {
        mob = new ArrayList<EntityType>();
    }

    @SuppressWarnings("unchecked")
    public List<EntityType> creatures() {
        return (List<EntityType>) mob.clone();
    }

    public static CreatureGroup get(String string) {
        return lookup.get(string.toUpperCase());
    }

    public static Set<String> all() {
        return lookup.keySet();
    }

    public static boolean isValid(String string) {
        return lookup.containsKey(string);
    }

    public boolean contains(EntityType material) {
        return mob.contains(material);
    }
}
