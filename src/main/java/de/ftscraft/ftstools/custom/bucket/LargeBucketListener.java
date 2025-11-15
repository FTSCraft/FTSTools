package de.ftscraft.ftstools.custom.bucket;

import de.ftscraft.ftstools.FTSTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class LargeBucketListener implements Listener {

    public LargeBucketListener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler
    public void onPlayerBucket(PlayerBucketFillEvent event) {
        ItemStack inHand = event.getPlayer().getInventory().getItem(event.getHand());
        if (!LargeBucketManager.isLargeBukket(inHand)) {
            return;
        }
        LargeBucketManager.setVolume(inHand, LargeBucketManager.MAX_FILL);
        event.setItemStack(inHand.withType(Material.WATER_BUCKET));
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        ItemStack inHand = event.getPlayer().getInventory().getItem(event.getHand());
        if (!LargeBucketManager.isLargeBukket(inHand)) {
            return;
        }
        int newVol = LargeBucketManager.getVolume(inHand) - 1;
        if (newVol == 0) {
            ItemStack newBucket = FTSTools.getItemBySign("LARGE_BUCKET");
            event.setItemStack(newBucket);
            return;
        }
        LargeBucketManager.setVolume(inHand, LargeBucketManager.getVolume(inHand) - 1);
        event.setItemStack(inHand);
    }


}
