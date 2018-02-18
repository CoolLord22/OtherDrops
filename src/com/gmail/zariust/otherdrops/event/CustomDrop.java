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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Dependencies;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.data.Data;
import com.gmail.zariust.otherdrops.event.ExclusiveMap.ExclusiveKey;
import com.gmail.zariust.otherdrops.options.Comparative;
import com.gmail.zariust.otherdrops.options.Flag;
import com.gmail.zariust.otherdrops.options.IntRange;
import com.gmail.zariust.otherdrops.options.Time;
import com.gmail.zariust.otherdrops.options.Weather;
import com.gmail.zariust.otherdrops.parameters.Action;
import com.gmail.zariust.otherdrops.parameters.Condition;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.Agent;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.ProjectileAgent;
import com.gmail.zariust.otherdrops.subject.Target;

public abstract class CustomDrop extends AbstractDropEvent implements Runnable {
    // Conditions
    private Map<Agent, Boolean>     tools;
    private Map<World, Boolean>     worlds;
    private Map<String, Boolean>    regions;
    private Map<Weather, Boolean>   weather;
    private Map<BlockFace, Boolean> faces;
    private Map<Biome, Boolean>     biomes;
    private Map<Time, Boolean>      times;
    private Map<String, Boolean>    permissionGroups;                // obseleted
                                                                      // - use
                                                                      // permissions
    private Map<String, Boolean>    permissions;
    private Set<Flag>               flags;
    private final Flag.FlagState    flagState = new Flag.FlagState();
    private Comparative             height;
    private Comparative             attackRange;
    private Comparative             lightLevel;
    // Chance
    private double                  chance;
    private String                  exclusiveKey;
    // Delay
    private IntRange                delay;
    // Execution; this is the actual event that this matched
    protected OccurredEvent         currentEvent;

    // Will this drop the default items?
    public abstract boolean isDefault();

    // The name of this drop
    public abstract String getDropName();

    protected List<String>        messages;
    private final List<Action>    actions    = new ArrayList<Action>();
    private final List<Condition> conditions = new ArrayList<Condition>();
    private boolean               defaultOverride;

    // Conditions
    @Override
    public boolean matches(AbstractDropEvent other) {
        // TODO: not as elegant as the single liner but needed for debugging
        Double rolledValue = rng.nextDouble();
        boolean chancePassed = rolledValue <= chance / 100.0;
        if (!chancePassed) {
            Log.logInfo("Drop failed due to chance (" + String.valueOf(chance)
                    + ", rolled: " + rolledValue * 100 + ")", HIGHEST);
            return false;
        }

        if (!basicMatch(other)) {
            Log.logInfo("CustomDrop.matches(): basic match failed.", HIGHEST);
            return false;
        }
        if (other instanceof OccurredEvent) {
            OccurredEvent drop = (OccurredEvent) other;
            currentEvent = drop;

            if (!isTool(drop.getTool()))
                return false; // TODO: log message is inside isTool check - do
                              // this for all?
            if (!isWorld(drop.getWorld())) {
                Log.logInfo("CustomDrop.matches(): world match failed.",
                        HIGHEST);
                return false;
            }
            if (!isRegion(drop.getRegions())) {
                Log.logInfo("CustomDrop.matches(): region match failed.",
                        HIGHEST);
                return false;
            }
            if (!isWeather(drop.getWeather())) {
                Log.logInfo("CustomDrop.matches(): weather match failed.",
                        HIGHEST);
                return false;
            }
            if (!isBlockFace(drop.getFace())) {
                Log.logInfo("CustomDrop.matches(): blockface match failed.",
                        HIGHEST);
                return false;
            }
            if (!isBiome(drop.getBiome())) {
                Log.logInfo(
                        "CustomDrop.matches(): biome match failed (current biome="
                                + drop.getBiome() + ", list: " + biomes.toString() + ")", HIGHEST);
                return false;
            }
            if (!isTime(drop.getTime())) {
                Log.logInfo("CustomDrop.matches(): time match failed.", HIGHEST);
                return false;
            }
            if (!isHeight(drop.getHeight())) {
                Log.logInfo("CustomDrop.matches(): height match failed.",
                        HIGHEST);
                return false;
            }
            if (!isAttackInRange((int) drop.getAttackRange())) {
                Log.logInfo("CustomDrop.matches(): range match failed.",
                        HIGHEST);
                return false;
            }
            if (!isLightEnough(drop.getLightLevel())) {
                Log.logInfo("CustomDrop.matches(): lightlevel match failed.",
                        HIGHEST);
                return false;
            }
            if (!inGroup(drop.getTool())) {
                Log.logInfo("CustomDrop.matches(): player group match failed.",
                        HIGHEST);
                return false;
            }
            if (!hasPermission(drop.getTool())) {
                Log.logInfo(
                        "CustomDrop.matches(): player permission match failed.",
                        HIGHEST);
                return false;
            }
            if (!checkFlags(drop)) {
                Log.logInfo("CustomDrop.matches(): a flag match failed.",
                        HIGHEST);
                return false;
            }

            boolean inMobArenaFlag = false;
            for (Flag activeflag : flags) {
                if (activeflag.toString().matches("IN_MOB_ARENA"))
                    inMobArenaFlag = true;
            }

            if (!inMobArenaFlag)
                if (Dependencies.hasMobArena())
                    if (Dependencies.getMobArenaHandler().inRunningRegion(
                            this.currentEvent.getLocation()))
                        return false;

            for (Condition condition : conditions) {
                if (!condition.check(this, currentEvent))
                    return false;
            }

            return true;
        }

        Log.logInfo(
                "CustomDrop.matches(): match failed - not an OccuredEvent?",
                HIGHEST);
        return false;
    }

