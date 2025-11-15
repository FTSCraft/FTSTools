package de.ftscraft.ftstools.custom.klappspaten;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KlappspatenListener implements Listener {

    private final List<Player> klappspatenUser = new ArrayList<>();

    public KlappspatenListener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (klappspatenUser.contains(player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isEmpty() || item.getType() != Material.DIAMOND_SHOVEL) return;

        String sign = ItemReader.getSign(item);
        if (!"KLAPPSPATEN".equals(sign)) return;

        BlockFace face = player.getTargetBlockFace(10);
        excavateArea(event.getBlock(), player, face, item);

        player.updateInventory();
    }

    private void excavateArea(Block center, Player player, BlockFace direction, ItemStack item) {
        klappspatenUser.add(player);

        Bukkit.getScheduler().runTask(FTSTools.getInstance(), () -> {
            int[][] offsets = chooseOffsets(direction);

            for (int[] o : offsets) {
                Block target = center.getRelative(o[0], o[1], o[2]);
                attemptBreak(player, item, target);
            }

            klappspatenUser.remove(player);
        });
    }

    private int[][] chooseOffsets(BlockFace direction) {
        if (direction == BlockFace.UP || direction == BlockFace.DOWN) {
            return new int[][] {
                    {-1,0,-1},{-1,0,0},{-1,0,1},
                    { 0,0,-1},{ 0,0,0},{ 0,0,1},
                    { 1,0,-1},{ 1,0,0},{ 1,0,1}
            };
        }

        if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
            return new int[][] {
                    {0,-1,-1},{0,-1,0},{0,-1,1},
                    {0, 0,-1},{0, 0,0},{0, 0,1},
                    {0, 1,-1},{0, 1,0},{0, 1,1}
            };
        }

        return new int[][] {
                {-1,-1,0},{0,-1,0},{1,-1,0},
                {-1, 0,0},{0, 0,0},{1, 0,0},
                {-1, 1,0},{0, 1,0},{1, 1,0}
        };
    }

    private void attemptBreak(Player p, ItemStack item, Block block) {
        if (!isValidBlock(block.getType())) return;

        p.breakBlock(block);
        item.damage(1, p);
    }

    private boolean isValidBlock(Material m) {
        return switch (m) {
            case DIRT, COARSE_DIRT, ROOTED_DIRT, GRASS_BLOCK,
                 MYCELIUM, PODZOL, MUD, CLAY, GRAVEL, SAND, RED_SAND,
                 SOUL_SAND, SOUL_SOIL, SNOW, SNOW_BLOCK, POWDER_SNOW,
                 FARMLAND, DIRT_PATH -> true;
            default -> false;
        };
    }
}
