package com.gmail.zariust.otherdrops;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import com.gmail.zariust.common.Verbosity;
import com.gmail.zariust.otherdrops.event.CustomDropTest;
import com.gmail.zariust.otherdrops.parameters.Trigger;

public class OtherDropsConfigTest extends AbstractTestingBase {

    public static final String   TMPFS_DIR = System.getProperty("tmpfs.dir",
                                                   "/tmp/");
    static Logger                log       = Logger.getLogger("Minecraft");

    @Test
    public void testDefaultsLoading() throws IOException {
        // Initialization: Bukkit must have a server with a logger.
        // BukkitMock mock = new BukkitMock();

        // Create a test drop
        String otherdropsString = "otherdrops:\n" + "  wool@white:\n"
                + "    - tool: dye@red\n"
                + "      replacementblock: wool@red\n"
                + "      consumetool: 1\n";

        // Create the defaults section to test (first testing with no dashes)
        String defaultsString = "defaults:\n" + "  world: TestWorld\n"
                + "  trigger: right_click\n";

        System.out.println("First run....");
        File file = getTempFile(defaultsString + otherdropsString);
        testDefaultsForFile(file);

        // Test defaults as a "section" start with a dash
        defaultsString = "defaults:\n" + "  - world: TestWorld\n"
                + "    trigger: right_click\n";

        System.out.println("Second run....");
        file = getTempFile(defaultsString + otherdropsString);
        testDefaultsForFile(file);

        // Currently no support for defaults with each value dashed, eg:
        defaultsString = "defaults:\n" + "  - world: all\n"
                + "  - trigger: right_click\n";

        // The end :)
    }

    private File getTempFile(String string) throws IOException {
        // Create the temporary directory and file
        File dir = new File(TMPFS_DIR + "/zar_deleteme");
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            System.out.print("tempdir deleted = " + dir.delete() + "/n");
        }

        assertTrue(dir.mkdir());
        System.out.println("Created " + dir.getAbsolutePath());

        File file = new File(dir, "tempfile.yml");

        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
        pw.println(string);
        pw.close();
        fw.close();

        return file;
    }

    void testDefaultsForFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        OtherDropsConfig config = new OtherDropsConfig(new OtherDrops());
        OtherDropsConfig.verbosity = Verbosity.HIGHEST;

        Map<String, Object> map = new HashMap<String, Object>();
        // map.put("trigger", "right_click");

        // ConfigurationNode defaults = new ConfigurationNode(map);
        ConfigurationNode node = null;

        if (yaml.getConfigurationSection("defaults") == null) {
            node = ConfigurationNode.parse(yaml.getMapList("defaults")).get(0);
        } else {
            System.out.println("list: "
                    + yaml.getConfigurationSection("defaults").getKeys(true)
                            .toString());
            ConfigurationSection section = yaml
                    .getConfigurationSection("defaults");
            for (String key : yaml.getConfigurationSection("defaults").getKeys(
                    true)) {
                map.put(key, section.get(key));
            }
            node = new ConfigurationNode(map);
        }

        assertTrue("Node is null!", node != null);

        config.loadModuleDefaults(node);

        System.out.println("Action = " + config.defaultTrigger.toString());
        System.out.println("World = " + config.defaultWorlds.toString());

        assertTrue("Default triggers didn't read?",
                config.defaultTrigger.contains(Trigger.RIGHT_CLICK));
        assertTrue(
                "Default world didn't read?",
                config.defaultWorlds.get(CustomDropTest.testWorld) == java.lang.Boolean.TRUE);
    }
}
