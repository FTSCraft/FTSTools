package de.ftscraft.ftstools.custom.magischer_beutel;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftsutils.items.ItemReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.ArrayList;
import java.util.List;

public class MagischerBeutel_Listener implements Listener {

    public MagischerBeutel_Listener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack cursor = e.getCursor();
        ItemStack current = e.getCurrentItem();

//        if (!e.getClick().isRightClick()) return;

        if (isNonEmpty(cursor) && isMagicBundle(current)) {
            e.setCancelled(true);
            return;
        }

        if (isMagicBundle(cursor) && isNonEmpty(current)) {
            e.setCancelled(true);
            return;
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        // Sicherstellen, dass kein Drag auf einen Bundle-Slot Items "hineinzieht".
        // (Bundes unterstützen kein klassisches Dragging, aber zur Sicherheit.)
        ItemStack cursor = e.getOldCursor();
        if (!isMagicBundle(cursor)) return;

        // Wenn der Cursor ein Bundle ist, verhindern wir das Verteilen/Draggen über Slots mit Items.
        // (Wäre potenziell ein "ins Bundle ziehen" über Right-Mouse-Mechanik.)
        if (!e.getNewItems().isEmpty()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onUseBundleOnEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand.getType() != Material.BLUE_BUNDLE) return;
        if(!isMagicBundle(hand))return;
        BundleMeta bundleMeta = (BundleMeta) hand.getItemMeta();
        List<Component> lore = bundleMeta.lore();
        String nutungenStr = PlainTextComponentSerializer.plainText().serialize(lore.get(1));
        String[] nutzungenStrGetrennt = nutungenStr.split(" ");
        int nutzungen = Integer.parseInt(nutzungenStrGetrennt[1]);
        lore.set(1, MiniMessage.miniMessage().deserialize("Nutzungen: " + (nutzungen-1)));
        bundleMeta.lore(lore);

        if(nutzungen<=0)return;

        Entity bukkitEntity = e.getRightClicked();
        EntityType type = bukkitEntity.getType();

        // Spawn-Ei-Material ermitteln
        Material eggMat = spawnEggMaterial(type);
        if (eggMat == null) {
            p.sendMessage(Component.text("Für " + type + " gibt es kein Spawn-Ei."));
            return;
        }

        // Spawn-Ei erzeugen
        ItemStack egg = new ItemStack(eggMat);
        SpawnEggMeta meta = (SpawnEggMeta) egg.getItemMeta();
        if (meta != null) {
            meta.setSpawnedEntity(bukkitEntity.createSnapshot());
            egg.setItemMeta(meta);
        }


        bundleMeta.addItem(egg);
        bukkitEntity.remove();
        hand.setItemMeta(bundleMeta);
        e.getPlayer().sendMessage("Eingefangen!");

//        // Ei ins Bundle packen (oder droppen, wenn „zu voll“)
//        if (!addToBundle(hand, egg)) {
//            bukkitEntity.getWorld().dropItemNaturally(bukkitEntity.getLocation(), egg);
//            p.sendMessage(Component.text("Bundle ist voll – Spawn-Ei gedroppt."));
//        } else {
//            // Entity „fangen“: entfernen
//            bukkitEntity.remove();
//            p.swingMainHand();
//        }

        e.setCancelled(true);
    }

    private boolean isMagicBundle(ItemStack stack) {
        if(stack != null && stack.getType() == Material.BLUE_BUNDLE){
            String sign = ItemReader.getSign(stack);
            return sign.equals("MAGIC_BUNDLE");
        }
        return false;
    }

    private boolean isNonEmpty(ItemStack stack) {
        return stack != null && stack.getType() != Material.AIR && stack.getAmount() > 0;
    }

    private boolean addToBundle(ItemStack bundle, ItemStack toAdd) {
        if (bundle == null || bundle.getType() != Material.BUNDLE) return false;
        ItemMeta im = bundle.getItemMeta();
        if (!(im instanceof BundleMeta bm)) return false;

        List<ItemStack> items = new ArrayList<>(bm.getItems());
        items.add(toAdd);

        // Wenn das Setzen scheitert (z. B. weil Gewicht > 64), abbrechen:
        try {
            bm.setItems(items);
            bundle.setItemMeta(bm);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private Material spawnEggMaterial(EntityType type) {
        String name = type.name() + "_SPAWN_EGG";
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null; // z. B. für Player, EnderDragon etc.
        }
    }

}
