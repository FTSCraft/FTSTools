package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Arrays;
import java.util.List;

public class RecipeLoader {

    public void createRecipe(ConfigurationSection recipeSection) {
        ConfigurationSection parent = recipeSection.getParent();

        ItemStack result = ItemStore.getItem(parent.getString("item.sign", parent.getName()));
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(FTSTools.getInstance(), parent.getName()),
                result);

        List<String> shape = recipeSection.getStringList("shape");

        recipe.shape(shape.toArray(String[]::new));
        setIngredients(recipe, recipeSection);
        Bukkit.getServer().addRecipe(recipe);
    }

    private void setIngredients(ShapedRecipe recipe, ConfigurationSection recipeSection) {
        recipeSection = recipeSection.getConfigurationSection("ingredients");
        for (String key : recipeSection.getKeys(false)) {
            String item = recipeSection.getString(key);
            char c = key.charAt(0);

            // if material exists, set ingredient to the material
            Material mat = Material.getMaterial(item);
            if (mat != null) {
                recipe.setIngredient(c, mat);
                continue;
            }

            // otherwise, get it from sign
            recipe.setIngredient(c, ItemStore.getItem(item));
        }
    }

}
