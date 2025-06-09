package de.ftscraft.ftstools.environments;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.StorageNBTComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;

public record CraftingEnvironment(String id, String name, boolean canCraftNormalItems) {

    public void generateCraftingInv(Player p) {
        StorageNBTComponent comp = Component.storageNBT("tools.env", Key.key(this.id));
        //noinspection UnstableApiUsage
        InventoryView inventoryView = MenuType.CRAFTING.create(p, comp);
        p.openInventory(inventoryView);
    }

}
