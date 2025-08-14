package de.ftscraft.ftstools.custom;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.custom.bucket.Large_Bukket_Listener;
import de.ftscraft.ftstools.utils.FTSUser;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
            if(result.getType().equals(Material.BLUE_BUNDLE)){
                String sign = ItemReader.getSign(result);
                if(sign==null)return;
                if(sign.equals("MAGIC_BUNDLE")){
                    if(!event.getView().getPlayer().hasDiscoveredRecipe(new NamespacedKey(FTSTools.getInstance(), "magic_bundle"))){
                        event.getInventory().setResult(null);
                    }
                }
            }
        }
    }


}
