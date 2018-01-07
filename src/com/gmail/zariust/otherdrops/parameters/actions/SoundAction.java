package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.CommonMaterial;
import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.parameters.Action;

public class SoundAction extends Action {
    public enum SoundLocation {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER, TOOL
    }

    static Map<String, SoundLocation> matches = new HashMap<String, SoundLocation>();
    static {
        String name = "sound";
        matches.put(name, SoundLocation.VICTIM); // default for a sound makes
                                                 // sense to be the target
        matches.put(name + ".attacker", SoundLocation.ATTACKER);
        matches.put(name + ".target", SoundLocation.VICTIM);
        matches.put(name + ".victim", SoundLocation.VICTIM);
        matches.put(name + ".server", SoundLocation.SERVER);
        matches.put(name + ".world", SoundLocation.WORLD);
        matches.put(name + ".global", SoundLocation.SERVER);
        matches.put(name + ".all", SoundLocation.SERVER);
        matches.put(name + ".radius", SoundLocation.RADIUS);
    }

    protected SoundLocation           damageActionType;
    protected double                  radius = OtherDropsConfig.gActionRadius;
    private final List<ODSound>       sounds;
    private boolean                   pickOne = false;

    private class ODSound {
        public ODSound(Sound sound2, DoubleRange volume2, DoubleRange pitch2) {
            this.sound = sound2;
            this.volume = volume2;
            this.pitch = pitch2;
        }

        Sound       sound;
        DoubleRange volume;
        DoubleRange pitch;
    }

    public SoundAction(Object object, SoundLocation damageEffectType2) {
        damageActionType = damageEffectType2;
        sounds = new ArrayList<SoundAction.ODSound>();

        if (object instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> stringList = (List<Object>) object;
            for (Object sub : stringList) {
                if (sub instanceof String)
                    parseValue((String) sub);
                else if (sub instanceof Integer)
                    parseValue(String.valueOf(sub));
            }
        } else if (object instanceof Map) {
            Log.dMsg("MAP detected");
            this.pickOne = true;
            @SuppressWarnings("unchecked")
            Map<Object, Object> stringList = (Map<Object, Object>) object;
            for (Object sub : stringList.keySet()) {
                if (sub instanceof String)
                    parseValue((String) sub);
                else if (sub instanceof Integer)
                    parseValue(String.valueOf(sub));
            }
        } else if (object instanceof String) {
            parseValue((String) object);
        }
    }

    private void parseValue(String sub) {
        Sound sound = null;
        DoubleRange pitch = null;
        DoubleRange volume = null;

        // split out sound/volume <#v>/pitch <#p>
        String[] split = sub.split("/");
        for (String value : split) {

            if (value.matches("[0-9.~-]*v")) {
                Log.dMsg("Found volume");

                volume = DoubleRange.parse(value.substring(0,
                        value.length() - 1));
            } else if (value.matches("[0-9.~-]*p")) {
                Log.dMsg("Found pitch");
                pitch = DoubleRange
                        .parse(value.substring(0, value.length() - 1));
            } else {
                for (Sound loopValue : Sound.values()) {
                    if (CommonMaterial.fuzzyMatchString(value,
                            loopValue.toString())) {
                        Log.logInfo("Matched sound " + loopValue.toString()
                                + " = " + value, Verbosity.HIGHEST);
                        sound = loopValue;
                    }
                }

            }
        }

        sounds.add(new ODSound(sound, volume, pitch));
    }

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {
        if (sounds != null) {
            if (pickOne) {
                process(drop, occurence,
                        sounds.get(OtherDrops.rng.nextInt(sounds.size())));
            } else {

                for (ODSound key : sounds) {
                    process(drop, occurence, key);
                }
            }
        }

        return false;
    }

    private void process(CustomDrop drop, OccurredEvent occurence, ODSound sound) {

        switch (damageActionType) {
        case ATTACKER:
            if (occurence.getPlayerAttacker() != null) {
                playSound(sound, occurence.getPlayerAttacker().getLocation());
            }
            break;
        case VICTIM: // and target
            playSound(sound, occurence.getLocation());
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
                            playSound(sound, player.getLocation());
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                playSound(sound, player.getLocation());
            }
            break;
        case WORLD:
            for (Player player : occurence.getLocation().getWorld()
                    .getPlayers()) {
                playSound(sound, player.getLocation());
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

    private void playSound(ODSound sound, Location location) {
        Double volume = 1.0, pitch = 1.0;
        if (sound.volume != null)
            volume = sound.volume.getRandomIn(OtherDrops.rng);
        if (sound.pitch != null)
            pitch = sound.pitch.getRandomIn(OtherDrops.rng);

        Log.dMsg("Playing sound '" + sound.sound.toString() + "'/" + volume
                + "v/" + pitch + "p at location: " + location.toString());

        location.getWorld().playSound(location, sound.sound,
                volume.floatValue(), pitch.floatValue());
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null)
                actions.add(new SoundAction(parseMe.get(key), matches.get(key)));
        }

        return actions;
    }

}
