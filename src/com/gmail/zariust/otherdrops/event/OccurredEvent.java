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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Dependencies;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.options.ConfigOnly;
import com.gmail.zariust.otherdrops.options.Weather;
import com.gmail.zariust.otherdrops.parameters.Trigger;
import com.gmail.zariust.otherdrops.subject.Agent;
import com.gmail.zariust.otherdrops.subject.BlockTarget;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.EnvironmentAgent;
import com.gmail.zariust.otherdrops.subject.ExplosionAgent;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.ProjectileAgent;
import com.gmail.zariust.otherdrops.subject.Target;
import com.gmail.zariust.otherdrops.subject.VehicleTarget;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;

/**
 * An actual drop that has occurred and may match one of the configured drops.
 */
public class OccurredEvent extends AbstractDropEvent implements Cancellable {
    private Agent       tool;
    private World       world;
    private Set<String> regions;
    private Weather     weather;
    private BlockFace   face;
    private Biome       biome;
    private long        time;
    private int         height;
    private double      attackRange;
    private int         lightLevel;
    private Location    location;
    private Cancellable event;
    private Event       realEvent;
    private boolean     denied;
    private boolean     overrideDefault;
    private boolean     overrideDefaultXp;
    private double      customDropAmount;
    private BlockTarget replaceBlockWith;
    private boolean     overrideEquipment;
    private String spawnedReason;

    // Constructors
    public OccurredEvent(BlockBreakEvent evt) {
        super(new BlockTarget(evt.getBlock()), Trigger.BREAK);
        event = evt;
        Block block = evt.getBlock();
        List<Block> blocks = evt.getPlayer().getLastTwoTargetBlocks(new HashSet<Material>(), 10);
        if (blocks.size() > 1) {
            face = blocks.get(1).getFace(blocks.get(0));
        }
        setLocationWorldBiomeLight(block);
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        attackRange = measureRange(
                location,
                evt.getPlayer().getLocation(),
                "Block '" + block.getType().toString() + "' broken by '"
                        + tool.toString() + "'");
        setRegions();
    }

    public OccurredEvent(final EntityDeathEvent evt) {
        super(getEntityTarget(evt.getEntity()), Trigger.BREAK);
        setRealEvent(evt);
        event = new Cancellable() {
            // Storing as an array is a crude way to get a copy
            @SuppressWarnings("unused")
			private final ItemStack[] drops = evt.getDrops().toArray(
                                                    new ItemStack[0]);

            @Override
            public boolean isCancelled() {
                return evt.getDrops().isEmpty();
            }

            @Override
            public void setCancelled(boolean cancel) {
                // no need to do anything in here as we deal with clearing drops
                // at the end of OtherDrops.performDrops()
            }
        };
        Entity e = evt.getEntity();
        setLocationWorldBiomeLight(e);
        setWeatherTimeHeight(location);
        setTool(evt.getEntity().getLastDamageCause());
        if (tool == null) {
            Log.logWarning(
                    "EntityDeathEvent: tool is null, this shouldn't happen! Entity:"
                            + e.toString() + " lastDamage: "
                            + e.getLastDamageCause().getCause().toString(),
                    Verbosity.NORMAL);
            return;
        }
        attackRange = measureRange(location, tool.getLocation(),
                "Entity '" + e.toString() + "' killed by '" + tool.toString()
                        + "'");
        setRegions();
    }

    public OccurredEvent(EntityDamageEvent evt) {
        super(getEntityTarget(evt.getEntity()), Trigger.HIT);
        event = evt;
        Entity e = evt.getEntity();
        setLocationWorldBiomeLight(e);
        setWeatherTimeHeight(location);
        if (evt instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent evt2 = (EntityDamageByEntityEvent) evt;
            setTool(evt2.getDamager());
            if (tool != null)
                attackRange = measureRange(location, evt2.getDamager().getLocation(), "Entity '" + e.toString()+ "' damaged by '" + tool.toString() + "'");
        } else
            setTool(evt.getCause());
        setRegions();
    }

