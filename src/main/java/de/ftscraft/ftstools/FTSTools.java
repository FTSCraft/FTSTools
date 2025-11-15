package de.ftscraft.ftstools;

import de.ftscraft.ftstools.commands.ToolsCommand;
import de.ftscraft.ftstools.custom.bucket.LargeBucketListener;
import de.ftscraft.ftstools.custom.klappspaten.KlappspatenListener;
import de.ftscraft.ftstools.custom.laubschneider.LaubschneiderListener;
import de.ftscraft.ftstools.custom.magicbundle.MagicBundleHandler;
import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftstools.listeners.CraftListener;
import de.ftscraft.ftstools.listeners.PlayerInteractListener;
import de.ftscraft.ftstools.loader.StartupManager;
import de.ftscraft.ftstools.recipes.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class FTSTools extends JavaPlugin {
    private static FTSTools instance;

    private RecipeManager recipeManager;

    @Override
    public void onEnable() {
        instance = this;
        recipeManager = new RecipeManager();
        StartupManager.startUp(this);

        initListeners();
        new ToolsCommand(this);
    }

    private void initListeners() {
        new CraftListener(this);
        new PlayerInteractListener(this);
        new LargeBucketListener(this);
        new KlappspatenListener(this);
        new LaubschneiderListener(this);
        new MagicBundleHandler(this);
    }

    @Override
    public void onDisable() {
        for (NamespacedKey recipeKey : recipeManager.getRecipeKeys()) {
            Bukkit.removeRecipe(recipeKey);
        }
        Bukkit.updateRecipes();
    }

    public static FTSTools getInstance() {
        return instance;
    }


    public static ItemStack getItemBySign(String sign) {
        return ItemStore.getItem(sign);
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}
