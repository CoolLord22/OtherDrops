package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.parameters.Action;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;

public class ParticleAction extends Action {
    // "particleeffect: "
    // message.player, message.radius@<r>, message.world, message.server
    public enum ParticleEffectActionType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER, DROP
    }

    static Map<String, ParticleEffectActionType> matches = new HashMap<String, ParticleEffectActionType>();
    static {
        matches.put("particleeffect", ParticleEffectActionType.ATTACKER);
        matches.put("particleeffect.attacker", ParticleEffectActionType.ATTACKER);
        matches.put("particleeffect.victim", ParticleEffectActionType.VICTIM);
        matches.put("particleeffect.target", ParticleEffectActionType.VICTIM);
        matches.put("particleeffect.server", ParticleEffectActionType.SERVER);
        matches.put("particleeffect.world", ParticleEffectActionType.WORLD);
        matches.put("particleeffect.global", ParticleEffectActionType.SERVER);
        matches.put("particleeffect.all", ParticleEffectActionType.SERVER);
        matches.put("particleeffect.radius", ParticleEffectActionType.RADIUS);
        matches.put("particleeffect.drop", ParticleEffectActionType.DROP);

        matches.put("particleeffects", ParticleEffectActionType.ATTACKER);
        matches.put("particleeffects.attacker", ParticleEffectActionType.ATTACKER);
        matches.put("particleeffects.victim", ParticleEffectActionType.VICTIM);
        matches.put("particleeffects.target", ParticleEffectActionType.VICTIM);
        matches.put("particleeffects.server", ParticleEffectActionType.SERVER);
        matches.put("particleeffects.world", ParticleEffectActionType.WORLD);
        matches.put("particleeffects.global", ParticleEffectActionType.SERVER);
        matches.put("particleeffects.all", ParticleEffectActionType.SERVER);
        matches.put("particleeffects.radius", ParticleEffectActionType.RADIUS);
        matches.put("particleeffects.drop", ParticleEffectActionType.DROP);

    }

    protected ParticleEffectActionType           particleEffectActionType;
    protected double                           radius  = 10;                                           // default
                                                                                                        // to
                                                                                                        // 10
                                                                                                        // blocks

    private Collection<ParticleEffect>           effects = new ArrayList<ParticleEffect>();

    public ParticleAction(Collection<ParticleEffect> effectsList) {
        this.effects = effectsList;
    }

    public ParticleAction(Object object,
            ParticleEffectActionType particleEffectType2, boolean onlyRemove) {
        this.particleEffectActionType = particleEffectType2;

        if (object instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> stringList = (List<String>) object;
            for (String effect : stringList) {
                ParticleEffect singleEffect = getEffect(effect);
                if (singleEffect != null)
                    effects.add(singleEffect);
            }
        } else if (object instanceof String) {
            ParticleEffect singleEffect = getEffect((String) object);
            if (singleEffect != null)
                effects.add(singleEffect);
        }
    }

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {
        switch (particleEffectActionType) {
        case ATTACKER:
            if (occurence.getPlayerAttacker() != null & this.effects != null)
                applyEffect(occurence.getPlayerAttacker());
            return false;
        case VICTIM:
            if (occurence.getPlayerVictim() != null & this.effects != null)
                applyEffect(occurence.getPlayerVictim());
            else if (occurence.getTarget() instanceof CreatureSubject) {
                Entity ent = ((CreatureSubject) occurence.getTarget())
                        .getEntity();
                if (ent instanceof LivingEntity) {
                    applyEffect(((LivingEntity) ent));
                }
            }

            return false;

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
                            applyEffect(player);
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                applyEffect(player);
            }
            break;
        case WORLD:
            for (Player player : occurence.getLocation().getWorld()
                    .getPlayers()) {
                applyEffect(player);
            }
            break;
        case DROP:
            if (drop instanceof SimpleDrop) {
                applyEffect(occurence.getLocation());
            }
            break;
        default:
            break;
        }

        return false;
    }

    private void applyEffect(Location location) {
        for (ParticleEffect effect : this.effects) {
            try {
                Log.dMsg("Sending effect: "+effect.getName() + " speed: "+effect.speed+", count:"+effect.count);
                effect.sendToLocation(location, OtherDrops.rng.nextFloat(), OtherDrops.rng.nextFloat(), OtherDrops.rng.nextFloat());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void applyEffect(Entity lEnt) {
        for (ParticleEffect effect : this.effects) {
            try {
                Log.dMsg("Sending effect: "+effect.getName() + " speed: "+effect.speed+", count:"+effect.count);
                Location location = lEnt.getLocation();
                effect.sendToLocation(location, OtherDrops.rng.nextFloat(), OtherDrops.rng.nextFloat(), OtherDrops.rng.nextFloat());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            boolean onlyRemove;
            if (parseMe.get(key) != null) {
                onlyRemove = false;
                actions.add(new ParticleAction(parseMe.get(key),
                        matches.get(key), onlyRemove));
            }
        }
        return actions;
    }

    private static ParticleEffect getEffect(String effects) {
        String[] split = effects.split("@");
        float speed = 1f;
        int count = 1;

        try {
            if (split.length > 1)
                speed = Float.parseFloat(split[1]);
        } catch (NumberFormatException ex) {
            Log.logInfo("Particleeffect: invalid speed (" + split[1] + ")");
        }

        try {
            if (split.length > 2)
                count = Integer.parseInt(split[2]);
        } catch (NumberFormatException ex) {
            Log.logInfo("Particleeffect: invalid count (" + split[2] + ")");
        }

        ParticleEffect effect = ParticleEffect.fromName(split[0]);
        if (effect == null) {
            Log.logInfo("ParticleEffect: INVALID effect (" + split[0] + ")",
                    Verbosity.NORMAL);
            return null;
        }

        effect.speed = speed;
        effect.count = count;
        
        Log.logInfo("ParticleEffect: adding effect (" + split[0] + ", speed: "
                + speed + ", count: " + count + ")", Verbosity.HIGH);

        return effect;
    }

}