    public OccurredEvent(EntityDamageEvent evt, String string) {
        super(getEntityTarget(evt.getEntity()), Trigger.HIT);
        event = evt;
        Entity e = evt.getEntity();
        setLocationWorldBiomeLight(e);
        setWeatherTimeHeight(location);
        if (evt instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent evt2 = (EntityDamageByEntityEvent) evt;
            setTool(evt2.getDamager());
            if (evt2.getDamager() == null) {
                Log.logInfo("EntityDamageEvent: damager is null, please inform developer.");
            } else if (e == null) {
                Log.logInfo("EntityDamageEvent: entity is null, please inform developer.");
            } else if (tool == null) {
                if (!(e instanceof TNTPrimed)) {
                    Log.logInfo(
                            "EntityDamageEvent: tool is null, please inform developer if this wasn't due to TNT (or TNT minecart).",
                            Verbosity.HIGH);
                }
            } else {
                Log.logInfo("Damager: " + evt2.getDamager().toString(),
                        Verbosity.HIGH);
                attackRange = measureRange(location, evt2.getDamager()
                        .getLocation(), "Entity '" + e.toString()
                        + "' damaged by '" + tool.toString() + "'");
            }
        } else
            setTool(evt.getCause());
        setRegions();
    }

    public OccurredEvent(LeavesDecayEvent evt) {
        super(new BlockTarget(evt.getBlock()), Trigger.LEAF_DECAY);
        event = evt;
        setLocationWorldBiomeLight(evt.getBlock());
        setWeatherTimeHeight(location);
        tool = null;
        setRegions();
    }

    public OccurredEvent(VehicleDestroyEvent evt) {
        super(new VehicleTarget(evt.getVehicle()), Trigger.BREAK);
        event = evt;
        setLocationWorldBiomeLight(evt.getVehicle());
        setWeatherTimeHeight(location);
        setTool(evt.getAttacker()); // Note: getAttacker is NULL for
                                    // environmental attack/break
        // environmental attacks (eg. burning) do not have a location, so range
        // is not valid.
        if (evt.getAttacker() instanceof Player) {
            attackRange = measureRange(location, evt.getAttacker()
                    .getLocation(), "Vehicle '"
                    + evt.getVehicle().getType().toString()
                    + "' destroyed by '" + tool.toString() + "'");
        } else {
            attackRange = 0;
        }

        setRegions();
    }

    public OccurredEvent(final PlayerInteractEvent evt, Block block) {
        super(new BlockTarget(block), Trigger.fromInteract(evt.getAction()));

        // Since we track "cancelled" player interact events in order to support
        // left/right clicking on air we need to make sure we do not "uncancel"
        // to cancelled events
        event = new Cancellable() {
            private boolean cancelled = false;
            {
                if (evt.isCancelled())
                    this.cancelled = true;
            }

            @Override
            public void setCancelled(boolean arg0) {
                if (!this.cancelled)
                    evt.setCancelled(arg0);
            }

            @Override
            public boolean isCancelled() {
                return evt.isCancelled();
            }
        };

        realEvent = evt;
        setLocationWorldBiomeLight(block);
        face = evt.getBlockFace();
        setWeatherTimeHeight(location);
        attackRange = measureRange(location, evt.getPlayer().getLocation(),
                "Player '" + evt.getPlayer().getName() + "' interacted with "
                        + block.toString());
        setTool(evt.getPlayer());
        setRegions();
    }

    public OccurredEvent(PlayerInteractEntityEvent evt) {
        super(getEntityTarget(evt.getRightClicked()), Trigger.RIGHT_CLICK);
        event = evt;
        setLocationWorldBiomeLight(evt.getRightClicked());
        setWeatherTimeHeight(location);
        attackRange = measureRange(location, evt.getPlayer().getLocation(),
                "Player '" + evt.getPlayer().getName() + "' interacted with "
                        + evt.getRightClicked().toString());
        setTool(evt.getPlayer());
        setRegions();
    }

