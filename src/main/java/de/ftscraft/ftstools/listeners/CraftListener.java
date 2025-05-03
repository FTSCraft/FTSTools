package de.ftscraft.ftstools.listeners;

import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class CraftListener implements Listener {
    private final Set<String> allSigns;

    public CraftListener(Set<String> allSigns) {
        this.allSigns = allSigns;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = inv.getMatrix();

        for (ItemStack item : matrix) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            String sign = ItemReader.getSign(item);
            if (sign == null || !allSigns.contains(sign)) {
                continue;
            }
            ItemStack result = inv.getResult();
            String resultSign = result != null ? ItemReader.getSign(result) : null;
            if (resultSign != null && allSigns.contains(resultSign)) {
                continue;
            }
            inv.setResult(null);
            return;
        }
    }
}