package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.ConfigManager;
import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftstools.loader.initializer.ItemInitializer;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StartupManager {
    private static ConfigManager configManager;
    private static ItemLoader itemLoader;
    private static RecipeLoader recipeLoader;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final static ArrayList<ItemInitializer> itemInitializers = new ArrayList<>();

    public static void startUp(FTSTools plugin) {
        configManager = new ConfigManager();
        new CraftingEnvLoader(plugin);
        itemLoader = new ItemLoader();
        recipeLoader = new RecipeLoader(plugin.getRecipeManager());
        initItemsAndRecipes();
    }

    private static void initItemsAndRecipes() {
        initItems();
        initRecipes();
    }

    private static void initItems() {
        initPluginItems();
        initIntermediateItems();
        initPluginConfigItems();

        FTSTools.getInstance().getLogger().info("All custom items have been loaded. Total: " + ItemStore.size());
    }

    private static void initPluginItems() {
        itemInitializers.forEach(ItemInitializer::initializeItems);
    }

    private static void initIntermediateItems() {
        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            ItemStore.addItem(itemSection.getString("sign"), itemLoader.generateItem(itemSection));
        }
    }

    private static void initPluginConfigItems() {
        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            String sign = itemSection.getString("sign");
            if (sign != null) {
                ItemStack item = itemLoader.generateItem(itemSection);
                ItemStore.addItem(sign, item);
            }
        }
    }

    private static void initRecipes() {
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
