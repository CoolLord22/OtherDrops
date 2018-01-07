package com.gmail.zariust.otherdrops.subject;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.junit.Test;

public class ExplosionAgentTest {

    // TODO: test EXPLOSION_CREEPER@POWERED

    @Test
    public void testAny() {
        String configString = "EXPLOSION_ANY";

        // Test by Material name
        ExplosionAgent agent = new ExplosionAgent();
        assertTrue("Explosion is: " + agent.toString() + " should be "
                + configString, agent.toString().equalsIgnoreCase(configString));

        // Test by parsing
        Agent agent2 = ExplosionAgent.parse(configString, "");
        // assertTrue("agent.isCreature() failed", agent.isCreature()); //
        // creaturetype will not be a creature
        assertTrue(agent2.toString() + " should be " + configString, agent2
                .toString().equalsIgnoreCase(configString));
    }

    @Test
    public void testMatchCreepers() {
        // Test by parsing
        Agent agent2 = ExplosionAgent.parse("EXPLOSION_CREEPER", "");
        // assertTrue("agent.isCreature() failed", agent.isCreature()); //
        // creaturetype will not be a creature
        assertTrue(agent2.toString() + " should be EXPLOSION_CREEPER.", agent2
                .toString().equalsIgnoreCase("EXPLOSION_CREEPER"));

        // Then test CreatureType constructor
        ExplosionAgent agent = new ExplosionAgent(EntityType.CREEPER);
        assertTrue(agent.toString() + " should be EXPLOSION_CREEPER.", agent
                .toString().equalsIgnoreCase("EXPLOSION_CREEPER"));

        // Create a "creeper entity" as would happen in a explosion event and
        // make sure it matches the config agent
        ExplosionAgent eventAgent = new ExplosionAgent(getCreeperTestEntity());
        assertTrue(
                "Config agent (" + agent2.toString()
                        + ") doesn't match explosion by creeper ("
                        + eventAgent.toString() + ")",
                agent2.matches(eventAgent));
    }

    @Test
    public void testMatchTnt() {
        String configString = "EXPLOSION_TNT";

        // Test by Material constructor
        ExplosionAgent agent = new ExplosionAgent(Material.TNT);
        assertTrue("Explosion is: " + agent.toString() + " should be "
                + configString, agent.toString().equalsIgnoreCase(configString));

        // Test by parsing
        Agent configAgent = ExplosionAgent.parse(configString, "");

        Entity eventEntity = getTntPrimedTestEntity();
        ExplosionAgent eventAgent = new ExplosionAgent(eventEntity);

        assertTrue(configAgent.toString() + " should be " + configString,
                configAgent.toString().equalsIgnoreCase(configString));
        assertTrue(eventAgent.toString() + " should be " + configString,
                eventAgent.toString().equalsIgnoreCase(configString));

        assertTrue(configAgent.matches(eventAgent));
    }

    @Test
    public void testTntMatchesAny() {
        String configString = "EXPLOSION_ANY";

        // Test by parsing
        Agent configAgent = ExplosionAgent.parse(configString, "");

        Entity eventEntity = getTntPrimedTestEntity();
        ExplosionAgent eventAgent = new ExplosionAgent(eventEntity);

        assertTrue(configAgent.toString() + " should be " + configString,
                configAgent.toString().equalsIgnoreCase(configString));

        assertTrue(eventAgent.toString() + " should be " + "EXPLOSION_TNT",
                eventAgent.toString().equalsIgnoreCase("EXPLOSION_TNT"));

        assertTrue(configAgent.matches(eventAgent));
    }

    @Test
    public void testCreeperMatchesAny() {
        // Test by parsing
        String configString = "EXPLOSION_ANY";
        Agent configAgent = ExplosionAgent.parse(configString, "");

        Entity eventEntity = getCreeperTestEntity();
        ExplosionAgent eventAgent = new ExplosionAgent(eventEntity);

        assertTrue(configAgent.toString() + " should be " + configString,
                configAgent.toString().equalsIgnoreCase(configString));

        assertTrue(eventAgent.toString() + " should be " + "EXPLOSION_CREEPER",
                eventAgent.toString().equalsIgnoreCase("EXPLOSION_CREEPER"));

        assertTrue(configAgent.matches(eventAgent));
    }