    public OccurredEvent(BlockFromToEvent evt) {
        super(new BlockTarget(evt.getToBlock()), Trigger.BREAK);
        event = evt;
        setLocationWorldBiomeLight(evt.getToBlock());
        setWeatherTimeHeight(location);
        tool = new EnvironmentAgent(DamageCause.CUSTOM);
        setRegions();
    }

    public OccurredEvent(ProjectileHitEvent evt, Block hitBlock) {
        super(new BlockTarget(hitBlock), Trigger.PROJECTILE_HIT_BLOCK);
        setRealEvent(evt);
        event = new Cancellable() {
            @Override
            public boolean isCancelled() {
                // no need to do anything - not cancellable
                return false;
            }

            @Override
            public void setCancelled(boolean cancel) {
                // no need to do anything - not cancellable
            }
        };
        setLocationWorldBiomeLight(hitBlock);
        setWeatherTimeHeight(location);
        tool = new ProjectileAgent(evt.getEntity());
        setRegions();
    }

    public OccurredEvent(EntityExplodeEvent evt, Block block) {
        super(new BlockTarget(block), Trigger.BREAK);
        event = evt;
        setLocationWorldBiomeLight(block);
        setWeatherTimeHeight(location);
        tool = new ExplosionAgent(evt.getEntity());
        setRegions();
    }

    // Generic constructors
    /**
     * Create a drop with a block as its target.
     * 
     * @param block
     *            The block.
     * @param trigger
     *            The action that led to this drop (usually your custom
     *            trigger).
     * @param agent
     *            The agent which caused this drop.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Block block, Trigger trigger, Agent agent)
            throws DropCreateException {
        super(new BlockTarget(block), trigger);
        event = null;
        setLocationWorldBiomeLight(block);
        setWeatherTimeHeight(location);
        setTool(agent);
        setRegions();
    }

    /**
     * Create a drop with a block as its target and an entity agent.
     * 
     * @param block
     *            The block.
     * @param trigger
     *            The action that led to this drop (usually your custom
     *            trigger).
     * @param agent
     *            The agent which caused this drop.
     */
    public OccurredEvent(Block block, Trigger trigger, Entity agent) {
        super(new BlockTarget(block), trigger);
        event = null;
        setLocationWorldBiomeLight(block);
        setWeatherTimeHeight(location);
        setTool(agent);
        setRegions();
    }

    /**
     * Create a cancellable drop with a block as its target.
     * 
     * @param block
     *            The block.
     * @param trigger
     *            The action that led to this drop (usually your custom
     *            trigger).
     * @param agent
     *            The agent which caused this drop.
     * @param evt
     *            An interface through which the default behaviour of this drop
     *            may be cancelled.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Block block, Trigger trigger, Agent agent,
            Cancellable evt) throws DropCreateException {
        this(block, trigger, agent);
        event = evt;
    }

    /**
     * Create a cancellable drop with a block as its target and an entity agent.
     * 
     * @param block
     *            The block.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The agent which caused this drop.
     * @param evt
     *            An interface through which the default behaviour of this drop
     *            may be cancelled.
     */
    public OccurredEvent(Block block, Trigger action, Entity agent,
            Cancellable evt) {
        this(block, action, agent);
        event = evt;
    }

    /**
     * Create a drop with an entity as its target.
     * 
     * @param entity
     *            The entity.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The agent which caused this drop.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Entity entity, Trigger action, Agent agent)
            throws DropCreateException {
        super(getEntityTarget(entity), action);
        event = null;
        setLocationWorldBiomeLight(entity);
        setTool(agent);
        setRegions();
    }

    /**
     * Create a drop with an entity as its target and an entity agent.
     * 
     * @param entity
     *            The entity.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The entity which caused this drop.
     */
    public OccurredEvent(Entity entity, Trigger action, Entity agent) {
        super(getEntityTarget(entity), action);
        event = null;
        setLocationWorldBiomeLight(entity);
        setTool(agent);
        setRegions();
    }

