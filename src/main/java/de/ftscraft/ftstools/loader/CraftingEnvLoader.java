package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.environments.CraftingEnvManager;
import de.ftscraft.ftstools.environments.CraftingEnvironment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CraftingEnvLoader {

    private final FileConfiguration config;
    private final FTSTools plugin;

    public CraftingEnvLoader(FTSTools plugin) {
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder(), "crafting-envs.yml");
        if (!file.exists()) {
            plugin.saveResource("crafting-envs.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadEnvironments();
    }

    public void loadEnvironments() {
        ConfigurationSection section = config.getConfigurationSection("environments");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            plugin.getLogger().info(key);
            String name = section.getString(key + ".name");
            String id = section.getString(key+".id").toLowerCase();
            boolean allowVanilla = section.getBoolean(key + ".allow-vanilla-recipes", true);

            if (name == null) continue;

            CraftingEnvironment env = new CraftingEnvironment(id, name, allowVanilla);
            CraftingEnvManager.addCraftingEnv(id, env);
        }
    }
}
