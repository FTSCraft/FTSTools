package de.ftscraft.ftstools.listeners;

import de.ftscraft.ftstools.environments.CraftingEnvironment;
import de.ftscraft.ftstools.environments.CraftingEnvironmentHolder;
import de.ftscraft.ftstools.misc.Utils;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public class CraftListener implements Listener {
    private final Set<String> allSigns;

    public CraftListener(Set<String> allSigns) {
        this.allSigns = allSigns;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {

        CraftingInventory inv = event.getInventory();
        ItemStack result = inv.getResult();
        String resultSign = result != null ? ItemReader.getSign(result) : null;

        if (handleCraftingEnv(event, resultSign))
            return;

        blockUsingCustomItemsForNormalRecipes(inv, resultSign);
    }

    private void blockUsingCustomItemsForNormalRecipes(CraftingInventory inv, String resultSign) {
        ItemStack[] matrix = inv.getMatrix();

        if (resultSign != null && allSigns.contains(resultSign)) {
            return;
        }
        for (ItemStack item : matrix) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            String sign = ItemReader.getSign(item);
            if (sign == null || !allSigns.contains(sign)) {
                continue;
            }
            inv.setResult(null);
            return;
        }
    }

    private boolean handleCraftingEnv(PrepareItemCraftEvent event, String sign) {

        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();

        if (result == null)
            return false;

        if (!(inventory.getHolder() instanceof CraftingEnvironmentHolder craftingEnvironmentHolder)) {
            return true;
        }

        CraftingEnvironment environment = craftingEnvironmentHolder.getEnvironment();
        if (!environment.canCraftNormalItems() && sign != null) {
            inventory.setResult(null);
            return true;
        }

        String craftingEnv;
        try {
            craftingEnv = ItemReader.getPDC(result, Utils.PDC_CRAFTING_ENVS, PersistentDataType.STRING);
            if (craftingEnv == null)
                return true;
        } catch (IllegalArgumentException ex) {
            return true;
        }

        if (!craftingEnv.contains(environment.id())) {
            inventory.setResult(null);
            return true;
        }

        return false;

    }

}