package de.ftscraft.ftstools.custom;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.custom.bucket.Large_Bukket_Listener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class handleCrafting implements Listener {

    public handleCrafting(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if(result != null && result.getItemMeta() != null){
            if(result.getItemMeta().getPersistentDataContainer().has(Large_Bukket_Listener.namespacedKey)){

                if(!event.getView().getPlayer().hasDiscoveredRecipe(new NamespacedKey(FTSTools.getInstance(), "large_bucket"))){
                    event.getInventory().setResult(null);
                }

            }
        }
    }


}
