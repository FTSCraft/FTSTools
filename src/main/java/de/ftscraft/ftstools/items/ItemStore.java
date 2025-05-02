package de.ftscraft.ftstools.items;

import org.bukkit.inventory.ItemStack;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.plugin.Plugin;

public class ItemStore {

    private static final Map<String, ItemStack> items = new TreeMap<>();

    public static ItemStack getItem(String sign) {
        return items.get(sign);
    }

    public static void addItem(String sign, ItemStack item) {
        items.put(sign, item);
    }

    public static void logAllItemsLoaded(Plugin plugin) {
        plugin.getLogger().info("All custom items have been loaded. Total: " + items.size());
    }
}
