package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.ConfigManager;
import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftstools.loader.initializer.InstrumentInitializer;
import de.ftscraft.ftstools.loader.initializer.ItemInitializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class StartupManager {

    ConfigManager configManager;
    ItemLoader itemLoader;
    RecipeLoader recipeLoader;

    private final ArrayList<ItemInitializer> itemInitializers = new ArrayList<>();

    public void startUp() {
        configManager = new ConfigManager(FTSTools.getInstance().getConfig());
        itemLoader = new ItemLoader();
        recipeLoader = new RecipeLoader();

        initItemsAndRecipes();
    }

    private void initItemsAndRecipes() {
        initItems();
        initRecipes();
    }

    private void initItems() {
        initPluginItems();
        initIntermediateItems();
    }

    private void initPluginItems() {
        itemInitializers.add(new InstrumentInitializer());

        itemInitializers.forEach(ItemInitializer::initializeItems);
    }

    private void initIntermediateItems() {
        for (ConfigurationSection itemSection : configManager.getAllItemSections()) {
            ItemStore.addItem(itemSection.getString("sign"), itemLoader.generateItem(itemSection));
        }
    }


    private void initRecipes() {
        for (ConfigurationSection recipeSection : configManager.getAllRecipeSections()) {
            recipeLoader.createRecipe(recipeSection);
        }
    }



}
