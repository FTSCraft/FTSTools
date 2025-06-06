package de.ftscraft.ftstools.environments;

import org.bukkit.inventory.Inventory;

public record CraftingEnvironment(String id, boolean canCraftNormalItems) {

    public Inventory generateCraftingInv() {
        return new CraftingEnvironmentHolder(this).getInventory();
    }

}
