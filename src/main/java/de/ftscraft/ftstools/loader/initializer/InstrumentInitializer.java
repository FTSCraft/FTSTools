package de.ftscraft.ftstools.loader.initializer;

import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftsutils.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;

public class InstrumentInitializer implements ItemInitializer {

    public void initializeItems() {
        ItemStore.addItem("LAUTE", new ItemBuilder(Material.STICK).sign("LAUTE").name(Component.text("Laute").color(NamedTextColor.RED)).build());
    }

}