    /**
     * Create a cancellable drop with an entity as its target.
     * 
     * @param entity
     *            The entity.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The agent which caused this drop.
     * @param evt
     *            An interface through which the default behaviour of this drop
     *            may be cancelled.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Entity entity, Trigger action, Agent agent,
            Cancellable evt) throws DropCreateException {
        this(entity, action, agent);
        event = evt;
    }

    /**
     * Create a cancellable drop with an entity as its target and an entity
     * agent.
     * 
     * @param entity
     *            The entity.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The entity which caused this drop.
     * @param evt
     *            An interface through which the default behaviour of this drop
     *            may be cancelled.
     */
    public OccurredEvent(Entity entity, Trigger action, Entity agent,
            Cancellable evt) {
        this(entity, action, agent);
        event = evt;
    }

    /**
     * Create a drop with an arbitrary target.
     * 
     * @param targ
     *            The target which was the source of this drop.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The agent which caused this drop.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Target targ, Trigger action, Agent agent)
            throws DropCreateException {
        super(targ, action, true);
        event = null;
        setLocationWorldBiomeLight(targ);
        setTool(agent);
        setRegions();
    }

    /**
     * Create a drop with an arbitrary target and an entity agent.
     * 
     * @param targ
     *            The target which was the source of this drop.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The entity which caused this drop.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Target targ, Trigger action, Entity agent)
            throws DropCreateException {
        super(targ, action, true);
        event = null;
        setLocationWorldBiomeLight(targ);
        setTool(agent);
        setRegions();
    }

    /**
     * Create a cancellable drop with an arbitrary target.
     * 
     * @param targ
     *            The target which was the source of this drop.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The agent which caused this drop.
     * @param evt
     *            An interface through which the default behaviour of this drop
     *            may be cancelled.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Target targ, Trigger action, Agent agent,
            Cancellable evt) throws DropCreateException {
        this(targ, action, agent);
        event = evt;
    }

    /**
     * Create a cancellable drop with an arbitrary target and an entity agent.
     * 
     * @param targ
     *            The target which was the source of this drop.
     * @param action
     *            The action that led to this drop (usually your custom action).
     * @param agent
     *            The entity which caused this drop.
     * @param evt
     *            An interface through which the default behaviour of this drop
     *            may be cancelled.
     * @throws DropCreateException
     *             If you try to use a wildcard target or agent.
     */
    public OccurredEvent(Target targ, Trigger action, Entity agent,
            Cancellable evt) throws DropCreateException {
        this(targ, action, agent);
        event = evt;
    }

    public OccurredEvent(PlayerFishEvent evt) {
        super(new PlayerSubject(evt.getPlayer()), Trigger.FISH_CAUGHT);
        event = evt;
        setLocationWorldBiomeLight(evt.getCaught().getLocation().getBlock());
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        setRegions();
    }

    // Yes, this needs to be a separate constructor as the "super" has to be on
    // the first line and includes the action
    public OccurredEvent(PlayerFishEvent evt, String string) {
        super(new PlayerSubject(evt.getPlayer()), Trigger.FISH_FAILED);
        event = evt;
        setLocationWorldBiomeLight(evt.getPlayer().getLocation().getBlock());
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        setRegions();
    }

    public OccurredEvent(CreatureSpawnEvent evt) {
        super(getEntityTarget(evt.getEntity()), Trigger.MOB_SPAWN);
        event = evt;
        setSpawnedReason(evt.getSpawnReason().toString());
        Entity e = evt.getEntity();
        setLocationWorldBiomeLight(e);
        setWeatherTimeHeight(location);
        setRegions();
    }

    public OccurredEvent(BlockRedstoneEvent evt, Block block) {
        super(new BlockTarget(block), Trigger.POWER_DOWN);
        gatherPowerEventInfo(evt, block);
    }

