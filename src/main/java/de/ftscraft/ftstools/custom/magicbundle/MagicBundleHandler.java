package de.ftscraft.ftstools.custom.magicbundle;

import de.ftscraft.ftstools.FTSTools;
import de.ftscraft.ftsutils.items.ItemBuilder;
import de.ftscraft.ftsutils.items.ItemReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.persistence.PersistentDataType;

public class MagicBundleHandler {

    private final String EGG_KEY = "EGG_DATA";
    private final String TYPE_KEY = "EGG_TYPE";
    private final String SIGN_FILLED_BUNDLE = "MAGIC_BUNDLE_FILLED", SIGN_EMPTY_BUNDLE = "MAGIC_BUNDLE_EMPTY";

    public MagicBundleHandler(FTSTools plugin) {
        new BundleListener(plugin, this);
    }

    public boolean isEmptyBundle(ItemStack itemStack) {
        return SIGN_EMPTY_BUNDLE.equals(ItemReader.getSign(itemStack));
    }

    public boolean isFilledBundle(ItemStack itemStack) {
        return SIGN_FILLED_BUNDLE.equals(ItemReader.getSign(itemStack));
    }

    public boolean checkIfEntityCanBePutInBundle(ItemStack bundle, Entity entity) {
        String bundleType = ItemReader.getPDC(bundle, TYPE_KEY, PersistentDataType.STRING);
        // If bundle type is not set yet, or it matches with the given entity, the bundle can be used
        return bundleType == null || entity.getType().equals(EntityType.valueOf(bundleType));
    }

    public void fillBundle(ItemStack bundle, Entity entity) {
        saveEggDataInBundle(bundle, entity);
        String entityType = entity.getType().toString();
        new ItemBuilder(bundle)
                .mmName("<gold>Gefülltes Magisches Bündle</gold>")
                .addPDC(TYPE_KEY, entityType, PersistentDataType.STRING)
                .resetLore()
                .mmLore("<gray>Beutel für: <lang:entity.minecraft.%s>".formatted(entityType.toLowerCase()))
                .sign(SIGN_FILLED_BUNDLE)
                .build();
        entity.remove();
    }

    public void emptyOutBundle(ItemStack bundle, Location location) {
        if (!SIGN_FILLED_BUNDLE.equals(ItemReader.getSign(bundle))) {
            throw new IllegalArgumentException("itemstack given is not a filled bundle");
        }
        spawnEntitySavedInBundle(bundle, location);
        new ItemBuilder(bundle)
                .mmName("<gold>Leeres Magisches Bündel</gold>")
                .addPDC(EGG_KEY, "", PersistentDataType.STRING)
                .sign(SIGN_EMPTY_BUNDLE)
                .build();

    }

    private EntityType spawnEntitySavedInBundle(ItemStack bundle, Location spawnLocation) {
        ItemStack eggItem = Bukkit.getUnsafe().deserializeItem(ItemReader.getPDC(bundle, EGG_KEY, PersistentDataType.BYTE_ARRAY));
        SpawnEggMeta eggMeta = (SpawnEggMeta) eggItem.getItemMeta();
        eggMeta.getSpawnedEntity().createEntity(spawnLocation);
        return eggMeta.getSpawnedEntity().getEntityType();
    }

    private void saveEggDataInBundle(ItemStack stack, Entity entity) {
        ItemStack egg = generateEgg(entity);
        ItemReader.addPDC(stack, EGG_KEY, egg.serializeAsBytes(), PersistentDataType.BYTE_ARRAY);
    }

    private ItemStack generateEgg(Entity entity) {
        Material spawnEggMaterial = getSpawnEggMaterial(entity.getType());
        if (spawnEggMaterial == null)
            throw new IllegalArgumentException("tried catching entity that does not work");

        ItemStack stack = new ItemStack(getSpawnEggMaterial(entity.getType()));
        SpawnEggMeta eggMeta = (SpawnEggMeta) stack.getItemMeta();

        EntitySnapshot snapshot = entity.createSnapshot();
        if (snapshot == null) {
            throw new RuntimeException("could not create copy of entity");
        }

        eggMeta.setSpawnedEntity(snapshot);
        stack.setItemMeta(eggMeta);

        return stack;
    }

    private Material getSpawnEggMaterial(EntityType type) {
        String name = type.name() + "_SPAWN_EGG";
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return null; // z. B. für Player, EnderDragon etc.
        }
    }

}
