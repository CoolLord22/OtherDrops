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

package com.gmail.zariust.otherdrops;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.SoundCategory;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class PlayerWrapper implements Player {
    private final Player               caller;
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final boolean              suppress, override;

    public PlayerWrapper(Player player, boolean opOverride,
            boolean suppressMessages) {
        this.caller = player;
        this.suppress = suppressMessages;
        this.override = opOverride;
    }

    // OtherDrops code
    private CommandSender getSender() {
        return suppress ? console : caller;
    }

    // OtherDrops code
    private Permissible getPermissible() {
        return override ? console : caller;
    }

    @Override
    public boolean isOp() {
        return getPermissible().isOp();
    }

    @Override
    // TODO: Could returning null cause issues?
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    // Special case for time-limited permissions; always go to the caller
    public PermissionAttachment addAttachment(Plugin plugin, int time) {
        return caller.addAttachment(plugin, time);
    }

    @Override
    // TODO: Could returning null cause issues?
    public PermissionAttachment addAttachment(Plugin plugin, String perm,
            boolean val) {
        return null;
    }

    @Override
    // Special case for time-limited permissions; always go to the caller
    public PermissionAttachment addAttachment(Plugin plugin, String perm,
            boolean val, int time) {
        return caller.addAttachment(plugin, perm, val, time);
    }

    // OtherDrops code
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getPermissible().getEffectivePermissions();
    }

    // OtherDrops code
    @Override
    public boolean hasPermission(String perm) {
        return getPermissible().hasPermission(perm);
    }

    // OtherDrops code
    @Override
    public boolean hasPermission(Permission perm) {
        return getPermissible().hasPermission(perm);
    }

    // OtherDrops code
    @Override
    public boolean isPermissionSet(String perm) {
        return getPermissible().isPermissionSet(perm);
    }

    // OtherDrops code
    @Override
    public boolean isPermissionSet(Permission perm) {
        return getPermissible().isPermissionSet(perm);
    }

    // OtherDrops code
    @Override
    public void recalculatePermissions() {
        getPermissible().recalculatePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment attached) {
    }

    // OtherDrops code
    @Override
    public void setOp(boolean is) {
        getPermissible().setOp(is);
    }

    // CommandSender methods; getName() may not be declared in CommandSender,
    // but it's used for any CommandSender that actually defines it
    @Override
    public String getName() {
        return caller.getName();
    }

    @Override
    public Server getServer() {
        return caller.getServer();
    }

    // OtherDrops code
    @Override
    public void sendMessage(String msg) {
        getSender().sendMessage(msg);
    }

    // Player, HumanEntity, LivingEntity, Entity methods... ugh, there are so
    // many of these...
    @Override
    public PlayerInventory getInventory() {
        return caller.getInventory();
    }

    @Override
    public ItemStack getItemInHand() {
        return caller.getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        caller.setItemInHand(item);
    }

    @Override
    public boolean isSleeping() {
        return caller.isSleeping();
    }

    @Override
    public int getSleepTicks() {
        return caller.getSleepTicks();
    }

    @Override
    public double getEyeHeight() {
        return caller.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        return caller.getEyeHeight(ignoreSneaking);
    }

    @Override
    public Location getEyeLocation() {
        return caller.getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return caller.getLineOfSight(transparent, maxDistance);
    }

    @Override
    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        return caller.getTargetBlock(transparent, maxDistance);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent,
            int maxDistance) {
        return caller.getLastTwoTargetBlocks(transparent, maxDistance);
    }
    
    @Override
    public boolean isInsideVehicle() {
        return caller.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        return caller.leaveVehicle();
    }

    @Override
    public int getRemainingAir() {
        return caller.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int ticks) {
        caller.setRemainingAir(ticks);
    }

    @Override
    public int getMaximumAir() {
        return caller.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int ticks) {
        caller.setMaximumAir(ticks);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return caller.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
        caller.setMaximumNoDamageTicks(ticks);
    }

    @Override
    public int getNoDamageTicks() {
        return caller.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        caller.setNoDamageTicks(ticks);
    }

    @Override
    public Location getLocation() {
        return caller.getLocation();
    }

    @Override
    public void setVelocity(Vector velocity) {
        caller.setVelocity(velocity);
    }

    @Override
    public Vector getVelocity() {
        return caller.getVelocity();
    }

    @Override
    public World getWorld() {
        return caller.getWorld();
    }

    @Override
    public boolean teleport(Location location) {
        return caller.teleport(location);
    }

    @Override
    public boolean teleport(Entity destination) {
        return caller.teleport(destination);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        return caller.getNearbyEntities(x, y, z);
    }

    @Override
    public int getEntityId() {
        return caller.getEntityId();
    }

    @Override
    public int getFireTicks() {
        return caller.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        return caller.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int ticks) {
        caller.setFireTicks(ticks);
    }

    @Override
    public void remove() {
        caller.remove();
    }

    @Override
    public boolean isDead() {
        return caller.isDead();
    }

    @Override
    public Entity getPassenger() {
        return caller.getPassenger();
    }

    @Override
    public boolean setPassenger(Entity passenger) {
        return caller.setPassenger(passenger);
    }

    @Override
    public boolean isEmpty() {
        return caller.isEmpty();
    }

    @Override
    public boolean eject() {
        return caller.eject();
    }

    @Override
    public float getFallDistance() {
        return caller.getFallDistance();
    }

    @Override
    public void setFallDistance(float distance) {
        caller.setFallDistance(distance);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        caller.setLastDamageCause(event);
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return caller.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId() {
        return caller.getUniqueId();
    }

    @Override
    public boolean isOnline() {
        return caller.isOnline();
    }

    @Override
    public String getDisplayName() {
        return caller.getDisplayName();
    }

    @Override
    public void setDisplayName(String name) {
        caller.setDisplayName(name);
    }

    @Override
    public void setCompassTarget(Location loc) {
        caller.setCompassTarget(loc);
    }

    @Override
    public Location getCompassTarget() {
        return caller.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress() {
        return caller.getAddress();
    }

    @Override
    // TODO: What on earth does this even do? Should it be sent to the console
    // if suppress is true?
    public void sendRawMessage(String message) {
        caller.sendRawMessage(message);
    }

    @Override
    public void kickPlayer(String message) {
        caller.kickPlayer(message);
    }

    @Override
    public void chat(String msg) {
        caller.chat(msg);
    }

    @Override
    public boolean performCommand(String command) {
        return caller.performCommand(command);
    }

    @Override
    public boolean isSneaking() {
        return caller.isSneaking();
    }

    @Override
    public void setSneaking(boolean sneak) {
        caller.setSneaking(sneak);
    }

    @Override
    public void saveData() {
        caller.saveData();
    }

    @Override
    public void loadData() {
        caller.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        caller.setSleepingIgnored(isSleeping);
    }

    @Override
    public boolean isSleepingIgnored() {
        return caller.isSleepingIgnored();
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
        caller.playNote(loc, instrument, note);
    }

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        caller.playNote(loc, instrument, note);
    }

    @Override
    public void playEffect(Location loc, Effect effect, int data) {
        caller.playEffect(loc, effect, data);
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
        caller.sendBlockChange(loc, material, data);
    }

    @Override
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz,
            byte[] data) {
        return caller.sendChunkChange(loc, sx, sy, sz, data);
    }

    @Override
    public void sendBlockChange(Location loc, int material, byte data) {
        caller.sendBlockChange(loc, material, data);
    }

    @Override
    public void sendMap(MapView map) {
        caller.sendMap(map);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void updateInventory() {
        caller.updateInventory();
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        caller.awardAchievement(achievement);
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        caller.incrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        caller.incrementStatistic(statistic, amount);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        caller.incrementStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material,
            int amount) {
        caller.incrementStatistic(statistic, material, amount);
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        caller.setPlayerTime(time, relative);
    }

    @Override
    public long getPlayerTime() {
        return caller.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset() {
        return caller.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return caller.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime() {
        caller.resetPlayerTime();
    }

    @Override
    public GameMode getGameMode() {
        return caller.getGameMode();
    }

    @Override
    public void setGameMode(GameMode mode) {
        caller.setGameMode(mode);
    }

    @Override
    public boolean isBanned() {
        return caller.isBanned();
    }

    @Override
    public boolean isWhitelisted() {
        return caller.isWhitelisted();
    }

    @Override
    public void setBanned(boolean ban) {
        caller.setBanned(ban);
    }

    @Override
    public void setWhitelisted(boolean wl) {
        caller.setWhitelisted(wl);
    }

    @Override
    public float getExhaustion() {
        return caller.getExhaustion();
    }

    @Override
    public int getFoodLevel() {
        return caller.getFoodLevel();
    }

    @Override
    public int getLevel() {
        return caller.getLevel();
    }

    @Override
    public float getSaturation() {
        return caller.getSaturation();
    }

    @Override
    public int getTotalExperience() {
        return caller.getTotalExperience();
    }

    @Override
    public void setExhaustion(float exhaustion) {
        caller.setExhaustion(exhaustion);
    }

    @Override
    public void setFoodLevel(int food) {
        caller.setFoodLevel(food);
    }

    @Override
    public void setLevel(int lvl) {
        caller.setLevel(lvl);
    }

    @Override
    public void setSaturation(float saturation) {
        caller.setSaturation(saturation);
    }

    @Override
    public void setTotalExperience(int xp) {
        caller.setTotalExperience(xp);
    }

    @Override
    public Location getBedSpawnLocation() {
        return caller.getBedSpawnLocation();
    }

    @Override
    public boolean isSprinting() {
        return caller.isSprinting();
    }

    @Override
    public void setSprinting(boolean run) {
        caller.setSprinting(run);
    }

    @Override
    public int getTicksLived() {
        return caller.getTicksLived();
    }

    @Override
    public void setTicksLived(int value) {
        caller.setTicksLived(value);
    }

    @Override
    public Player getPlayer() {
        return caller.getPlayer();
    }

    @Override
    public Map<String, Object> serialize() {
        return caller.serialize();
    }

    @Override
    public String getPlayerListName() {
        return caller.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String name) {
        caller.setPlayerListName(name);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        return caller.teleport(location, cause);
    }

    @Override
    public boolean teleport(Entity destination, TeleportCause cause) {
        return caller.teleport(destination, cause);
    }

    @Override
    public void giveExp(int amount) {
        caller.giveExp(amount);
    }

    @Override
    public float getExp() {
        return caller.getExp();
    }

    @Override
    public void setExp(float exp) {
        caller.setExp(exp);
    }

    @Override
    public Player getKiller() {
        return caller.getKiller();
    }

    @Override
    public long getFirstPlayed() {
        return caller.getFirstPlayed();
    }

    @Override
    public long getLastPlayed() {
        return caller.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore() {
        return caller.hasPlayedBefore();
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean getAllowFlight() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setAllowFlight(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playEffect(EntityEffect arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean canSee(Player arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void hidePlayer(Player arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBedSpawnLocation(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void showPlayer(Player arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean addPotionEffect(PotionEffect arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removePotionEffect(PotionEffectType arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void closeInventory() {
        // TODO Auto-generated method stub

    }

    @Override
    public ItemStack getItemOnCursor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryView getOpenInventory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryView openEnchanting(Location arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InventoryView openInventory(Inventory arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void openInventory(InventoryView arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public InventoryView openWorkbench(Location arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setItemOnCursor(ItemStack arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean setWindowProperty(Property arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityType getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MetadataValue> getMetadata(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasMetadata(String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeMetadata(String arg0, Plugin arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMetadata(String arg0, MetadataValue arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void abandonConversation(Conversation arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void acceptConversationInput(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean beginConversation(Conversation arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConversing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void sendMessage(String[] arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public Entity getVehicle() {
        // TODO Auto-generated method stub
        return caller.getVehicle();
    }

    @Override
    public void abandonConversation(Conversation arg0,
            ConversationAbandonedEvent arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public Inventory getEnderChest() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getExpToLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isBlocking() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCanPickupItems() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EntityEquipment getEquipment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasLineOfSight(Entity arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCanPickupItems(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRemoveWhenFarAway(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public Location getLocation(Location arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void resetMaxHealth() {
        // TODO Auto-generated method stub

    }

    @Override
    public float getFlySpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getWalkSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void giveExpLevels(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFlying() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBedSpawnLocation(Location arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFlySpeed(float arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFlying(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    // for 1.4.6
    public void setTexturePack(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setWalkSpeed(float arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getCustomName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCustomNameVisible() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCustomName(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCustomNameVisible(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public WeatherType getPlayerWeather() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scoreboard getScoreboard() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Deprecated
    public boolean isOnGround() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void resetPlayerWeather() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPlayerWeather(WeatherType arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setScoreboard(Scoreboard arg0) throws IllegalArgumentException,
            IllegalStateException {
        // TODO Auto-generated method stub
        
    }

    // Minecraft 1.6.1 methods:

    @Override
    @Deprecated
    public int _INVALID_getLastDamage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Deprecated
    public void _INVALID_setLastDamage(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLastDamage(double arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    @Deprecated
    public void _INVALID_damage(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    @Deprecated
    public void _INVALID_damage(int arg0, Entity arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    @Deprecated
    public int _INVALID_getHealth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Deprecated
    public int _INVALID_getMaxHealth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    @Deprecated
    public void _INVALID_setHealth(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    @Deprecated
    public void _INVALID_setMaxHealth(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void damage(double arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void damage(double arg0, Entity arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHealth(double arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMaxHealth(double arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getLastDamage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getHealth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getMaxHealth() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isLeashed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setLeashHolder(Entity arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getHealthScale() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isHealthScaled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void playSound(Location arg0, String arg1, float arg2, float arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHealthScale(double arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setHealthScaled(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setResourcePack(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopSound(Sound sound) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopSound(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendSignChange(Location lctn, String[] strings) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAchievement(Achievement a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasAchievement(Achievement a) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void decrementStatistic(Statistic ststc) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void decrementStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatistic(Statistic ststc, int i) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStatistic(Statistic ststc) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void decrementStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStatistic(Statistic ststc, Material mtrl) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void decrementStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatistic(Statistic ststc, Material mtrl, int i) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void incrementStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void decrementStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getStatistic(Statistic ststc, EntityType et) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void incrementStatistic(Statistic ststc, EntityType et, int i) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void decrementStatistic(Statistic ststc, EntityType et, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatistic(Statistic ststc, EntityType et, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Entity getSpectatorTarget() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendTitle(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendTitle(String string, String string1, int int1, int int2, int int3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopSound(Sound sound, SoundCategory category) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopSound(String sound, SoundCategory category) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, T t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, T t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, T t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, T t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, Location lctn, int i, double d, double d1, double d2, double d3, T t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void spawnParticle(Particle prtcl, double d, double d1, double d2, int i, double d3, double d4, double d5, double d6, T t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Spigot spigot() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MainHand getMainHand() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InventoryView openMerchant(Villager vlgr, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public InventoryView openMerchant(Merchant merchant, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isGliding() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setGliding(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAI(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasAI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCollidable(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCollidable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AttributeInstance getAttribute(Attribute atrbt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setGlowing(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isGlowing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setInvulnerable(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInvulnerable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSilent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSilent(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasGravity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setGravity(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> type, Vector vector) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isHandRaised() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType pet) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getPortalCooldown() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPortalCooldown(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> getScoreboardTags() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addScoreboardTag(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeScoreboardTag(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}