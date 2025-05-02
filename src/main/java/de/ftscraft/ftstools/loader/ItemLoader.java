package de.ftscraft.ftstools.loader;

import de.ftscraft.ftsutils.items.ItemBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLoader {
    public ItemStack generateItem(ConfigurationSection itemSection) {
        String stringMat = itemSection.getString("material");
        if (stringMat == null) {
            throw new RuntimeException("no material given for " + itemSection.getParent().getName());
        }
        Material mat = Material.getMaterial(stringMat);
        if (mat == null) {
            throw new RuntimeException("no material " + stringMat + " found for section " + itemSection.getParent().getName());
        }

        String miniMsgName = itemSection.getString("name");
        List<String> lore = itemSection.getStringList("lore");
        String sign = itemSection.getString("sign");
        boolean placeable = itemSection.getBoolean("placeable", false);
        assert miniMsgName != null;
        ItemBuilder builder = new ItemBuilder(mat)
            .name(MiniMessage.miniMessage().deserialize(miniMsgName).decoration(TextDecoration.ITALIC, false))
            .placeable(placeable)
            .sign(sign);
        Stream<Component> loreStream = lore.stream()
            .map(l -> MiniMessage.miniMessage().deserialize(l).decoration(TextDecoration.ITALIC, false));
        Objects.requireNonNull(builder);
        loreStream.forEach(builder::lore);

        boolean hasEnchantments = false;
        if (itemSection.contains("enchantments")) {
            for (String ench : itemSection.getStringList("enchantments")) {
                String[] parts = ench.split(":");
                if (parts.length == 2) {
                    try {
                        Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
                        int level = Integer.parseInt(parts[1]);
                        if (enchantment != null) {
                            builder.enchant(enchantment, level);
                            hasEnchantments = true;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (hasEnchantments) {
            builder.addFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        }

        // Handle color for leather items
        if (itemSection.contains("color") && mat.name().startsWith("LEATHER_")) {
            String colorStr = itemSection.getString("color");
            Color color = null;
            try {
                assert colorStr != null;
                if (colorStr.startsWith("#")) {
                    java.awt.Color awtColor = java.awt.Color.decode(colorStr);
                    color = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
                } else if (colorStr.contains(",")) {
                    String[] rgb = colorStr.split(",");
                    color = Color.fromRGB(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()));
                } else {
                    color = (Color) Color.class.getField(colorStr.toUpperCase()).get(null);
                }
                builder.color(color);
            } catch (Exception ignored) {
            }
        }

        ItemStack result = builder.build();

        // Set custom model data
        if (itemSection.contains("custom_model_data")) {
            int customModelData = itemSection.getInt("custom_model_data");
            ItemMeta meta = result.getItemMeta();
            meta.setCustomModelData(customModelData);
            result.setItemMeta(meta);
        }

        // Hide enchantments
        if (hasEnchantments) {
            ItemMeta meta = result.getItemMeta();
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            result.setItemMeta(meta);
        }

        return result;
    }
}