    public void setTool(Map<Agent, Boolean> tool) {
        this.tools = tool;
    }

    public Map<Agent, Boolean> getTool() {
        return tools;
    }

    public String getToolString() {
        return mapToString(tools);
    }

    public boolean isTool(Agent tool) {
        boolean positiveMatch = false;
        if (tools == null)
            return true;
        // tools={DIAMOND_SPADE@=true}
        // tool=PLAYER@Xarqn with DIAMOND_SPADE@4
        // Note: tools.get(tool) fails with a player.

        // Check for tool matches
        for (Map.Entry<Agent, Boolean> agent : tools.entrySet()) {
            if (!agent.getValue())
                continue;
            if (agent.getKey().matches(tool)) {
                positiveMatch = true;
                break;
            }
        }

        // Check for tool exception matches
        for (Map.Entry<Agent, Boolean> agent : tools.entrySet()) {
            if (agent.getValue())
                continue;
            if (agent.getKey().matches(tool)) {
                positiveMatch = false;
                break;
            }
        }
        if (!positiveMatch)
            Log.logInfo(
                    "Tool match = " + positiveMatch + " - tool="
                            + String.valueOf(tool) + " tools="
                            + tools.toString(), HIGHEST);
        return positiveMatch;
    }

    public void setWorlds(Map<World, Boolean> places) {
        this.worlds = places;
    }

    public Map<World, Boolean> getWorlds() {
        return worlds;
    }

    public String getWorldsString() {
        return mapToString(worlds);
    }

    public boolean isWorld(World world) {
        return checkList(world, worlds);
    }

    public void setRegions(Map<String, Boolean> areas) {
        this.regions = areas;
    }

    public Map<String, Boolean> getRegions() {
        return regions;
    }

    public String getRegionsString() {
        return mapToString(regions);
    }

    /**
     * Check if the current regions match the configured regions.
     * 
     * @param inRegions
     *            - a set of regions the player is currently in
     * @return true if the condition matches
     */
    public boolean isRegion(Set<String> inRegions) {
        // if no regions configured then all is ok
        if (regions == null)
            return true;

        Log.logInfo("Regioncheck: inRegions: " + inRegions.toString(),
                Verbosity.HIGH);
        Log.logInfo("Regioncheck: dropRegions: " + regions.toString(),
                Verbosity.HIGH);

        // save the config region keys in a temp list for some reason (can't
        // remember)
        HashSet<String> tempConfigRegionKeys = new HashSet<String>();
        tempConfigRegionKeys.addAll(regions.keySet());

        // set matched flag to false, since we know there's at least something
        // in the customRegion condition
        boolean matchedRegion = false;
        int positiveRegions = 0;

        // loop through each region within the customRegions and check if it
        // matches all current regions
        for (String dropRegion : tempConfigRegionKeys) {
            dropRegion = dropRegion.toLowerCase(); // WorldGuard, at least,
                                                   // stores regions in lower
                                                   // case
            // Check if the entry is an exception (ie. starts with "-")
            Boolean exception = false;
            if (dropRegion.startsWith("-")) {
                Log.logInfo("Checking dropRegion exception: " + dropRegion,
                        Verbosity.EXTREME);
                exception = true;
                dropRegion = dropRegion.substring(1);
            } else {
                positiveRegions++;
                Log.logInfo("Checking dropRegion: " + dropRegion,
                        Verbosity.EXTREME);
            }

            if (exception) {
                if (inRegions.contains(dropRegion)) {
                    Log.logInfo("Failed check: regions (exception: "
                            + dropRegion + ")", Verbosity.HIGH);
                    return false; // if this is an exception and you are in that
                                  // region then all other checks are moot -
                                  // hence immediate "return false"
                } else {
                    Log.logInfo("Exception check: region " + dropRegion
                            + " passed", Verbosity.HIGHEST);
                }
            } else {
                if (inRegions.contains(dropRegion)) {
                    Log.logInfo("In dropRegion: " + dropRegion
                            + ", setting match=TRUE", Verbosity.HIGHEST);
                    matchedRegion = true;
                } else {
                    // OtherDrops.logInfo("Not in dropRegion: "+dropRegion+", setting match=FALSE",
                    // Verbosity.HIGHEST);
                    // matchedRegion = false;
                }

            }

        }

        // If there were only exception conditions then return true as we
        // haven't been kicked by a matched exception
        if (positiveRegions < 1)
            matchedRegion = true;

        Log.logInfo("Regioncheck: finished. match=" + matchedRegion,
                Verbosity.HIGH);
        return matchedRegion;
    }