    /**
     * @param evt
     */
    private void gatherPowerEventInfo(BlockRedstoneEvent evt, Block block) {
        setRealEvent(evt);
        event = new Cancellable() {
            // Storing as an array is a crude way to get a copy
            private Boolean cancelled = false;

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public void setCancelled(boolean cancel) {
                this.cancelled = cancel;
            }
        };
        setLocationWorldBiomeLight(block);
        setWeatherTimeHeight(location);
        setRegions();
    }

    public OccurredEvent(BlockRedstoneEvent evt, Block block, String string) {
        super(new BlockTarget(block), Trigger.POWER_UP);
        gatherPowerEventInfo(evt, block);
    }

    public OccurredEvent(PlayerJoinEvent evt) {
        super(new PlayerSubject(evt.getPlayer().getDisplayName()),
                Trigger.PLAYER_JOIN);
        setRealEvent(evt);
        event = new Cancellable() {
            // Storing as an array is a crude way to get a copy
            private Boolean cancelled = false;

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public void setCancelled(boolean cancel) {
                this.cancelled = cancel;
            }
        };

        setLocationWorldBiomeLight(evt.getPlayer().getLocation().getBlock());
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        setRegions();

    }

    public OccurredEvent(PlayerRespawnEvent evt) {
        super(new PlayerSubject(evt.getPlayer().getDisplayName()),
                Trigger.PLAYER_RESPAWN);
        setRealEvent(evt);
        event = new Cancellable() {
            // Storing as an array is a crude way to get a copy
            private Boolean cancelled = false;

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public void setCancelled(boolean cancel) {
                this.cancelled = cancel;
            }
        };

        setLocationWorldBiomeLight(evt.getPlayer().getLocation().getBlock());
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        setRegions();
    }

    public OccurredEvent(PlayerItemConsumeEvent evt) {
        super(new PlayerSubject(evt.getPlayer()), Trigger.CONSUME_ITEM);
        event = evt;
        setLocationWorldBiomeLight(evt.getPlayer().getLocation().getBlock());
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        setRegions();
    }

    public OccurredEvent(PlayerMoveEvent evt, Block standingOn) {
//        super(new PlayerSubject(evt.getPlayer()), Trigger.PLAYER_MOVE);
        super(new BlockTarget(standingOn), Trigger.PLAYER_MOVE);
        event = evt;
        setLocationWorldBiomeLight(evt.getPlayer().getLocation().getBlock());
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        setRegions();
    }

    public OccurredEvent(EntityExplodeEvent evt, Entity entity) {
        super(getEntityTarget(evt.getEntity()), Trigger.BREAK);
        event = evt;
        Entity e = evt.getEntity();
        setLocationWorldBiomeLight(e);
        setWeatherTimeHeight(location);
        tool = new ExplosionAgent(evt.getEntity());
        setRegions();
    }


    @SuppressWarnings("deprecation")
	public OccurredEvent(BlockGrowEvent evt) {
        super(new BlockTarget(evt.getNewState().getType(), evt.getBlock().getLocation(), evt.getNewState().getRawData()), Trigger.BLOCK_GROW);
        event = evt;
        setLocationWorldBiomeLight(evt.getBlock());
        setWeatherTimeHeight(location);
        setRegions();
    }

    public OccurredEvent(BlockPlaceEvent evt) {
        super(new BlockTarget(evt.getBlock()), Trigger.BLOCK_PLACE);
        event = evt;
        Block block = evt.getBlock();
        List<Block> blocks = evt.getPlayer().getLastTwoTargetBlocks(new HashSet<Material>(), 10);
        if (blocks.size() > 1) {
            face = blocks.get(1).getFace(blocks.get(0));
        }
        setLocationWorldBiomeLight(block);
        setWeatherTimeHeight(location);
        setTool(evt.getPlayer());
        attackRange = measureRange(
                location,
                evt.getPlayer().getLocation(),
                "Block '" + block.getType().toString() + "' placed by '"
                        + tool.toString() + "'");
        setRegions();
    }

