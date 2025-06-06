package de.ftscraft.ftstools.environments;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CraftingEnvironmentHolder implements InventoryHolder {

    private final Inventory inventory;
    private final CraftingEnvironment environment;

    public CraftingEnvironmentHolder(CraftingEnvironment environment) {
        this.environment = environment;
        this.inventory = Bukkit.createInventory(this, InventoryType.CRAFTING);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public CraftingEnvironment getEnvironment() {
        return environment;
    }
}
