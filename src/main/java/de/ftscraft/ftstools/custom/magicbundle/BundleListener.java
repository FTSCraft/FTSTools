package de.ftscraft.ftstools.custom.magicbundle;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.misc.Utils;
import de.ftscraft.ftsutils.misc.MiniMsg;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class BundleListener implements Listener {

    private final FTSTools plugin;
    private final MagicBundleHandler bundleHandler;

    private final Map<OfflinePlayer, Long> cooldown = new HashMap<>();

    public BundleListener(FTSTools plugin, MagicBundleHandler magicBundleHandler) {
        this.plugin = plugin;
        this.bundleHandler = magicBundleHandler;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!bundleHandler.isEmptyBundle(itemInHand)) {
            return;
        }

        if (cooldown.getOrDefault(player, 0L) > System.currentTimeMillis()) {
            return;
        } else {
            cooldown.put(player, System.currentTimeMillis() + 500);
        }

        Entity entity = event.getRightClicked();

        if (!bundleHandler.checkIfEntityCanBePutInBundle(itemInHand, entity)) {
            MiniMsg.msg(player, Utils.MM_PREFIX + "Das Bündel ist für ein anderes Tier bestimmt.");
            return;
        }

        bundleHandler.fillBundle(itemInHand, entity);

        MiniMsg.msg(player, "%sDu hast ein <lang:entity.minecraft.%s> in dein Magisches Bündel gesteckt."
                .formatted(Utils.MM_PREFIX, entity.getType().toString().toLowerCase()));

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (!bundleHandler.isFilledBundle(item)) {
            return;
        }

        if (cooldown.getOrDefault(player, 0L) > System.currentTimeMillis()) {
            return;
        } else {
            cooldown.put(player, System.currentTimeMillis() + 500);
        }

        bundleHandler.emptyOutBundle(item, event.getPlayer().getLocation());

    }

}
