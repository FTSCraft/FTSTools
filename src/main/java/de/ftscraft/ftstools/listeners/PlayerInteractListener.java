package de.ftscraft.ftstools.listeners;

import de.ftscraft.ftstools.environments.CraftingEnvManager;
import de.ftscraft.ftstools.environments.CraftingEnvironment;
import de.ftscraft.ftstools.misc.Utils;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        handleOpenCraftingEnv(event);

    }

    private void handleOpenCraftingEnv(PlayerInteractEvent event) {

        if (!event.getAction().isRightClick())
            return;
        if (event.getItem() == null)
            return;

        String craftingEnvId;
        try {
            craftingEnvId = ItemReader.getPDC(event.getItem(), Utils.PDC_OPEN_CRAFTING_ENV, PersistentDataType.STRING);
        } catch (IllegalArgumentException ex) {
            return;
        }

        CraftingEnvironment craftingEnv = CraftingEnvManager.getCraftingEnv(craftingEnvId);
        event.getPlayer().openInventory(craftingEnv.generateCraftingInv());

    }

}
