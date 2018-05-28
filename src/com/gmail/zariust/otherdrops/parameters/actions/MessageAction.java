package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.event.SimpleDrop;
import com.gmail.zariust.otherdrops.parameters.Action;
import com.gmail.zariust.otherdrops.subject.CreatureSubject;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.ProjectileAgent;
import com.gmail.zariust.otherdrops.things.ODVariables;

public class MessageAction extends Action {
    // message.player, message.radius@<r>, message.world, message.server
    public enum MessageType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER
    }

    static Map<String, MessageType> matches = new HashMap<String, MessageType>();
    static {
        matches.put("message", MessageType.ATTACKER);
        matches.put("message.attacker", MessageType.ATTACKER);
        matches.put("message.victim", MessageType.VICTIM);
        matches.put("message.server", MessageType.SERVER);
        matches.put("message.world", MessageType.WORLD);
        matches.put("message.global", MessageType.SERVER);
        matches.put("message.all", MessageType.SERVER);
        matches.put("message.radius", MessageType.RADIUS);
    }

    protected MessageType           messageType;
    protected double                radius  = OtherDropsConfig.gActionRadius;
    private boolean                 variableParseRequired = false;
    private final List<String>            messages = new ArrayList<String>(); // this can contain variables, parse at runtime

    public MessageAction(Object messageToParse, MessageType messageType2) {
        this(messageToParse, messageType2, 0);
    }

    @SuppressWarnings("unchecked")
    public MessageAction(Object messageToParse, MessageType messageType2,
            double radius) {
        if (messageToParse == null)
            return; // "Registration" passed a null value

        List<String> tmpMessages = new ArrayList<String>();
        if (messageToParse instanceof List)
            tmpMessages = (List<String>) messageToParse;
        else
            tmpMessages = Collections.singletonList(messageToParse.toString());

        // OtherDrops.logInfo("Adding messages: "+messages.toString());

        messageType = messageType2;
        this.radius = radius;
        
        for (String msg : tmpMessages) {
            messages.add(ODVariables.preParse(msg));
            if (msg.contains("%") || msg.contains("<")) variableParseRequired = true;
        }

    }

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {
        String message = getRandomMessage(drop, occurence, this.messages, variableParseRequired);
        if (message.isEmpty())
            return false;

        Log.logInfo("Message action - messages = " + messages.toString()
                + ", message=" + message + ", type=" + messageType.toString(),
                Verbosity.HIGH);

        switch (messageType) {
        case ATTACKER:
            if (occurence.getPlayerAttacker() != null)
                occurence.getPlayerAttacker().sendMessage(message);
            break;
        case VICTIM:
            if (occurence.getPlayerVictim() != null)
                occurence.getPlayerVictim().sendMessage(message);
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
                            player.sendMessage(message);
            }

            break;
        case SERVER:
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(message);
            }
            break;
        case WORLD:
            for (Player player : occurence.getLocation().getWorld().getPlayers()) {
                player.sendMessage(message);
            }
            break;
        }
        return false;
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null)
                actions.add(new MessageAction(parseMe.get(key), matches.get(key)));
        }
        // messages = OtherDropsConfig.getMaybeList(new
        // ConfigurationNode((Map<?, ?>)parseMe), "message", "messages");
        return actions;
    }

    static public String getRandomMessage(CustomDrop drop,
            OccurredEvent occurence, List<String> messages, boolean parseVariablesRequired) {
        double amount = occurence.getCustomDropAmount();
        if (messages == null || messages.isEmpty())
            return "";
        String msg = messages.get(drop.rng.nextInt(messages.size()));
        if (parseVariablesRequired) msg = parseVariables(msg, drop, occurence, amount);
        return (msg == null) ? "" : msg;
    }

    static public String parseVariables(String msg, CustomDrop drop,
            OccurredEvent occurence, double amount) {
        if (msg == null)
            return msg;

        String dropName = "";
        String toolName = "";
        String playerName = "";
        String victimName = "";
        String quantityString = "";
        String deathMessage = "";
        String loreName = "";

        if (drop != null) {
            if (drop instanceof SimpleDrop) {
                if (((SimpleDrop) drop).getDropped() != null) {
                    if (((SimpleDrop) drop).getDropped().isQuantityInteger())
                        quantityString = String.valueOf(Math.round(amount));
                    else
                        quantityString = Double.toString(amount);
                }
            }
            dropName = drop.getDropName();
        }

        if (occurence != null) {
            if (occurence.getTool() != null)
                toolName = occurence.getTool().getReadableName();
            if (occurence.getTool() instanceof PlayerSubject) {
                toolName = ((PlayerSubject) occurence.getTool()).getTool().getReadableName();
                ItemStack inHand = ((PlayerSubject) occurence.getTool()).getPlayer().getInventory().getItemInMainHand();
                if (inHand != null)
                    loreName = (inHand.getItemMeta() == null ? null : inHand.getItemMeta().getDisplayName());
                if (loreName == null)
                    loreName = toolName;
                playerName = ((PlayerSubject) occurence.getTool()).getPlayer().getName();
            } else if (occurence.getTool() instanceof ProjectileAgent) {
                toolName = occurence.getTool().getReadableName();
                if (((ProjectileAgent) occurence.getTool()).getShooter() == null) {
                    Log.logInfo("MessageAction: getShooter = null, this shouldn't happen. (" + occurence.getTool().toString() + ")");
                    playerName = "null";
                } else {
                    playerName = ((ProjectileAgent) occurence.getTool()).getShooter().getReadableName();
                    toolName += " shot by " + playerName;

                    Entity ent = ((ProjectileAgent) occurence.getTool()).getShooter().getEntity();
                    if (ent instanceof LivingEntity) {
                        loreName = ((LivingEntity) ent).getCustomName();
                    }

                }
            } else if (occurence.getTool() instanceof CreatureSubject) {
                Entity ent = ((CreatureSubject) occurence.getTool()).getEntity();
                if (ent instanceof LivingEntity) {
                    loreName = ((LivingEntity) ent).getCustomName();
                }
            }
            victimName = occurence.getTarget().getReadableName();

            if (occurence.getRealEvent() instanceof PlayerDeathEvent) {
                PlayerDeathEvent ede = (PlayerDeathEvent) occurence.getRealEvent();

                deathMessage = ede.getDeathMessage();
            }
        }

        return new ODVariables().setPlayerName(playerName).setVictimName(victimName).setDropName(dropName).setToolName(toolName).setQuantity(quantityString).setDeathMessage(deathMessage).setloreName(loreName).setLocation(occurence.getLocation()).parse(msg);
    }
}
