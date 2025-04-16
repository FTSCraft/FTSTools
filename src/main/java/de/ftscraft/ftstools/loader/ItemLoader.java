package de.ftscraft.ftstools.loader;

import de.ftscraft.ftsutils.items.ItemBuilder;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemLoader {

    public ItemStack generateItem(ConfigurationSection itemSection) {

        String stringMat;
        if ((stringMat = itemSection.getString("material")) == null)
            throw new RuntimeException("no material given for " + itemSection.getParent().getName());

        Material mat = Material.getMaterial(stringMat);

        if (mat == null)
            throw new RuntimeException("no material " + stringMat + " found for section " + itemSection.getParent().getName());

        String miniMsgName = itemSection.getString("name");
        List<String> lore = itemSection.getStringList("lore");
        String sign = itemSection.getString("sign");
        boolean placeable = itemSection.getBoolean("placeable", false);

        ItemBuilder builder = new ItemBuilder(mat)
                .name(MiniMessage.miniMessage().deserialize(miniMsgName).decoration(TextDecoration.ITALIC, false))
                .placeable(placeable)
                .sign(sign);

        lore.stream().map(l -> MiniMessage.miniMessage().deserialize(l).decoration(TextDecoration.ITALIC, false)).forEach(builder::lore);

        return builder.build();
    }

}
