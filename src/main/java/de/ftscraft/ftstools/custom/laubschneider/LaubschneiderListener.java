package de.ftscraft.ftstools.custom.laubschneider;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LaubschneiderListener implements Listener {

    private final List<Player> activePlayers = new ArrayList<>();

    public LaubschneiderListener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (activePlayers.contains(player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty() || item.getType() != Material.SHEARS) return;

        if (!"LAUBSCHNEIDER".equals(ItemReader.getSign(item))) return;

        Block start = event.getBlock();
        if (!isLeaf(start.getType())) return;

        activePlayers.add(player);
        processLeaves(player, start, item);
        activePlayers.remove(player);
    }

    private void processLeaves(Player player, Block start, ItemStack item) {
        int remaining = 20;

        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty() && remaining > 0) {
            Block block = queue.poll();
            if (!visited.add(block)) continue;
            if (!isLeaf(block.getType())) continue;

            block.breakNaturally(item);
            remaining--;

            if (!applyDurabilityDamage(player, item)) break;

            expandSearch(block, visited, queue);
        }
    }

    private void expandSearch(Block block, Set<Block> visited, Queue<Block> queue) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block b = block.getRelative(x, y, z);
                    if (!visited.contains(b) && isLeaf(b.getType())) {
                        queue.add(b);
                    }
                }
            }
        }
    }

    private boolean applyDurabilityDamage(Player player, ItemStack item) {
        return item.damage(1, player).getAmount() != 0;
    }

    private boolean isLeaf(Material mat) {
        return mat.name().endsWith("_LEAVES")
                || mat == Material.AZALEA_LEAVES
                || mat == Material.FLOWERING_AZALEA_LEAVES;
    }
}
