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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.data.EffectData;

public class SoundEffect {
    private final Effect     type;
    // TODO: Would be nice to include note block sounds in here (missing API
    // though)
    private final EffectData data;

    public SoundEffect(Effect effect) {
        this(effect, null);
    }

    public SoundEffect(Effect effect, EffectData d) {
        type = effect;
        data = d;
    }

    public void play(Location location) {
        if (type != null) {
            if (data == null)
                location.getWorld().playEffect(location, type, 0,
                        EffectData.DEFAULT_RADIUS);
            else
                location.getWorld().playEffect(location, type, data.getData(),
                        data.getRadius());
        }
    }

    public static SoundEffect parse(String key) {
        String[] split = key.split("@");
        String name = split[0], data = "";
        if (split.length > 1)
            data = split[1];
        try {
            Effect effect = null;
            for (Effect loopEffect : Effect.values()) {
                if (CommonMaterial.fuzzyMatchString(loopEffect.name(), name)) {
                    effect = loopEffect;
                }
            }
            if (effect == null)
                return null;
            // I believe all data should be uppercase, this could be done at a
            // later
            // stage if any effect data needs case sensitivity
            EffectData state = EffectData.parse(effect, data.toUpperCase());
            return new SoundEffect(effect, state);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Set<SoundEffect> parseFrom(ConfigurationNode node) {
        List<String> effects = OtherDropsConfig.getMaybeList(node, "effect",
                "effects");
        if (effects.isEmpty())
            return null;
        Set<SoundEffect> result = new HashSet<SoundEffect>();
        for (String name : effects) {
            SoundEffect effect = parse(name);
            if (effect == null) {
                Log.logWarning("Invalid effect " + name + "; skipping...");
                continue;
            }
            result.add(effect);
        }
        if (result.isEmpty())
            return null;
        return result;
    }

    @Override
    public String toString() {
        String ret = type.toString();
        // TODO: Will data ever be null, or will it just be 0?
        if (data != null)
            ret += "@" + data.get(type);
        return ret;
    }
}
