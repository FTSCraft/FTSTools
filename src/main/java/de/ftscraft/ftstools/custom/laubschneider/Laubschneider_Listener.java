package de.ftscraft.ftstools.custom.laubschneider;

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

import java.util.*;

public class Laubschneider_Listener implements Listener {

    List<Player> klappspatenUser = new ArrayList<>();

    public Laubschneider_Listener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (klappspatenUser.contains(event.getPlayer())) return;
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
        if (itemInHand.isEmpty() || itemInHand == null) return;
        if(itemInHand.getType().equals(Material.SHEARS)){
            String sign = ItemReader.getSign(itemInHand);
            if (sign != null && sign.equals("LAUBSCHNEIDER")) {
                Block block = event.getBlock();
                if (!isLeaf(block.getType())) return;

                int rest = 20;

                Set<Block> visited = new HashSet<>();
                Queue<Block> toCheck = new LinkedList<>();
                toCheck.add(block);

                while (!toCheck.isEmpty()) {
                    if(rest<=0)return;
                    Block current = toCheck.poll();
                    if (visited.contains(current)) continue;
                    visited.add(current);

                    if (isLeaf(current.getType())) {
                        current.breakNaturally(itemInHand);
                        rest--;
//                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
//                            onlinePlayer.playSound(current.getLocation(), Sound.BLOCK_AZALEA_LEAVES_BREAK,1,1);
//                        }
                        Damageable damageable = (Damageable) itemInHand.getItemMeta();
                        damageable.setDamage(damageable.getDamage() + 1);
                        itemInHand.setItemMeta(damageable);
                        int damage = damageable.getDamage();
                        int maxDurability = itemInHand.getType().getMaxDurability();
                        if (damage >= maxDurability) {
                            event.getPlayer().getInventory().setItemInMainHand(null); // Item entfernen
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        }

                        // Alle Nachbarn in 3D prüfen (auch diagonal)
                        for (int x = -1; x <= 1; x++) {
                            for (int y = -1; y <= 1; y++) {
                                for (int z = -1; z <= 1; z++) {
                                    Block relative = current.getRelative(x, y, z);
                                    if (!visited.contains(relative) && isLeaf(relative.getType())) {
                                        toCheck.add(relative);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isLeaf(Material material) {
        return material.name().endsWith("_LEAVES") ||
                material == Material.AZALEA_LEAVES ||
                material == Material.FLOWERING_AZALEA_LEAVES;
    }

}
