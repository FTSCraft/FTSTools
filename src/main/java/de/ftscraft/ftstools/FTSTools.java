package de.ftscraft.ftstools;

import de.ftscraft.ftstools.commands.ToolsCommand;
import de.ftscraft.ftstools.items.ItemStore;
import de.ftscraft.ftstools.listeners.CraftListener;
import de.ftscraft.ftstools.listeners.PlayerInteractListener;
import de.ftscraft.ftstools.loader.StartupManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class FTSTools extends JavaPlugin {
    private static FTSTools instance;

    @Override
    public void onEnable() {
        instance = this;
        StartupManager.startUp(this);

        initListeners();
        new ToolsCommand(this);
    }

    private void initListeners() {
        new CraftListener(this);
        new PlayerInteractListener(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static FTSTools getInstance() {
        return instance;
    }

    public static ItemStack getItemBySign(String sign) {
        return ItemStore.getItem(sign);
    }

}
