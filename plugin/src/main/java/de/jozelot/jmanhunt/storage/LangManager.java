package de.jozelot.jmanhunt.storage;

import de.jozelot.jmanhunt.JManhunt;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LangManager {

    private final JManhunt plugin;

    public LangManager(JManhunt plugin) {
        this.plugin = plugin;
    }

    private FileConfiguration languageConfig;
    private final String[] defaultLanguages = {"de", "en"};

    private String prefix;

    /**
     * Here the plugin saves the locales that the plugin brings with it
     */
    public void setupFiles() {
        File langFolder = new File(plugin.getDataFolder(), "locales");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        for (String lang : defaultLanguages) {
            File file = new File(langFolder, lang + ".yml");
            if (!file.exists()) {
                plugin.saveResource("locales/" + lang + ".yml", false);
            }
        }
    }

    /**
     * Here the plugin loads the lang keys and values from the given locale
     * @param locale Local prefix to load
     * @return
     */
    public boolean load(String locale) {
        setupFiles();
        File langFile = new File(plugin.getDataFolder(), "locales/" + locale + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().log(Level.SEVERE, "");
            plugin.getLogger().log(Level.SEVERE, "Language file '" + locale + ".yml' not found in locales folder!");
            plugin.getLogger().log(Level.SEVERE, "");
            return false;
        }

        this.languageConfig = YamlConfiguration.loadConfiguration(langFile);

        loadData();
        return true;
    }

    private void loadData() {
        prefix = languageConfig.getString("prefix", "<dark_gray>[<aqua>JManhunt<dark_gray>]<reset>");
    }

    /**
     * Format the language files for the placeholders like {player}
     * @param path to the key in the lang file
     * @param replace Map for what word should be replaced with what
     * @return
     */
    public String format(String path, Map<String, String> replace) {
        String rawMessage = languageConfig.getString(path);

        if (rawMessage == null) {
            return "<red>" + path;
        }

        return replacePlaceholders(rawMessage, replace);
    }

    public List<String> formatList(String path, Map<String, String> replace) {
        List<String> rawList = languageConfig.getStringList(path);

        if (rawList.isEmpty()) {
            return List.of("<red>" + path);
        }

        List<String> formattedList = new ArrayList<>();
        for (String line : rawList) {
            formattedList.add(replacePlaceholders(line, replace));
        }

        return formattedList;
    }

    private String replacePlaceholders(String input, Map<String, String> replace) {
        if (input == null) return "";

        input = input.replace("{prefix}", prefix);

        if (replace != null && !replace.isEmpty()) {
            for (Map.Entry<String, String> entry : replace.entrySet()) {
                input = input.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return input;
    }

    public String getPrefix() {
        return prefix;
    }

}
