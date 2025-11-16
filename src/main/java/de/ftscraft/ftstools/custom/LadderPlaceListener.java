package de.ftscraft.ftstools.custom;

import de.ftscraft.ftstools.FTSTools;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Ladder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated(forRemoval = true)
public class LadderPlaceListener implements Listener {
    private final FTSTools plugin;

    public LadderPlaceListener(FTSTools plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.LADDER) return;

        Player p = event.getPlayer();
        if (!p.hasPermission("autoladders.use")) return;

        Block start = event.getBlockPlaced();
        World world = start.getWorld();
        Ladder baseData = (Ladder) start.getBlockData();
        BlockFace facing = baseData.getFacing();
        BlockFace supportFace = facing.getOppositeFace();

        final boolean consumeInSurvival = true;
        final boolean airOnly = true;
        final int maxExtend = 256;

        int maxY = world.getMaxHeight() - 1;
        int placed = 0;

        // Bestimme verfügbare Leiter-Anzahl (falls Survival + kein free)
        int available = Integer.MAX_VALUE;
        boolean free = p.getGameMode() == GameMode.CREATIVE || p.hasPermission("autoladders.free");
        if (!free && consumeInSurvival) {
            available = countItem(p, Material.LADDER); // bereits die platzierte Leiter ist aus der Hand entnommen
        }

        Block cursor = start.getRelative(BlockFace.UP);
        while (cursor.getY() <= maxY && placed < maxExtend) {
            // Abbruch, wenn Block belegt und nicht erlaubt
            if (airOnly) {
                if (!cursor.isEmpty()) break;
            } else {
                if (!(cursor.isEmpty() || cursor.isLiquid())) break;
            }

            // Check: fester Block hinter der Leiter
            Block support = cursor.getRelative(supportFace);
            if (!support.getType().isSolid()) break;

            // Falls Survival und Verbrauch aktiv: genug Leitern?
            if (!free && consumeInSurvival) {
                if (available <= 0) break;
                // versuche, eine Leiter aus dem Inventar zu entfernen
                if (!removeOne(p, Material.LADDER)) break;
                available--;
            }

            // Leiter mit gleicher Ausrichtung setzen
            cursor.setType(Material.LADDER, true);
            Ladder data = (Ladder) cursor.getBlockData();
            data.setFacing(facing);
            cursor.setBlockData(data, true);

            placed++;
            cursor = cursor.getRelative(BlockFace.UP);
        }
    }

    private int countItem(Player p, Material mat) {
        int total = 0;
        for (ItemStack is : p.getInventory().getContents()) {
            if (is != null && is.getType() == mat) total += is.getAmount();
        }
        return total;
    }

    private boolean removeOne(Player p, Material mat) {
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack is = p.getInventory().getItem(i);
            if (is != null && is.getType() == mat) {
                is.setAmount(is.getAmount() - 1);
                if (is.getAmount() <= 0) p.getInventory().setItem(i, null);
                return true;
            }
        }
        return false;
    }
}