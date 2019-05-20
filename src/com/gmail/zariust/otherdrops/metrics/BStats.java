package com.gmail.zariust.otherdrops.metrics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.gmail.zariust.otherdrops.OtherDrops;

public class BStats {
	private final OtherDrops plugin;
	private Metrics metrics;
    private final static Map<String, Integer> triggerCounts = new HashMap<String, Integer>();
	
    public BStats(OtherDrops plugin) {
    	this.plugin = plugin;
    }

    public void registerMetrics() {
        if (metrics == null) {
            metrics = new Metrics(plugin);
            registerCustomMetrics();
        }
    }
    
    private void registerCustomMetrics() {
        registerTriggers();
        // more custom charts
    }

    /**
     * Set up any required custom graphs that count data from config loading.
     * Currently counts used triggers
     * 
     */
    void registerTriggers() {
        metrics.addCustomChart(new Metrics.AdvancedPie("triggers", () -> {
            Map<String, Integer> values = new HashMap<>();
            for(Entry<String, Integer> entry : triggerCounts.entrySet()) {
            	String trigger = entry.getKey();
            	Integer amount = entry.getValue();
            	values.put(trigger, amount);
            }
            return values;
        }));
    }
    
    /**
     * Keeps a count of each individual trigger for the purpose of logging to
     * Metrics custom graph
     * 
     * @param triggerString
     */
    public static void incrementTriggerCounts(String triggerString) {
        if (triggerCounts.get(triggerString) == null) {
            triggerCounts.put(triggerString, new Integer(1));
        } else {
            triggerCounts.put(triggerString,
                    triggerCounts.get(triggerString) + 1);
        }
    }
}
