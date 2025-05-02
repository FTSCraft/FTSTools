package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.ConfigManager;
import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftstools.loader.initializer.ItemInitializer;
import de.ftscraft.ftsutils.items.ItemReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class StartupManager {
    private ConfigManager configManager;
    private ItemLoader itemLoader;
    private RecipeLoader recipeLoader;
    private final ArrayList<ItemInitializer> itemInitializers = new ArrayList<>();
    
    public void startUp() {
        configManager = new ConfigManager();
        itemLoader = new ItemLoader();
        recipeLoader = new RecipeLoader();
        initItemsAndRecipes();
    }

    private void initItemsAndRecipes() {
        initItems();
        ItemStore.logAllItemsLoaded(FTSTools.getInstance());
        initRecipes();
    }

    private void initItems() {
        initPluginItems();
        initIntermediateItems();
        initPluginConfigItems();
    }

    private void initPluginItems() {
        itemInitializers.forEach(ItemInitializer::initializeItems);
    }

    private void initIntermediateItems() {
        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            ItemStore.addItem(itemSection.getString("sign"), itemLoader.generateItem(itemSection));
        }
    }

    private void initPluginConfigItems() {
        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            String sign = itemSection.getString("sign");
            if (sign != null) {
                ItemStack item = itemLoader.generateItem(itemSection);
                ItemStore.addItem(sign, item);
            }
        }
    }

    private void initRecipes() {
        Set<String> allSigns = new HashSet<>();

        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            String sign = itemSection.getString("sign");
            if (sign != null) {
                allSigns.add(sign);
            }
        }

        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            ItemStack result = recipe.getResult();
            String sign = ItemReader.getSign(result);
            if (sign != null && allSigns.contains(sign) && recipe instanceof Keyed keyed) {
                Bukkit.removeRecipe(keyed.getKey());
            }
        });

        for (ConfigurationSection recipeSection : configManager.getAllRecipeSections()) {
            recipeLoader.createRecipe(recipeSection);
        }
    }

    public static Set<String> getAllConfigSigns() {
        Set<String> allSigns = new HashSet<>();
        ConfigManager configManager = new ConfigManager();
        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            String sign = itemSection.getString("sign");
            if (sign != null) {
                allSigns.add(sign);
            }
        }

        return allSigns;
    }
}
