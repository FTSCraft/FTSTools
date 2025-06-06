package de.ftscraft.ftstools.items;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class ItemStore {

    private static final Map<String, ItemStack> items = new TreeMap<>();

    public static ItemStack getItem(String sign) {
        return items.get(sign);
    }

    public static void addItem(String sign, ItemStack item) {
        items.put(sign, item);
    }

    public static int size() {
        return items.size();
    }

    public static Collection<ItemStack> getAllItems() {
        return items.values();
    }

}
