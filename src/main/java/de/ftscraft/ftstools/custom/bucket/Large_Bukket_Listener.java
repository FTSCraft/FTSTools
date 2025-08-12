package de.ftscraft.ftstools.custom.bucket;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftstools.custom.bucket.objective.LargeBukket;
import de.ftscraft.ftstools.utils.FTSUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Large_Bukket_Listener implements Listener {

    public static NamespacedKey namespacedKey = new NamespacedKey(FTSTools.getInstance(), "large_bukket_volume");

    public boolean isLargeBukket(ItemStack itemStack){
        if(itemStack != null && itemStack.getItemMeta() != null){
            return itemStack.getItemMeta().getPersistentDataContainer().has(namespacedKey);
        }
        return false;
    }

    public Large_Bukket_Listener(FTSTools ftsTools) {
        Bukkit.getPluginManager().registerEvents(this, ftsTools);

        ItemStack itemStack = new ItemStack(Material.BUCKET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(MiniMessage.miniMessage().deserialize("Großer Eimer"));
        itemMeta.lore(List.of(MiniMessage.miniMessage().deserialize("<white><i>Füllungen: </i>0")));
        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 0);
        itemStack.setItemMeta(itemMeta);

        ShapedRecipe Large_Bukket = new ShapedRecipe(new NamespacedKey(FTSTools.getInstance(), "large_bucket"), itemStack);
        Large_Bukket.shape("   ", "ABA", "AAA");
        Large_Bukket.setIngredient('A', Material.IRON_BLOCK);
        Large_Bukket.setIngredient('B', Material.BUCKET);
        Bukkit.addRecipe(Large_Bukket);
        Bukkit.getConsoleSender().sendMessage("Rezept 'Large_Bukket' registiert!");
        check();
    }

    public void check(){
        Bukkit.getScheduler().runTaskTimer(FTSTools.getInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    FTSUser user = new FTSUser(player.getUniqueId());
                    if(!player.hasDiscoveredRecipe(new NamespacedKey(FTSTools.getInstance(), "large_bucket"))){
                        if(player.hasPermission("ftstools.tool.large_bucket") || user.hasRequiredSkill()){
                            player.discoverRecipe(new NamespacedKey(FTSTools.getInstance(), "large_bucket"));
                        }
                    }else {
                        System.out.println("a");
                        if(!player.hasPermission("ftstools.tool.large_bucket") && !user.hasRequiredSkill()){
                            System.out.println("Does not have perms, remove recipe...");
                            player.undiscoverRecipe(new NamespacedKey(FTSTools.getInstance(), "large_bucket"));
                        }
                    }
                });
            }
        }, 0 , 20);
    }

//    @EventHandler(priority = EventPriority.HIGH)
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        if(event.getClickedBlock()!=null&&event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
//            ItemStack itemStack = event.getItem();
//            if(itemStack==null)return;
//            if(isLargeBukket(itemStack)){
//                LargeBukket largeBukket = new LargeBukket(itemStack);
//                if(!largeBukket.success())return;
//                if(largeBukket.getVolume()==0&&itemStack.getType().equals(Material.BUCKET)){
//                    if(event.getClickedBlock().getType().equals(Material.WATER)&&event.getClickedBlock().getType().equals(Material.LAVA)){
//                        largeBukket.setVolume(100);
//                    }
//                }else {
//                    largeBukket.reduceVolume();
//                }
//            }
//        }
//    }

    @EventHandler
    public void onPlayerBucket(PlayerBucketFillEvent event) {
        ItemStack inHand = event.getPlayer().getInventory().getItem(event.getHand());
        if(!isLargeBukket(inHand))return;
        if(event.getBlockClicked().getType().equals(Material.WATER)){
            ItemStack itemStack = new ItemStack(Material.WATER_BUCKET);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(MiniMessage.miniMessage().deserialize("Großer Wassereimer"));
            itemMeta.lore(List.of(MiniMessage.miniMessage().deserialize("<white><i>Füllungen: </i>100")));
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 100);
            itemStack.setItemMeta(itemMeta);
            event.setItemStack(itemStack);
        }
        if(event.getBlockClicked().getType().equals(Material.LAVA)){
            ItemStack itemStack = new ItemStack(Material.LAVA_BUCKET);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(MiniMessage.miniMessage().deserialize("Großer Lavaeimer"));
            itemMeta.lore(List.of(MiniMessage.miniMessage().deserialize("<white><i>Füllungen: </i>100")));
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 100);
            itemStack.setItemMeta(itemMeta);
            event.setItemStack(itemStack);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        ItemStack inHand = event.getPlayer().getInventory().getItem(event.getHand());
        if(!isLargeBukket(inHand))return;
        int volume = inHand.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        ItemStack itemStack;
        if(volume>1){
            itemStack = new ItemStack(inHand.getType());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.lore(List.of(MiniMessage.miniMessage().deserialize("<white><i>Füllungen: </i>"+(volume-1))));
            String displayname = PlainTextComponentSerializer.plainText().serialize(inHand.displayName());
            displayname = displayname.replace("[", "");
            displayname = displayname.replace("]", "");
            itemMeta.displayName(MiniMessage.miniMessage().deserialize(displayname));
            itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, volume-1);
            itemStack.setItemMeta(itemMeta);
            event.setItemStack(itemStack);
        }else {
            event.setItemStack(null);
        }
    }


}