    private Entity getTntPrimedTestEntity() {
        // TODO Auto-generated method stub
        return new TNTPrimed() {

            @Override
            public int getFuseTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public void setFuseTicks(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public float getYield() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public boolean isIncendiary() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void setIsIncendiary(boolean arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setYield(float arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean eject() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public int getEntityId() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public float getFallDistance() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getFireTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public EntityDamageEvent getLastDamageCause() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Location getLocation() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getMaxFireTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public List<Entity> getNearbyEntities(double arg0, double arg1,
                    double arg2) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Entity getPassenger() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Server getServer() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getTicksLived() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public EntityType getType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public UUID getUniqueId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Entity getVehicle() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Vector getVelocity() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public World getWorld() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isDead() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isEmpty() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isInsideVehicle() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean leaveVehicle() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void playEffect(EntityEffect arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void remove() {
                // TODO Auto-generated method stub

            }

            @Override
            public void setFallDistance(float arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setFireTicks(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setLastDamageCause(EntityDamageEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean setPassenger(Entity arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void setTicksLived(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setVelocity(Vector arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean teleport(Location arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean teleport(Entity arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean teleport(Location arg0, TeleportCause arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean teleport(Entity arg0, TeleportCause arg1) {
                // TODO Auto-generated method stub
                return false;
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
            public boolean isOnGround() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Entity getSource() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    private Entity getCreeperTestEntity() {
        // TODO Auto-generated method stub
        return new Creeper() {

            @Override
            public boolean isPowered() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void setPowered(boolean arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public LivingEntity getTarget() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setTarget(LivingEntity arg0) {
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
            public double getEyeHeight() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public double getEyeHeight(boolean arg0) {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Location getEyeLocation() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Player getKiller() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0,
                    int arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getMaximumAir() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getMaximumNoDamageTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getNoDamageTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getRemainingAir() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Block getTargetBlock(HashSet<Byte> arg0, int arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean hasPotionEffect(PotionEffectType arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public <T extends Projectile> T launchProjectile(
                    Class<? extends T> arg0) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void removePotionEffect(PotionEffectType arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setMaximumAir(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setMaximumNoDamageTicks(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setNoDamageTicks(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setRemainingAir(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public Arrow shootArrow() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Egg throwEgg() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Snowball throwSnowball() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean eject() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public int getEntityId() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public float getFallDistance() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getFireTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public EntityDamageEvent getLastDamageCause() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Location getLocation() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getMaxFireTicks() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public List<Entity> getNearbyEntities(double arg0, double arg1,
                    double arg2) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Entity getPassenger() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Server getServer() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int getTicksLived() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public EntityType getType() {
                // TODO Auto-generated method stub
                return EntityType.CREEPER;
            }

            @Override
            public UUID getUniqueId() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Entity getVehicle() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Vector getVelocity() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public World getWorld() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isDead() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isEmpty() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isInsideVehicle() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean leaveVehicle() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void playEffect(EntityEffect arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void remove() {
                // TODO Auto-generated method stub

            }

            @Override
            public void setFallDistance(float arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setFireTicks(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setLastDamageCause(EntityDamageEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean setPassenger(Entity arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void setTicksLived(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setVelocity(Vector arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean teleport(Location arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean teleport(Entity arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean teleport(Location arg0, TeleportCause arg1) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean teleport(Entity arg0, TeleportCause arg1) {
                // TODO Auto-generated method stub
                return false;
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
            public boolean isOnGround() {
                // TODO Auto-generated method stub
                return false;
            }

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
        };
    }
}
