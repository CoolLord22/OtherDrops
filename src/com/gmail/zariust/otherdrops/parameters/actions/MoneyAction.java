package com.gmail.zariust.otherdrops.parameters.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.zariust.otherdrops.ConfigurationNode;
import com.gmail.zariust.otherdrops.Dependencies;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDrops;
import com.gmail.zariust.otherdrops.event.CustomDrop;
import com.gmail.zariust.otherdrops.event.OccurredEvent;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.parameters.Action;

public class MoneyAction extends Action {
    public enum MoneyActionType {
        ATTACKER, VICTIM, RADIUS, WORLD, SERVER, STEAL
    }

    static Map<String, MoneyActionType> matches         = new HashMap<String, MoneyActionType>();
    static {
        matches.put("money", MoneyActionType.ATTACKER);
        matches.put("money.attacker", MoneyActionType.ATTACKER);
        matches.put("money.victim", MoneyActionType.VICTIM);
        matches.put("money.target", MoneyActionType.VICTIM);
        matches.put("money.world", MoneyActionType.WORLD);
        matches.put("money.server", MoneyActionType.SERVER);
        matches.put("money.global", MoneyActionType.SERVER);
        matches.put("money.all", MoneyActionType.SERVER);
        matches.put("money.radius", MoneyActionType.RADIUS);
        matches.put("money.steal", MoneyActionType.STEAL);
    }

    protected DoubleRange               moneyAmount;
    protected boolean                   moneyPercent    = false;
    protected boolean                   deductBelowZero = false;
    protected MoneyActionType           moneyActionType;
    protected double                    radius          = 10;                                    // default
                                                                                                  // to
                                                                                                  // 10
                                                                                                  // blocks

    public MoneyAction(Object object, MoneyActionType moneyType) {
        this.moneyActionType = moneyType;

        if (object instanceof String) {
            String value = (String) object;
            if (value.endsWith("!")) {
                deductBelowZero = true;
                value = value.substring(0, value.length() - 1);
            }

            if (value.endsWith("%")) {
                moneyPercent = true;
                value = value.substring(0, value.length() - 1);
            }

            moneyAmount = DoubleRange.parse(value);
        } else if (object instanceof Integer || object instanceof Float
                || object instanceof Double) {
            moneyAmount = DoubleRange.parse(object.toString());
        }
    }

    @Override
    public boolean act(CustomDrop drop, OccurredEvent occurence) {
        switch (moneyActionType) {
        case ATTACKER:
            applyEffect(occurence.getPlayerAttacker());
            return false;
        case VICTIM:
            applyEffect(occurence.getVictim());
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
        case STEAL:
            stealMoney(occurence.getPlayerAttacker(),
                    occurence.getPlayerVictim());
            break;
        default:
            break;
        }

        return false;
    }

    /**
     * 'Steals' money (amount or percentage) from victim and gives to attacker
     * 
     * @param attacker
     * @param victim
     */
    private void stealMoney(Player attacker, Player victim) {
        if (attacker == null || victim == null) {
            return;
        }

        Double amount = calculateAmount(victim.getName());

        // We don't want to steal negative money
        if (amount > 0) {
            playerWithdraw(victim.getName(), amount);
            playerAdd(attacker.getName(), amount);
        }
    }

    private void applyEffect(LivingEntity lEnt) {
        if (lEnt == null || moneyAmount == null) {
            return;
        }

        if (lEnt instanceof Player) {
            String playerName = ((Player) lEnt).getName();
            if (Dependencies.hasVaultEcon()) {
                Double amount = calculateAmount(playerName);
                if (amount < 0) {
                    Log.dMsg("Acting on money action." + moneyAmount
                            + playerName);
                    playerWithdraw(playerName, amount);
                } else if (amount > 0) {
                    Log.dMsg("Acting on money action - adding: " + moneyAmount
                            + playerName);
                    playerAdd(playerName, amount);
                }
            }
        }
    }

    /**
     * @param playerName
     * @return
     */
    private Double calculateAmount(String playerName) {
        Double amount = moneyAmount.getRandomIn(OtherDrops.rng);
        if (moneyPercent) {
            Double balance = Dependencies.getVaultEcon().getBalance(playerName);
            amount = balance * moneyAmount.getRandomIn(OtherDrops.rng) / 100;
        }
        return amount;
    }

    private void playerWithdraw(String playerName, Double amount) {
        Double balance = Dependencies.getVaultEcon().getBalance(playerName);
        Double withDraw = amount;
        if (!deductBelowZero && balance - amount < 0) {
            withDraw = balance;
        }

        Log.dMsg("Acting on money action." + withDraw + playerName);

        Dependencies.getVaultEcon().withdrawPlayer(playerName, withDraw * -1);
    }

    private void playerAdd(String playerName, Double amount) {
        Dependencies.getVaultEcon().depositPlayer(playerName, amount);
    }

    // @Override
    @Override
    public List<Action> parse(ConfigurationNode parseMe) {
        List<Action> actions = new ArrayList<Action>();

        for (String key : matches.keySet()) {
            if (parseMe.get(key) != null) {
                actions.add(new MoneyAction(parseMe.get(key), matches.get(key)));
            }
        }
        return actions;
    }
}