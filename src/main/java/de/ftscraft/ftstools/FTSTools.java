package de.ftscraft.ftstools;

import de.ftscraft.ftstools.loader.StartupManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FTSTools extends JavaPlugin {

    private static FTSTools instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        new StartupManager().startUp();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static FTSTools getInstance() {
        return instance;
    }
}
