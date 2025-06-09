package de.ftscraft.ftstools.listeners;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.environments.CraftingEnvManager;
import de.ftscraft.ftstools.environments.CraftingEnvironment;
import de.ftscraft.ftstools.misc.Utils;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerInteractListener implements Listener {

    public PlayerInteractListener(FTSTools plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

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
            if (craftingEnvId == null)
                return;
        } catch (IllegalArgumentException ex) {
            return;
        }

        CraftingEnvironment craftingEnv = CraftingEnvManager.getCraftingEnv(craftingEnvId);
        craftingEnv.generateCraftingInv(event.getPlayer());

    }

}