    public void setWeather(Map<Weather, Boolean> sky) {
        this.weather = sky;
    }

    public Map<Weather, Boolean> getWeather() {
        return weather;
    }

    public String getWeatherString() {
        return mapToString(weather);
    }

    public boolean isWeather(Weather sky) {
        // return checkList(sky, weather); // TODO: (ZAR) doesn't work - but
        // should, no?

        if (weather == null)
            return true;
        boolean match = weather.get(null);
        for (Weather type : weather.keySet()) {
            if (type != null) {
                if (type.matches(sky)) {
                    if (weather.get(type))
                        match = true;
                    else
                        return false;
                }
            }
        }
        return match;
    }

    public void setBlockFace(Map<BlockFace, Boolean> newFaces) {
        this.faces = newFaces;
    }

    public Map<BlockFace, Boolean> getBlockFaces() {
        return faces;
    }

    public String getBlockFacesString() {
        return mapToString(faces);
    }

    public boolean isBlockFace(BlockFace face) {
        return checkList(face, faces);
    }

    public void setBiome(Map<Biome, Boolean> biome) {
        this.biomes = biome;
    }

    public Map<Biome, Boolean> getBiome() {
        return biomes;
    }

    public String getBiomeString() {
        return mapToString(biomes);
    }

    public static <T> boolean checkList(T obj, Map<T, Boolean> list) {
        // Check if null - return true (this should only happen if no defaults
        // have been set)
        if (list == null || obj == null)
            return true;

        // Check if empty (this should only happen if an invalid world or biome,
        // etc is set)
        // We return false as the user obviously wants it only to occur for a
        // specific world, even if that world doesn't exist
        if (list.isEmpty())
            return false;

        // Check if a key matches (important to do this before checking for null
        // key [all])
        // eg. for the config [ALL, -DESERT] this will return false for desert
        // before it gets to true for all
        if (list.containsKey(obj))
            return list.get(obj);

        return list.get(null);

    }

    public boolean isBiome(Biome biome) {
        return checkList(biome, biomes);
    }

    public void setTime(Map<Time, Boolean> time) {
        this.times = time;
    }

    public Map<Time, Boolean> getTime() {
        return times;
    }

    public String getTimeString() {
        return mapToString(times);
    }

    public boolean isTime(long time) {
        if (times == null)
            return true;
        boolean match = false;
        for (Time t : times.keySet()) {
            if (t.contains(time)) {
                if (times.get(t))
                    match = true;
                else
                    return false;
            }
        }
        return match;
    }

    public void setGroups(Map<String, Boolean> newGroups) {
        this.permissionGroups = newGroups;
    }

    public Map<String, Boolean> getGroups() {
        return permissionGroups;
    }

    public String getGroupsString() {
        return mapToString(permissionGroups);
    }

    public boolean inGroup(Agent agent) {
        if (permissionGroups == null)
            return true;
        Player player = null;

        if (!(agent instanceof PlayerSubject)) {
            if (agent instanceof ProjectileAgent) {
                Entity shooter = ((ProjectileAgent) agent).getShooter()
                        .getEntity();
                if (shooter instanceof Player) {
                    player = (Player) shooter;
                }
            } else
                return false; // if permissions is set and agent is not a
                              // player, fail
        }

        if (player == null)
            player = ((PlayerSubject) agent).getPlayer();

        boolean match = false;
        for (String group : permissionGroups.keySet()) {
            if (OtherDrops.inGroup(player, group)) {
                if (permissionGroups.get(group))
                    match = true;
                else
                    return false;
            }
        }
        return match;
    }