    // Constructor helpers
    private void setWeatherTimeHeight(Location loc) {
        World world = loc.getWorld();
        Biome biome = loc.getBlock().getBiome();
        weather = Weather.match(biome, world.hasStorm(), world.isThundering());
        time = world.getTime();
        height = loc.getBlockY();
    }

    private void setLocationWorldBiomeLight(Block block) {
        location = block.getLocation();
        world = block.getWorld();
        biome = block.getBiome();
        if (block.getType().isTransparent()) {
            lightLevel = block.getLightLevel();
        } else { // look for an air block around
            byte maxLight = 0;
            for (BlockFace face : BlockFace.values()) {
                if (block.getRelative(face).getType().isTransparent()) {
                    byte currentLevel = block.getRelative(face).getLightLevel();
                    if (currentLevel > maxLight)
                        maxLight = currentLevel;
                }
            }
            lightLevel = maxLight;
        }
    }

    private void setLocationWorldBiomeLight(Entity e) {
        location = e.getLocation();
        world = e.getWorld();
        biome = world.getBiome(location.getBlockX(), location.getBlockZ());
        lightLevel = world.getBlockAt(location).getLightLevel();
    }

    private void setLocationWorldBiomeLight(Target targ) {
        location = targ.getLocation();
        world = location.getWorld();
        biome = world.getBiome(location.getBlockX(), location.getBlockZ());
        lightLevel = world.getBlockAt(location).getLightLevel();
    }

    private void setRegions() {
        regions = new HashSet<String>();
        if (!Dependencies.hasWorldGuard())
            return;
        Map<String, ProtectedRegion> regionMap = Dependencies.getWorldGuard().getRegionContainer().get(world).getRegions();
        Vector vec = new Vector(location.getX(), location.getY(),
                location.getZ());
        for (String region : regionMap.keySet()) {
            if (regionMap.get(region).contains(vec))
                regions.add(region.toLowerCase()); // note: region needs to be
                                                   // lowercase for case
                                                   // insensitive matches
        }
    }

    private void setTool(DamageCause cause) {
        tool = new EnvironmentAgent(cause);
    }

    private void setTool(Agent agent) throws DropCreateException {
        if (agent.getClass().isAnnotationPresent(ConfigOnly.class)) {
            ConfigOnly annotate = agent.getClass().getAnnotation(
                    ConfigOnly.class);
            throw new DropCreateException(agent.getClass(), annotate.value());
        }
        tool = agent;
    }

    private void setTool(Entity damager) {
        if (damager instanceof Player)
            tool = new PlayerSubject((Player) damager);
        else if (damager instanceof Projectile)
            tool = new ProjectileAgent((Projectile) damager);
        else if (damager instanceof LightningStrike)
            // TODO: Is there any use in passing the lightning entity through
            // here?
            tool = new EnvironmentAgent(DamageCause.LIGHTNING);
        else if (damager instanceof LivingEntity)
            tool = new CreatureSubject(damager);
        else if (damager instanceof Explosive)
            tool = new ExplosionAgent(damager);
    }

