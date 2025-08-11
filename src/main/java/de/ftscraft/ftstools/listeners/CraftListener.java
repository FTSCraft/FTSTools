package de.ftscraft.ftstools.listeners;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.custom.bucket.Large_Bukket_Listener;
import de.ftscraft.ftstools.environments.CraftingEnvManager;
import de.ftscraft.ftstools.environments.CraftingEnvironment;
import de.ftscraft.ftstools.loader.StartupManager;
import de.ftscraft.ftstools.misc.Utils;
import de.ftscraft.ftsutils.items.ItemReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftListener implements Listener {
    private final Set<String> allSigns;

    public CraftListener(FTSTools plugin) {
        allSigns = StartupManager.getAllConfigSigns();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {

        CraftingInventory inv = event.getInventory();
        ItemStack result = inv.getResult();
        String resultSign = result != null ? ItemReader.getSign(result) : null;

        handleCraftingEnv(event, resultSign);

        blockUsingCustomItemsForNormalRecipes(inv, resultSign);
    }

    private void blockUsingCustomItemsForNormalRecipes(CraftingInventory inv, String resultSign) {
        ItemStack[] matrix = inv.getMatrix();

        if (resultSign != null && allSigns.contains(resultSign)) {
            return;
        }
        for (ItemStack item : matrix) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            String sign = ItemReader.getSign(item);
            if (sign == null || !allSigns.contains(sign)) {
                continue;
            }
            inv.setResult(null);
            return;
        }
    }

    private void handleCraftingEnv(PrepareItemCraftEvent event, String sign) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();

        // Wenn das Ergebnis null ist, gibt es nichts zu prüfen oder anzupassen
        if (result == null) {
            return;
        }

        // Extrahiert die Crafting-Umgebungs-ID aus dem Titel des Crafting-Fensters
        String id = getEnvIdFromComponent(event.getView().title());

        // Liest die Umgebungen, in denen das Item gecraftet werden darf
        String itemCraftableIn = ItemReader.getPDC(result, Utils.PDC_CRAFTING_ENVS, PersistentDataType.STRING);

        // Wenn keine Umgebung gefunden wurde, aber das Item eine bestimmte Umgebung zum Craften benötigt,
        // dann wird das Crafting blockiert
        if (id == null) {
            if (itemCraftableIn != null) {
                inventory.setResult(null);
            }
            return;
        }

        // Holt die Crafting-Umgebung anhand der ID
        CraftingEnvironment environment = CraftingEnvManager.getCraftingEnv(id);

        // Wenn die Umgebung das normale Crafting nicht erlaubt und kein "sign" gesetzt ist, blockiere das Crafting
        if (!environment.canCraftNormalItems() && sign == null && itemCraftableIn == null) {
            inventory.setResult(null);
            return;
        }

        // Wenn das Item keine Craftingbeschränkung hat und man in der Umgebung diese Items craften darf
        // nicht blockieren
        if (itemCraftableIn == null) {
            return;
        }

        // Wenn die aktuelle Umgebung nicht zu den erlaubten passt, wird das Crafting blockiert
        if (!itemCraftableIn.contains(environment.id())) {
            inventory.setResult(null);
        }

    }


    private String getEnvIdFromComponent(Component component) {
        String input = JSONComponentSerializer.json().serialize(component);
        Pattern pattern = Pattern.compile("\"storage\"\\s*:\\s*\"minecraft:([^\"]+)\"");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}