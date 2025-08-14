package de.ftscraft.ftstools.custom.klappspaten;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Klappspaten_Listener implements Listener {

    List<Player> klappspatenUser = new ArrayList<>();

    public Klappspaten_Listener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (klappspatenUser.contains(event.getPlayer())) return;
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInHand.isEmpty() || itemInHand == null) return;
        if(itemInHand.getType().equals(Material.DIAMOND_SHOVEL)){
            String sign = ItemReader.getSign(itemInHand);
            if (sign != null && sign.equals("KLAPPSPATEN")) {
                excavateArea(event.getBlock(), event.getPlayer(), event.getPlayer().getTargetBlockFace(10), itemInHand, event.getPlayer());
                event.getPlayer().updateInventory();
            }
        }
    }

    public void excavateArea(Block centerBlock, Player p, BlockFace direction, ItemStack item, Player player) {
        klappspatenUser.add(p);
        AtomicReference<Short> breakedBlocks = new AtomicReference<>((short) 0);
        Bukkit.getScheduler().runTask(FTSTools.getInstance(), () -> {
            if (direction == BlockFace.UP || direction == BlockFace.DOWN) {
                for (int xOffset = -1; xOffset <= 1; xOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        Block targetBlock = centerBlock.getRelative(xOffset, 0, zOffset);
                        if (isValidBlock(targetBlock.getType())) {
                            p.breakBlock(targetBlock);
                            Damageable damageable = (Damageable) item.getItemMeta();
                            damageable.setDamage(damageable.getDamage() + 1);
                            item.setItemMeta(damageable);
                            int damage = damageable.getDamage();
                            int maxDurability = item.getType().getMaxDurability();
                            if (damage >= maxDurability) {
                                player.getInventory().setItemInMainHand(null); // Item entfernen
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                            }
                        }
                    }
                }
            } else {
                if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
                    for (int yOffset = -1; yOffset <= 1; yOffset++) {
                        for (int zOffset = -1; zOffset <= 1; zOffset++) {
                            Block targetBlock = centerBlock.getRelative(0, yOffset, zOffset);
                            if (isValidBlock(targetBlock.getType())) {
                                p.breakBlock(targetBlock);
                                Damageable damageable = (Damageable) item.getItemMeta();
                                damageable.setDamage(damageable.getDamage() + 1);
                                item.setItemMeta(damageable);
                                int damage = damageable.getDamage();
                                int maxDurability = item.getType().getMaxDurability();
                                if (damage >= maxDurability) {
                                    player.getInventory().setItemInMainHand(null); // Item entfernen
                                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                }
                            }
                        }
                    }
                } else if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH) {
                    for (int yOffset = -1; yOffset <= 1; yOffset++) {
                        for (int xOffset = -1; xOffset <= 1; xOffset++) {
                            Block targetBlock = centerBlock.getRelative(xOffset, yOffset, 0);
                            if (isValidBlock(targetBlock.getType())) {
                                p.breakBlock(targetBlock);
                                Damageable damageable = (Damageable) item.getItemMeta();
                                damageable.setDamage(damageable.getDamage() + 1);
                                item.setItemMeta(damageable);
                                int damage = damageable.getDamage();
                                int maxDurability = item.getType().getMaxDurability();
                                if (damage >= maxDurability) {
                                    player.getInventory().setItemInMainHand(null); // Item entfernen
                                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                                }
                            }
                        }
                    }
                }
            }
            klappspatenUser.remove(p);
        });
    }

    private boolean isValidBlock(Material material) {
        return material == Material.DIRT ||
                material == Material.COARSE_DIRT ||
                material == Material.ROOTED_DIRT ||
                material == Material.GRASS_BLOCK ||
                material == Material.MYCELIUM ||
                material == Material.PODZOL ||
                material == Material.MUD ||
                material == Material.CLAY ||
                material == Material.GRAVEL ||
                material == Material.SAND ||
                material == Material.RED_SAND ||
                material == Material.SOUL_SAND ||
                material == Material.SOUL_SOIL ||
                material == Material.SNOW ||
                material == Material.SNOW_BLOCK ||
                material == Material.POWDER_SNOW ||
                material == Material.FARMLAND ||
                material == Material.DIRT_PATH;
    }

}
