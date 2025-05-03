package de.ftscraft.ftstools;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {

    private final List<FileConfiguration> items = new ArrayList<>();

    public ConfigManager() {
        loadAllConfigs();
    }

    private void loadAllConfigs() {
        File configDir = new File(FTSTools.getInstance().getDataFolder(), "items");
        if (!configDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            configDir.mkdirs();
        }

        File[] files = configDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                items.add(config);
            }
        }
    }

    public List<ConfigurationSection> getAllItemSections() {

        List<ConfigurationSection> itemSections = new ArrayList<>();

        for (FileConfiguration config : items) {
            ConfigurationSection intermediateItems = config.getConfigurationSection("intermediate_items");
            if (intermediateItems != null) {
                for (String intermediateItemName : intermediateItems.getKeys(false)) {
                    ConfigurationSection itemSection = Objects.requireNonNull(intermediateItems.getConfigurationSection(intermediateItemName)).getConfigurationSection("item");
                    if (itemSection != null) {
                        itemSections.add(itemSection);
                    }
                }
            }

            ConfigurationSection pluginItems = config.getConfigurationSection("plugin_items");
            if (pluginItems != null) {
                for (String pluginItemName : pluginItems.getKeys(false)) {
                    ConfigurationSection itemSection = Objects.requireNonNull(pluginItems.getConfigurationSection(pluginItemName)).getConfigurationSection("item");
                    if (itemSection != null) {
                        itemSections.add(itemSection);
                    }
                }
            }
        }

        return itemSections;

    }

    public List<ConfigurationSection> getAllRecipeSections() {

        List<ConfigurationSection> recipeSections = new ArrayList<>();

        for (FileConfiguration config : items) {
            ConfigurationSection intermediateItems = config.getConfigurationSection("intermediate_items");
            if (intermediateItems != null) {
                for (String intermediateItemName : intermediateItems.getKeys(false)) {
                    //noinspection DataFlowIssue
                    ConfigurationSection recipeSection = intermediateItems.getConfigurationSection(intermediateItemName).getConfigurationSection("recipe");
                    if (recipeSection != null) {
                        recipeSections.add(recipeSection);
                    }
                }
            }

            ConfigurationSection pluginItems = config.getConfigurationSection("plugin_items");
            if (pluginItems != null) {
                for (String pluginItemName : pluginItems.getKeys(false)) {
                    //noinspection DataFlowIssue
                    ConfigurationSection recipeSection = pluginItems.getConfigurationSection(pluginItemName).getConfigurationSection("recipe");
                    if (recipeSection != null) {
                        recipeSections.add(recipeSection);
                    }
                }
            }
        }

        return recipeSections;

    }


}
