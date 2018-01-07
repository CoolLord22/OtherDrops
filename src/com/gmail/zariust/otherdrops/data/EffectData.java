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

package com.gmail.zariust.otherdrops.data;


import org.bukkit.Effect;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.data.effects.SmokeEffectData;
import com.gmail.zariust.otherdrops.data.effects.StepSoundEffectData;

public class EffectData implements Data {
    public static final int DEFAULT_RADIUS = 16;
    protected int             data;
    protected int           radius;

    public EffectData() {
        // nothing to do here, needed for subclasses
    }

    public EffectData(int d) {
        data = d;
    }

    @Override
    public int getData() {
        return data;
    }

    @Override
    public void setData(int d) {
        data = d;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int r) {
        radius = r;
    }

    @Override
    public boolean matches(Data d) {
        return data == d.getData();
    }

    @Override
    public String get(Enum<?> mat) {
        if (mat instanceof Effect)
            return get((Effect) mat);
        return "";
    }

    protected String get(Effect effect) {
        return "";
    }

    @Override
    // No effect has a block state, so nothing to do here.
    public void setOn(BlockState state) {
    }

    @Override
    // Effects are not entities, so nothing to do here.
    public void setOn(Entity entity, Player witness) {
    }

    public static EffectData parse(Effect effect, String state) {
        // note: null values are ok and should set reasonable defaults on the
        // effects
        String[] split = state.split("/");
        String key = split[0];
        int radius = DEFAULT_RADIUS; // default radius that noise is heard
                                     // within
        EffectData data;
        switch (effect) {
        case RECORD_PLAY:
            data = RecordData.parse(key);
            break;
        case SMOKE:
            data = SmokeEffectData.parse(key);
            break;
        case STEP_SOUND: // apparently this is actually BLOCK_BREAK
            data = StepSoundEffectData.parse(key);
            break;
        default:
            data = new EffectData(0);
            break;
        }
        
        if (split.length > 1) {
            try {
                radius = Integer.parseInt(split[1]);
                data.setRadius(radius);
            } catch (NumberFormatException e) {
            }
        }
        data.setRadius(radius);
        return data;
    }

    @Override
    public int hashCode() {
        return data ^ radius;
    }

    @Override
    public Boolean getSheared() {
        // TODO Auto-generated method stub
        return null;
    }
}
