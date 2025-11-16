package de.ftscraft.ftstools.commands;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.items.ItemStore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ToolsCommand implements CommandExecutor {

    private final FTSTools plugin;

    public ToolsCommand(FTSTools plugin) {
        this.plugin = plugin;
        //noinspection DataFlowIssue
        plugin.getCommand("tools").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!sender.hasPermission("tools.debug")) {
            return true;
        }

        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length > 0 && args[0].equals("reload")) {
            plugin.onDisable();
            plugin.onEnable();
            sender.sendMessage("Reload fertig!");
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 9 * 6);
        inv.setContents(ItemStore.getAllItems().toArray(new ItemStack[0]));
        player.openInventory(inv);


        return true;
    }
}
