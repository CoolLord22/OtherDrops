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

package com.gmail.zariust.otherdrops.event;

import static com.gmail.zariust.common.Verbosity.HIGHEST;

import java.util.Random;

import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.options.ConfigOnly;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.Target;

public abstract class AbstractDropEvent {
    protected Target  target;
    protected Trigger trigger;
    public Random     rng;

    public AbstractDropEvent(Target targ, Trigger trigger) {
        target = targ;
        this.trigger = trigger;
        rng = OtherDrops.rng;
    }

    /**
     * @param diff
     *            A flag whose value doesn't matter but whose presence means
     *            "validate the target".
     */
    protected AbstractDropEvent(Target targ, Trigger act, boolean diff)
            throws DropCreateException {
        this(targ, act);
        if (targ.getClass().isAnnotationPresent(ConfigOnly.class)) {
            ConfigOnly annotate = targ.getClass().getAnnotation(
                    ConfigOnly.class);
            throw new DropCreateException(targ.getClass(), annotate.value());
        }
    }

    public abstract boolean matches(AbstractDropEvent other);

    public void setTarget(Target targ) {
        this.target = targ;
    }

    public Target getTarget() {
        return target;
    }

    public void setTrigger(Trigger act) {
        this.trigger = act;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public int getRandom(int limit) {
        return rng.nextInt(limit);
    }

    @Override
    public String toString() {
        return trigger.toString() + " on "
                + ((target == null) ? "<no block>" : target.toString());
    }

    public abstract String getLogMessage();

    public boolean basicMatch(AbstractDropEvent other) {
        if (!target.matches(other.target)) {
            Log.logInfo(
                    "AbstractDrop - basicMatch/target (type=" + target.getClass() + ") - failed. this.target="
                            + target.toString() + " other.target="
                            + other.target.toString(), HIGHEST);
            return false;
        }
        if (!trigger.equals(other.trigger)) {
            Log.logInfo(
                    "AbstractDrop - basicMatch/trigger - failed. this.trigger="
                            + trigger.toString() + " other.trigger="
                            + other.trigger.toString(), HIGHEST);
            return false;
        }
        return true;
    }
}
