package de.ftscraft.ftstools.custom.bucket;

import de.ftscraft.ftsutils.items.ItemBuilder;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class LargeBucketManager {

    public static final String VOLUME_KEY = "BUCKET_VOLUME";
    public static final int MAX_FILL = 15;

    public static Integer getVolume(@NotNull ItemStack itemStack) {
        return ItemReader.getPDC(itemStack, VOLUME_KEY, PersistentDataType.INTEGER);
    }

    public static void setVolume(@NotNull ItemStack itemStack, int amount) {
        new ItemBuilder(itemStack)
                .resetLore()
                .mmLore("<gray>Füllungen: %d</gray>".formatted(amount))
                .addPDC(VOLUME_KEY, amount, PersistentDataType.INTEGER)
                .build();
    }

    public static boolean isLargeBukket(@NotNull ItemStack itemStack) {
        return "LARGE_BUCKET".equals(ItemReader.getSign(itemStack));
    }

}
