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

package com.gmail.zariust.otherdrops.drop;

import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.Dependencies;
import com.gmail.zariust.otherdrops.Log;
import com.gmail.zariust.otherdrops.OtherDropsConfig;
import com.gmail.zariust.otherdrops.options.DoubleRange;
import com.gmail.zariust.otherdrops.subject.PlayerSubject;
import com.gmail.zariust.otherdrops.subject.Target;

public class MoneyDrop extends DropType {
    public enum MoneyDropType {
        NORMAL, STEAL, PENALTY, PERCENTPENALTY;

        public static MoneyDropType fromString(String other) {
            try {
                MoneyDropType type = MoneyDropType.valueOf(other.toUpperCase());
                return type;
            } catch (Exception e) {
            }

            return null;
        }
    }

    /**
     * Amount (range) of money this individual drop contains
     * 
     */
    protected DoubleRange   loot;
    protected MoneyDropType type;

    public MoneyDrop(DoubleRange amount, double chance, MoneyDropType type) { // Rome
        super(DropCategory.MONEY, chance);
        loot = amount;
        this.type = type;
    }

    @Override
    public double getAmount() {
        return total;
    }

    @Override
    public boolean isQuantityInteger() {
        return false;
    }

    @Override
    protected int calculateQuantity(double amount, Random rng) {
        total = loot.getRandomIn(rng);
        total *= amount;
        total = roundOffMoney(total);
        return 1;
    }

    /**
     * Round the money to the nearest x decimal places as specified in the
     * global config
     * 
     * @param val
     *            - value to round off
     * @return Value rounded off as per global config "money_precision" setting
     */
    private double roundOffMoney(double val) {
        double factor = pow(10, OtherDropsConfig.moneyPrecision);
        val *= factor;
        val = round(val);
        val /= factor;
        return val;
    }

    @Override
    protected DropResult performDrop(Target source, Location where,
            DropFlags flags) {
        DropResult dropResult = DropResult.fromOverride(this.overrideDefault);

        Player victim = null;
        double amount = total;

        if (source instanceof PlayerSubject)
            victim = ((PlayerSubject) source).getPlayer();
        if (victim != null) {
            if (type.equals(MoneyDropType.STEAL)) {
                Log.logInfo(
                        "(vault)Stealing money ("
                                + amount
                                + ") from "
                                + victim.getName()
                                + ", giving to "
                                + (flags.recipient == null ? "no-one"
                                        : flags.recipient.getName()) + ".",
                        Verbosity.HIGHEST);
                double balance = Dependencies.getVaultEcon().getBalance(
                        victim.getName());
                if (balance <= 0)
                    return dropResult;
                amount = min(balance, amount);
                Dependencies.getVaultEcon().withdrawPlayer(victim.getName(),
                        amount);
            }
        } else {
            Log.logInfo(
                    "Processing money@"
                            + type.toString()
                            + "/"
                            + amount
                            + " to "
                            + (flags.recipient == null ? "no-one"
                                    : flags.recipient.getName()) + "",
                    Verbosity.HIGHEST);
        }
        if (!canDrop(flags)) {
            return dropResult;
        }

        String amountString = String.valueOf(amount);
        if (type.equals(MoneyDropType.PENALTY)
                || type.equals(MoneyDropType.PERCENTPENALTY)) {
            double withdraw = amount;
            double balance = Dependencies.getVaultEcon().getBalance(
                    flags.recipient.getName());
            if (type.equals(MoneyDropType.PERCENTPENALTY)) {
                withdraw = balance * amount / 100;
                amountString = amountString + "% (" + roundOffMoney(withdraw)
                        + ")";
            }

            double newBalance = Dependencies.getVaultEcon().withdrawPlayer(
                    flags.recipient.getName(), withdraw).balance;

            if (OtherDropsConfig.getVerbosity().exceeds(Verbosity.HIGHEST)) {
                Log.logInfoNoVerbosity("(vault)Reducing attacker ("
                        + flags.recipient.getName() + ") funds by "
                        + amountString + ": " + roundOffMoney(balance) + "->"
                        + roundOffMoney(newBalance));
            }

            dropResult.setQuantity(1);
            return dropResult;
        }

        dropMoney(source, where, flags, amount);
        dropResult.setQuantity(1);
        return dropResult;
    }

    protected void dropMoney(Target source, Location where, DropFlags flags,
            double amount) {
        if (Dependencies.hasVaultEcon()) {
            Dependencies.getVaultEcon().depositPlayer(
                    flags.recipient.getName(), amount); // TODO: is this right?
                                                        // Or check for accounts
                                                        // still?
            Log.logInfo("Funds deposited via VAULT.", Verbosity.HIGHEST);
        }
    }

    private boolean canDrop(DropFlags flags) {
        if (flags.recipient == null) {
            Log.logInfo(
                    "MoneyDrop - recipient is null, cannot give money to recipient.",
                    Verbosity.HIGH);
            return false;
        }
        if (!Dependencies.hasVaultEcon()) {
            Log.logWarning("Money drop has been configured but no economy plugin has been detected.");
            return false;
        }
        return true;
    }

    public static DropType parse(String drop, String data, DoubleRange amount,
            double chance) {
        String[] split = null;
        if (drop.matches("\\w+:.*")) {
            split = drop.toUpperCase().split(":", 2);
        } else
            split = drop.toUpperCase().split("@", 2);

        boolean real = split[0].matches("MONEY[ _-]DROP");
        if (!real && !split[0].equals("MONEY"))
            return null; // Invalid type of money
        if (split.length > 1)
            data = split[1];
        MoneyDropType type = MoneyDropType.fromString(data);
        if (type == null)
            type = MoneyDropType.NORMAL;

        if (!type.equals(MoneyDropType.STEAL) && !data.isEmpty()
                && !data.equals("0"))
            Log.logWarning("Invalid data for " + split[0] + ": " + data);
        if (real) {
            return new RealMoneyDrop(amount.toIntRange(), chance, type); // TODO:
                                                                         // should
                                                                         // reduce
                                                                         // apply
                                                                         // to
                                                                         // moneydrop?
        } else {
            if (Dependencies.hasVaultEcon()) {
                return new MoneyDrop(amount, chance, type);
            } else {
                Log.logWarning("Money drop has been configured but no economy plugin has been detected (have you installed Vault and a separate economy plugin?)");
                return null;
            }
        }
    }

    @Override
    public String getName() {
        return "MONEY";
    }

    @Override
    public DoubleRange getAmountRange() {
        return loot;
    }
}
