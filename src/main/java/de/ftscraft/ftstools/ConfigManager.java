package de.ftscraft.ftstools;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private FileConfiguration config;

    public ConfigManager(FileConfiguration configuration) {
        this.config = configuration;
        saveDefaultConfig();
    }

    public void saveDefaultConfig() {
        config.options().copyDefaults(true);
        FTSTools.getInstance().saveDefaultConfig();
        FTSTools.getInstance().saveConfig();
    }

    public List<ConfigurationSection> getAllItemSections() {

        List<ConfigurationSection> itemSections = new ArrayList<>();

        ConfigurationSection intermediateItems = config.getConfigurationSection("intermediate_items");
        for (String intermediateItemNames : intermediateItems.getKeys(false)) {
            itemSections.add(intermediateItems
                    .getConfigurationSection(intermediateItemNames)
                    .getConfigurationSection("item"));
        }

        return itemSections;

    }

    public List<ConfigurationSection> getAllRecipeSections() {

        List<ConfigurationSection> recipeSections = new ArrayList<>();

        ConfigurationSection intermediateItems = config.getConfigurationSection("intermediate_items");
        for (String intermediateItemNames : intermediateItems.getKeys(false)) {
            recipeSections.add(intermediateItems
                    .getConfigurationSection(intermediateItemNames)
                    .getConfigurationSection("recipe"));
        }

        ConfigurationSection pluginItems = config.getConfigurationSection("plugin_items");
        for (String pluginItemNames : pluginItems.getKeys(false)) {
            recipeSections.add(pluginItems.getConfigurationSection(pluginItemNames).getConfigurationSection("recipe"));
        }

        return recipeSections;

    }


}
