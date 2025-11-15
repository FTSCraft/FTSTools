package de.ftscraft.ftstools.recipes;

import org.bukkit.NamespacedKey;

import java.util.ArrayList;

public class RecipeManager {

    private final ArrayList<NamespacedKey> recipeKeys = new ArrayList<>();

    public void addRecipeKey(NamespacedKey key) {
        recipeKeys.add(key);
    }

    public ArrayList<NamespacedKey> getRecipeKeys() {
        return recipeKeys;
    }
}
