package de.ftscraft.ftstools.loader;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.misc.Utils;
import de.ftscraft.ftsutils.items.ItemBuilder;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.NoSuchElementException;

public class ItemLoader {

    public ItemStack generateItem(ConfigurationSection itemSection) {

        String stringMat = itemSection.getString("material");
        if (stringMat == null) {
            //noinspection DataFlowIssue
            throw new NoSuchElementException("no material given for " + itemSection.getParent().getName());
        }

        Material mat = Material.getMaterial(stringMat);
        if (mat == null) {
            //noinspection DataFlowIssue
            throw new NoSuchElementException("no material " + stringMat + " found for section " + itemSection.getParent().getName());
        }

        String miniMsgName = itemSection.getString("name");
        if (miniMsgName == null) {
            throw new NoSuchElementException("no name given for item");
        }

        String sign = itemSection.getString("sign");
        boolean placeable = itemSection.getBoolean("placeable", false);

        ItemBuilder builder = new ItemBuilder(mat)
                .name(MiniMessage.miniMessage().deserialize(miniMsgName).decoration(TextDecoration.ITALIC, false))
                .placeable(placeable)
                .sign(sign);

        addLore(itemSection, builder);
        addEnchantments(itemSection, builder);
        addLeatherColor(itemSection, mat, builder);
        addCraftingEnv(itemSection, builder);

        ItemStack result = builder.build();

        addModelData(itemSection, result);

        return result;
    }

    private static void addLore(ConfigurationSection itemSection, ItemBuilder builder) {
        var lore = itemSection.getStringList("lore");
        lore.stream()
                .map(l -> MiniMessage.miniMessage().deserialize(l).decoration(TextDecoration.ITALIC, false))
                .forEach(builder::lore);
    }

    private static void addEnchantments(ConfigurationSection itemSection, ItemBuilder builder) {
        boolean hasEnchantments = false;
        if (!itemSection.contains("enchantments")) {
            return;
        }

        for (String ench : itemSection.getStringList("enchantments")) {
            String[] parts = ench.split(":");
            if (parts.length != 2) {
                continue;
            }

            try {
                //noinspection deprecation
                Enchantment enchantment = Enchantment.getByName(parts[0].toUpperCase());
                if (enchantment != null) {
                    int level = Integer.parseInt(parts[1]);
                    builder.enchant(enchantment, level);
                    hasEnchantments = true;
                } else {
                    FTSTools.getInstance().getLogger().warning("No enchantment " + parts[0] + " found. Can't add enchantment");
                }
            } catch (NumberFormatException formatException) {
                FTSTools.getInstance().getLogger().warning("No number given. Can't add enchantment");
            }
        }

        if (hasEnchantments) {
            builder.addFlags(ItemFlag.HIDE_ENCHANTS);
        }

    }

    private static void addLeatherColor(ConfigurationSection itemSection, Material mat, ItemBuilder builder) {
        if (itemSection.contains("color") && mat.name().startsWith("LEATHER_")) {
            String colorStr = itemSection.getString("color");
            if (colorStr == null) {
                FTSTools.getInstance().getLogger().warning("Could not get Leather color for item " + itemSection.getName());
                return;
            }
            Color color;
            try {
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
    }

    private static void addModelData(ConfigurationSection itemSection, ItemStack result) {
        if (!itemSection.contains("custom_model_data")) {
            return;
        }
        int customModelData = itemSection.getInt("custom_model_data");
        ItemMeta meta = result.getItemMeta();
        meta.setCustomModelData(customModelData);
        result.setItemMeta(meta);
    }

    private static void addCraftingEnv(ConfigurationSection itemSection, ItemBuilder builder) {
        if (!itemSection.contains("crafting-envs")) {
            return;
        }
        builder.addPDC("crafting-envs",
                Utils.transformStringListToString(itemSection.getStringList("crafting-envs")),
                PersistentDataType.STRING);
    }

    private static void addOpenCraftingEnv(ConfigurationSection itemSection, ItemBuilder builder) {
        if (!itemSection.contains("open-crafting-env")) {
            return;
        }
        builder.addPDC("open-crafting-env",
                itemSection.getString("open-crafting-env"),
                PersistentDataType.STRING);
    }

}
