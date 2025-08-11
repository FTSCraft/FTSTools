package de.ftscraft.ftstools.custom.bucket.objective;

import de.ftscraft.ftstools.custom.bucket.Large_Bukket_Listener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class LargeBukket {

    private ItemStack itemStack;
    private NamespacedKey key = Large_Bukket_Listener.namespacedKey;

    public LargeBukket(ItemStack itemStack) {
        if(itemStack.getType().equals(Material.BUCKET) || itemStack.getType().equals(Material.LAVA_BUCKET) || itemStack.getType().equals(Material.WATER_BUCKET)){
            if(itemStack.hasItemMeta()){
                if(itemStack.getPersistentDataContainer().has(key)){
                    this.itemStack = itemStack;
                }
            }
        }
    }

    public boolean success(){
        return itemStack!=null;
    }

    public Integer getVolume(){
        if(itemStack != null){
            return this.itemStack.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        return 0;
    }

    public void setVolume(int amount){
        if(itemStack==null)return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER,  amount);
        itemStack.setItemMeta(itemMeta);
        if(amount==0){
            itemStack.setType(Material.BUCKET);
        }
        updateLore();
    }

    public void reduceVolume(){
        if(itemStack==null)return;
        if(getVolume()>0){
            setVolume(getVolume()-1);
        }
    }

    public void updateLore(){
        if(itemStack==null)return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(List.of(MiniMessage.miniMessage().deserialize("<I>Füllungen: </i>" + getVolume())));
    }

}
