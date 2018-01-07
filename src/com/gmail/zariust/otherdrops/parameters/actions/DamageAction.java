package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.EntityWrapper;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.parameters.Action;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;

public class DamageAction extends Action {
    // "potioneffect: "
    // message.player, message.radius@<r>, message.world, message.server
    public enum DamageActionType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER, TOOL
    }

    public enum DamageType {
        NORMAL, FIRE, LIGHTNING
    }

    static Map<String, DamageActionType>    matches = new HashMap<String, DamageActionType>();
    static {
        matches.put("damage", DamageActionType.ATTACKER);
        matches.put("damageattacker", DamageActionType.ATTACKER);
        matches.put("damage.attacker", DamageActionType.ATTACKER);
        matches.put("damage.victim", DamageActionType.VICTIM);
        matches.put("damage.server", DamageActionType.SERVER);
        matches.put("damage.world", DamageActionType.WORLD);
        matches.put("damage.global", DamageActionType.SERVER);
        matches.put("damage.all", DamageActionType.SERVER);
        matches.put("damage.radius", DamageActionType.RADIUS);

        // Can't do tooldamage yet - need a way to damage tools by "1" if a
        // block break
        // event and this condition hasn't run.

        // matches.put("damage.tool", DamageActionType.TOOL);
        // matches.put("damagetool", DamageActionType.TOOL);
    }

    protected DamageActionType              damageActionType;
    protected double                        radius  = OtherDropsConfig.gActionRadius;
    private final Map<DoubleRange, DamageType> damages;                                          // this
                                                                                               // can
                                                                                               // contain
                                                                                               // variables,
                                                                                               // parse
                                                                                               // at
                                                                                               // runtime

    public DamageAction(Object object, DamageActionType damageEffectType2) {
        damageActionType = damageEffectType2;
        damages = new HashMap<DoubleRange, DamageType>();

        if (object instanceof List) {
            // TODO: support lists?
            @SuppressWarnings("unchecked")
            List<Object> stringList = (List<Object>) object;
            for (Object sub : stringList) {
                if (sub instanceof String)
                    parseDamage((String) sub);
                else if (sub instanceof Integer)
                    parseDamage(String.valueOf(sub));
            }
        } else if (object instanceof String) {
            parseDamage((String) object);
        } else if (object instanceof Integer) {
            parseDamage(String.valueOf(object));
        }
    }

    private void parseDamage(String sub) {
        DoubleRange value = DoubleRange.parse("0");
        DamageType type = DamageType.NORMAL;

        if (sub.matches("(?i)fire.*")) {
            type = DamageType.FIRE;
            String[] split = sub.split("@");
            if (split.length > 1)
                value = DoubleRange.parse(split[1]);
            else
                value = DoubleRange.parse("60"); // default to 60 ticks (3 seconds)
        } else if (sub.matches("(?i)lightning.*")) {
            type = DamageType.LIGHTNING;
            String[] split = sub.split("@");
            if (split.length > 1)
                value = DoubleRange.parse(split[1]);
            // default of 0 (harmless lightning) is ok.
        } else
            value = DoubleRange.parse(sub);

        damages.put(value, type);
    }

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {
        if (damages != null) {
            for (DoubleRange key : damages.keySet()) {
                processDamage(drop, occurence, key, damages.get(key));
            }
        }

        return false;
    }

    private void processDamage(CustomDrop drop, OccurredEvent occurence,
            DoubleRange damageRange, DamageType damageType) {

        switch (damageActionType) {
        case ATTACKER:
            if (occurence.getPlayerAttacker() != null) {
                damage(occurence.getPlayerAttacker(), damageRange, damageType,
                        drop, null);
            }
            break;
        case VICTIM:
            if (occurence.getPlayerVictim() != null)
                damage(occurence.getPlayerVictim(), damageRange, damageType,
                        drop, occurence.getAttacker());
            else if (occurence.getTarget() instanceof CreatureSubject) {
                Entity ent = ((CreatureSubject) occurence.getTarget())
                        .getEntity();
                if (ent instanceof LivingEntity) {
                    damage((LivingEntity) ent, damageRange, damageType, drop,
                            occurence.getAttacker());
                }
            }

            break;
        case RADIUS:
            // occurence.getLocation().getRadiusPlayers()? - how do we get
            // players around radius without an entity?
            Location loc = occurence.getLocation();
            for (Player player : loc.getWorld().getPlayers()) {
                if (player.getLocation().getX() > (loc.getX() - radius)
                        || player.getLocation().getX() < (loc.getX() + radius))
                    if (player.getLocation().getY() > (loc.getY() - radius)
                            || player.getLocation().getY() < (loc.getY() + radius))
                        if (player.getLocation().getZ() > (loc.getZ() - radius)
                                || player.getLocation().getZ() < (loc.getZ() + radius))
                            damage(player, damageRange, damageType, drop,
                                    occurence.getAttacker());
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                damage(player, damageRange, damageType, drop,
                        occurence.getAttacker());
            }
            break;
        case WORLD:
            for (Player player : occurence.getLocation().getWorld()
                    .getPlayers()) {
                damage(player, damageRange, damageType, drop,
                        occurence.getAttacker());
            }
            break;
        case TOOL:
            // not yet supported, as default damage of 1 needs to be done in the
            // main DropRunner.run() method
            break;
        default:
            break;
        }

    }

    private void damage(LivingEntity ent, DoubleRange damageRange,
            DamageType damageType, CustomDrop drop, LivingEntity attacker) {
        Double damageVal = damageRange.getRandomIn(OtherDrops.rng);
        Log.logInfo("Damaging entity: " + ent.toString() + " range="
                + damageRange.toString() + " value=" + damageVal + " ("
                + damageType.toString() + ")", Verbosity.HIGHEST);
        switch (damageType) {
        case NORMAL:
            if (damageVal < 0) {
                double newHealth = ent.getHealth() + (damageVal * -1);
                if (newHealth > ent.getMaxHealth())
                    newHealth = ent.getMaxHealth();
                EntityWrapper.setHealth(ent, newHealth);
            } else if (damageVal > 0) {
                if (attacker != null) {
                    Log.logInfo("Attacker found, " + attacker.toString(),
                            Verbosity.HIGH);
                    EntityWrapper.damage(ent, damageVal, attacker);
                } else {
                    EntityWrapper.damage(ent, damageVal);
                }
            }
            break;
        case FIRE:
            ent.setFireTicks((int) Math.round(damageVal));
            break;
        case LIGHTNING:
            Location location = ent.getLocation().clone();
            if (drop instanceof SimpleDrop)
                location = ((SimpleDrop) drop).getRandomisedLocation(location);
            World world = location.getWorld();

            if (damageVal == 0)
                world.strikeLightningEffect(location);
            else
                world.strikeLightning(location);

            break;
        }

    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null)
                actions.add(new DamageAction(parseMe.get(key), matches.get(key)));
        }

        return actions;
    }

}