    public void setPermissions(Map<String, Boolean> newPerms) {
        this.permissions = newPerms;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public String getPermissionsString() {
        return mapToString(permissions);
    }

    public boolean hasPermission(Agent agent) {
        if (permissions == null)
            return true;
        Player player = null;

        if (!(agent instanceof PlayerSubject)) {
            if (agent instanceof ProjectileAgent) {
                Entity shooter = ((ProjectileAgent) agent).getShooter()
                        .getEntity();
                if (shooter instanceof Player) {
                    player = (Player) shooter;
                }
            }
            if (player == null)
                return false; // if permissions is set and agent (or shooter) is
                              // not a player, fail
        }

        if (player == null)
            player = ((PlayerSubject) agent).getPlayer();

        boolean match = false;
        for (String perm : permissions.keySet()) {
            if (perm.startsWith("!")) {
                perm = perm.substring(1);
                
                if (Dependencies.hasPermission(player, perm)) {
                    if (permissions.get(perm))
                        match = true;
                    else {
                        return false;	
                    }
                }
            } 
            
            else {
            	if (Dependencies.hasPermission(player, "otherdrops.custom." + perm)) {
                    if (permissions.get(perm))
                        match = true;
                    else {
                        return false;	
                    }
                }
            }
        }
        return match;
    }

    public void setHeight(Comparative h) {
        this.height = h;
    }

    public Comparative getHeight() {
        return height;
    }

    public boolean isHeight(int h) {
        if (height == null)
            return true;
        return height.matches(h);
    }

    public void setAttackRange(Comparative range) {
        this.attackRange = range;
    }

    public Comparative getAttackRange() {
        return attackRange;
    }

    public boolean isAttackInRange(int range) {
        if (attackRange == null)
            return true;
        return attackRange.matches(range);
    }

    public void setLightLevel(Comparative light) {
        this.lightLevel = light;
    }

    public Comparative getLightLevel() {
        return lightLevel;
    }

    public boolean isLightEnough(int light) {
        if (lightLevel == null)
            return true;
        return lightLevel.matches(light);
    }

    public void setFlags(Set<Flag> newFlags) {
        flags = newFlags;
    }

    public void setFlag(Flag flag) {
        if (flags == null)
            setFlags(new HashSet<Flag>());
        flags.add(flag);
    }

    public boolean hasFlag(Flag flag) {
        if (flags == null)
            setFlags(new HashSet<Flag>());
        return flags.contains(flag);
    }

    public void unsetFlag(Flag flag) {
        if (flags == null)
            setFlags(new HashSet<Flag>());
        flags.remove(flag);
    }

    public Flag.FlagState getFlagState() {
        return flagState;
    }

    public boolean checkFlags(OccurredEvent drop) {
        boolean shouldDrop = true;
        for (Flag flag : Flag.values()) {
            // Error: flags.contains(flag) was returning true even for flags not
            // in the hashset
            boolean match = false;
            for (Flag activeflag : flags) {
                if (activeflag.toString().matches(flag.toString()))
                    match = true;
            }
            // Logic issue: if flags that are not active are processed we may
            // override continuedropping and dropthis settings...
            if (match == true) {
                flag.matches(drop, match, flagState);
                shouldDrop = shouldDrop && flagState.dropThis;
            }
        }
        return shouldDrop;
    }

    // Chance
    public boolean willDrop(ExclusiveMap exclusives) {
        if (exclusives != null && exclusiveKey != null) {
            if (!exclusives.contains(exclusiveKey)) {
                Data data = currentEvent.getTarget().getData();
                exclusives.put(exclusiveKey, data);
            }
            ExclusiveKey key = exclusives.get(exclusiveKey);
            key.cumul += getChance();
            if (key.select > key.cumul) {
                Log.logInfo("Drop failed due to exclusive key (" + exclusiveKey
                        + ").", HIGHEST);
                return false;
            }
        }
        // TODO: not as elegant as the single liner but needed for debugging
        Double rolledValue = rng.nextDouble();
        boolean chancePassed = rolledValue <= chance / 100.0;
        if (chancePassed) {
            return true;
        } else {
            Log.logInfo("Drop failed due to chance (" + String.valueOf(chance)
                    + ", rolled: " + rolledValue * 100 + ")", HIGHEST);
            return false;
        }
    }

    public void setChance(double percent) {
        chance = percent;
    }

    public double getChance() {
        return chance;
    }

    public void setExclusiveKey(String key) {
        exclusiveKey = key;
    }

    public String getExclusiveKey() {
        return exclusiveKey;
    }

    protected CustomDrop(Target targ, Trigger trigger) {
        super(targ, trigger);
    }

    // Delay
    public int getRandomDelay() {
        if (delay.getMin() == delay.getMax())
            return delay.getMin();

        int randomVal = (delay.getMin() + rng.nextInt(delay.getMax()
                - delay.getMin() + 1));
        return randomVal;
    }

    public String getDelayRange() {
        return delay.getMin().equals(delay.getMax()) ? delay.getMin()
                .toString() : delay.getMin().toString() + "-"
                + delay.getMax().toString();
    }

    public void setDelay(IntRange val) {
        delay = val;
    }

    public void setDelay(int val) {
        delay = new IntRange(val, val);
    }

    public IntRange getDelay() {
        return this.delay;
    }

    public void setDelay(int low, int high) {
        delay = new IntRange(low, high);
    }

    public void perform(OccurredEvent evt) {
        currentEvent = evt;

        int schedule = getRandomDelay();
        // if(schedule > 0.0)
        // Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(OtherDrops.plugin,
        // this, schedule);
        // else run();

        Location playerLoc = null;
        Player player = null; // FIXME: need to get player early - in event
        // if (evt.player != null) playerLoc = player.getLocation();
        DropRunner dropRunner = new DropRunner(OtherDrops.plugin, evt, this,
                player, playerLoc, this.isDefault());

        // schedule the task - NOTE: this must be a sync task due to the changes
        // made in the performActualDrop function
        if (schedule > 0.0)
            Bukkit.getServer()
                    .getScheduler()
                    .scheduleSyncDelayedTask(OtherDrops.plugin, dropRunner,
                            schedule);
        else
            dropRunner.run();
        // }
    }

    private String setToString(Set<?> set) {
        if (set.size() > 1)
            return set.toString();
        if (set.isEmpty())
            return "(any/none)";
        List<Object> list = new ArrayList<Object>();
        list.addAll(set);
        if (list.get(0) == null) {
            Log.logWarning(
                    "CustomDropEvent.setToString - list.get(0) is null?",
                    Verbosity.HIGHEST);
            return "";
        }
        return list.get(0).toString();
    }

    private String mapToString(Map<?, Boolean> map) {
        return (map == null) ? null : setToString(stripFalse(map));
    }

    private Set<?> stripFalse(Map<?, Boolean> map) {
        Set<Object> set = new HashSet<Object>();
        for (Object key : map.keySet()) {
            if (map.get(key))
                set.add(key);
        }
        return set;
    }

    @Override
    public String getLogMessage() {
        StringBuilder log = new StringBuilder();
        log.append(toString() + ": ");
        // Tool
        log.append(mapToString(tools));
        // Faces
        if (faces != null)
            log.append(" on faces " + mapToString(faces));
        // Placeholder for drops info
        log.append(" now drops %d");
        // Chance
        log.append(" with " + Double.toString(chance) + "% chance");
        // Worlds and regions
        if (worlds != null) {
            log.append(" in worlds " + mapToString(worlds));
            if (regions != null)
                log.append(" and regions " + mapToString(regions));
        } else if (regions != null)
            log.append(" in regions " + mapToString(regions));
        // Other conditions
        if (weather != null || biomes != null || times != null
                || height != null || attackRange != null || lightLevel != null) {
            log.append(" with conditions");
            char sep = ':';
            if (weather != null) {
                log.append(sep + " " + mapToString(weather));
                sep = ',';
            }
            if (biomes != null) {
                log.append(sep + " " + mapToString(biomes));
                sep = ',';
            }
            if (times != null) {
                log.append(sep + " " + mapToString(times));
                sep = ',';
            }
            if (height != null) {
                log.append(sep + " " + height.toString());
                sep = ',';
            }
            if (attackRange != null) {
                log.append(sep + " " + attackRange.toString());
                sep = ',';
            }
            if (lightLevel != null) {
                log.append(sep + " " + lightLevel.toString());
                sep = ',';
            }
        }
        return log.toString();
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return (trigger.toString() + " on "
                + ((target == null) ? "<no block>" : target.toString())
                + " drops " + getDropName());
    }

    public void addActions(List<Action> parse) {
        if (parse != null)
            this.actions.addAll(parse);
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public void addConditions(List<Condition> parse) {
        if (parse != null)
            this.conditions.addAll(parse);
    }

    public boolean getDefaultOverride() {
        return this.defaultOverride;
    }

    public void setDefaultOverride(boolean set) {
        this.defaultOverride = set;
    }
}
