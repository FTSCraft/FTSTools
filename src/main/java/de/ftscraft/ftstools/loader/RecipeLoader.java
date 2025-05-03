package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.items.ItemStore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class RecipeLoader {

    public void createRecipe(ConfigurationSection recipeSection) {

        ConfigurationSection parent = recipeSection.getParent();

        //noinspection DataFlowIssue
        ItemStack result = ItemStore.getItem(parent.getString("item.sign", parent.getName()));

        if (result == null) {
            throw new IllegalStateException("No ItemStack found for key: " + parent.getString("item.sign", parent.getName()) + ". Check the config and item initialization.");
        }

        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(FTSTools.getInstance(), sanitizeKey(parent.getName())),
                result);

        List<String> shape = recipeSection.getStringList("shape");
        recipe.shape(shape.toArray(String[]::new));

        setIngredients(recipe, recipeSection);

        Bukkit.getServer().addRecipe(recipe);

    }

    private void setIngredients(ShapedRecipe recipe, ConfigurationSection recipeSection) {
        recipeSection = recipeSection.getConfigurationSection("ingredients");

        assert recipeSection != null;
        for (String key : recipeSection.getKeys(false)) {
            String item = recipeSection.getString(key);
            char c = key.charAt(0);
            assert item != null;
            Material mat = Material.getMaterial(item);
            if (mat != null) {
                recipe.setIngredient(c, mat);
            } else {
                ItemStack customItem = ItemStore.getItem(item);
                if (customItem == null) {
                    FTSTools.getInstance().getLogger().severe("Could not find custom item for ingredient '" + item + "' in recipe for '" + recipe.getKey().getKey() + "'. Check the config and item registration.");
                    throw new IllegalStateException("Missing custom item for ingredient: " + item + " in recipe: " + recipe.getKey().getKey());
                }
                recipe.setIngredient(c, customItem);
            }
        }
    }

    private String sanitizeKey(String input) {
        return input.toLowerCase()
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("ß", "ss")
                .replaceAll("[^a-z0-9/._-]", "_");
    }

}