    private void setTool(EntityDamageEvent lastDamage) {
        // This is for EntityDeathEvent
        // Check if the damager is a player - if so, weapon is the held tool
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) lastDamage;
            if (e.getDamager() instanceof Player) {
                tool = new PlayerSubject((Player) e.getDamager());
                return;
            } else if (e.getDamager() instanceof Projectile) {
                tool = new ProjectileAgent((Projectile) e.getDamager());
                return;
            } else if (e.getDamager() instanceof LivingEntity) {
                tool = new CreatureSubject(e.getDamager());
                return;
            } else {
                // The only other one I can think of is lightning, which would
                // be covered by the non-entity code
                // But just in case, log it.
                Log.logInfo("A "
                        + lastDamage.getEntity().getClass().getSimpleName()
                        + " was damaged by a "
                        + e.getDamager().getClass().getSimpleName(), HIGHEST);
            }
        }
        // Damager was not a person - check damage types
        DamageCause cause = lastDamage.getCause();
        // if(cause == DamageCause.CUSTOM) return; // We don't handle custom
        // damage // Zar: actually, probably preferable to actually handle
        // custom
        // Used to ignore void damage as well, but since events were added I can
        // see some use for it.
        // For example, a lightning strike when someone falls off the bottom of
        // the map.
        tool = new EnvironmentAgent(cause);
    }

    private static Target getEntityTarget(Entity what) {
        if (what instanceof Player)
            return new PlayerSubject((Player) what);
        else if (what instanceof LivingEntity)
            return new CreatureSubject(what);
        else if (what instanceof Vehicle)
            return new VehicleTarget((Vehicle) what);
        else if (what instanceof Painting)
            return new VehicleTarget((Painting) what);
        else if (what instanceof FallingBlock)
            return new BlockTarget((FallingBlock) what);
        else if (what instanceof Fireball)
            return null; // TODO: do we need to do anything here? This is a
                         // fireball dying, getting hurt or being interacted
                         // with?
        else if (what instanceof EnderDragonPart)
            return new CreatureSubject(((ComplexEntityPart) what).getParent());
        else
            return new CreatureSubject(what);
        /*
         * else if(what instanceof EnderCrystal) return null; // TODO: allow
         * ender crystal targets (change creaturesubject to entitysubject?) else
         * if(what instanceof ItemFrame) return null; else if(what instanceof
         * TNTPrimed) return null; else if(what instanceof Arrow) return null;
         * else if(what instanceof Item) return null;
         */
        // Log.logWarning("Error: unknown entity target ("+what.getClass().toString()+") - please let the developer know.",
        // Verbosity.HIGH);
        // return null; // Ideally this return is unreachable
    }

    // Accessors
    /**
     * @return The agent that caused this event.
     */
    public Agent getTool() {
        return tool;
    }

    /**
     * @return The location at which the event occurred.
     */
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location newLocation) {
        location = newLocation;
    }

    /**
     * @return The world in which the event occurred.
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return The set of WorldGuard regions that contain the location of the
     *         event.
     */
    public Set<String> getRegions() {
        return regions;
    }

    /**
     * @return The weather conditions at the time of the event.
     */
    public Weather getWeather() {
        return weather;
    }

    /**
     * @return The block face that was hit, if applicable, or null otherwise.
     */
    public BlockFace getFace() {
        return face;
    }

    /**
     * @return The biome in which the event occurred.
     */
    public Biome getBiome() {
        return biome;
    }

    /**
     * @return The (in-game) time of day at which the event occurred.
     */
    public long getTime() {
        return time;
    }

    /**
     * @return The height above bedrock at which the event occurred.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The distance the agent was from the target at the time of the
     *         event.
     */
    public double getAttackRange() {
        return attackRange;
    }

    /**
     * @return The light level at the location of the event when it occurred.
     */
    public int getLightLevel() {
        return lightLevel;
    }

    // Matching!
    @Override
    public boolean matches(AbstractDropEvent other) {
        if (other instanceof OccurredEvent) {
            return equals(other);
        } else if (other instanceof CustomDrop
                || other instanceof GroupDropEvent) {
            return other.matches(this);
        }
        return false;
    }

    @Override
    public String getLogMessage() {
        // TODO: Hm, how should this log message go? It would be used if you
        // were logging actual event firing
        return getTrigger().toString()
                + " on "
                + ((getTarget() == null) ? "<no block>" : getTarget()
                        .toString()
                        + " with "
                        + ((getTool() == null) ? "<no tool> " : getTool()
                                .toString()));
    }

    @Override
    public boolean isCancelled() {
        if (event != null)
            return event.isCancelled();
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (event != null)
            event.setCancelled(cancel);
    }

    public Cancellable getEvent() {
        return this.event;
    }

    private static double measureRange(Location fromLoc, Location toLoc, String onError) {
        if (toLoc == null)
            return 0;
        if (fromLoc == null) {
            Log.logWarning(
                    "OccuredEvent.measureRange: location is null, this should never happen! (please advise developer)."
                            + onError, Verbosity.NORMAL);
            return 0;
        }
        if (fromLoc.getWorld() != toLoc.getWorld()) {
            Log.logWarning("OccuredEvent.measureRange: worlds ("
                    + fromLoc.getWorld().toString() + ", "
                    + toLoc.getWorld().toString()
                    + ") do not match - perhaps another plugin intervened."
                    + onError, Verbosity.HIGH);
        } else {
            return fromLoc.distance(toLoc);
        }
        return 0;
    }

    public void setRealEvent(Event realEvent) {
        this.realEvent = realEvent;
    }

    public Event getRealEvent() {
        return realEvent;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    public boolean isDenied() {
        return denied;
    }

    public void setCustomDropAmount(double amount) {
        customDropAmount = amount;
    }

    public double getCustomDropAmount() {
        return customDropAmount;
    }

    public Player getPlayerAttacker() {
        if (getTool() instanceof PlayerSubject)
            return ((PlayerSubject) getTool()).getPlayer();
        else if (getTool() instanceof ProjectileAgent) {
            if (((ProjectileAgent) getTool()).getShooter() instanceof PlayerSubject)
                return (Player) ((ProjectileAgent) getTool()).getShooter()
                        .getEntity();
        }

        return null;
    }

    public LivingEntity getAttacker() {
        if (getTool() instanceof PlayerSubject)
            return ((PlayerSubject) getTool()).getPlayer();
        else if (getTool() instanceof CreatureSubject) {
            Entity ent = ((CreatureSubject) getTool()).getEntity();
            if (ent instanceof LivingEntity)
                return (LivingEntity) ent;
        } else if (getTool() instanceof ProjectileAgent) {
            if (((ProjectileAgent) getTool()).getShooter() == null)
            	return null;
            else if(((ProjectileAgent) getTool()).getShooter().getEntity() instanceof LivingEntity)
            	return (LivingEntity) ((ProjectileAgent) getTool()).getShooter().getEntity();
        }
        return null;
    }

    public LivingEntity getVictim() {
        if (this.getTarget() instanceof CreatureSubject) {
            Entity ent = ((CreatureSubject) this.getTarget()).getEntity();
            if (ent instanceof LivingEntity) {
                return (LivingEntity) ent;
            }
        } else if (this.getTarget() instanceof PlayerSubject) {
            return ((PlayerSubject) getTarget()).getPlayer();
        }

        return null;
    }

    public Player getPlayerVictim() {
        if (getTarget() instanceof PlayerSubject)
            return ((PlayerSubject) getTarget()).getPlayer();
        else
            return null;
    }

    public boolean isOverrideDefault() {
        return overrideDefault;
    }

    public void setOverrideDefault(boolean overrideDefault) {
        this.overrideDefault = overrideDefault;
    }

    public boolean isOverrideDefaultXp() {
        return overrideDefaultXp;
    }

    public void setOverrideDefaultXp(boolean overrideDefaultXp) {
        this.overrideDefaultXp = overrideDefaultXp;
    }

    public void setReplaceBlockWith(BlockTarget tempReplace) {
        replaceBlockWith = tempReplace;

    }

    public BlockTarget getReplaceBlockWith() {
        return replaceBlockWith;

    }

    public boolean isOverrideEquipment() {
        return this.overrideEquipment;
    }

    public void setOverrideEquipment(boolean equip) {
        this.overrideEquipment = equip;
    }

    public String getSpawnedReason() {
        return spawnedReason;
    }

    public void setSpawnedReason(String spawnedReason) {
        this.spawnedReason = spawnedReason;
    }

    /** FIXME: Yes, this is a hack until I find a better way to pass
     *  victim names etc to variable parsing from item custom names
     * @return
     */
    public String getVictimName() {
        if (getTarget() instanceof PlayerSubject) {
            return ((PlayerSubject) getTarget()).getPlayer().getName();
        } else if (this.getTarget() instanceof CreatureSubject) {
            return ((CreatureSubject) this.getTarget()).getReadableName();
        }
        return "";
    }

}
