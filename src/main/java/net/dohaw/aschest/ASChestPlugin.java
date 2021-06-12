package net.dohaw.aschest;

import net.dohaw.corelib.CoreLib;
import net.dohaw.corelib.JPUtils;
import net.dohaw.corelib.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class ASChestPlugin extends JavaPlugin {

    private final String AS_CHEST_META_KEY = "as_chest_owner";

    private ChestsConfig chestsConfig;

    private Map<UUID, List<Location>> autoSellChestLocations = new HashMap<>();

    public final NamespacedKey AS_CHEST_ITEM_KEY = NamespacedKey.minecraft("as-chest-item");
    public static ItemStack asChest;

    @Override
    public void onEnable() {

        CoreLib.setInstance(this);
        createAutoSellChest();

        JPUtils.validateFiles("chests.yml");

        this.chestsConfig = new ChestsConfig();
        this.autoSellChestLocations = chestsConfig.getAutoSellChestLocations();
        markAllChests();

        JPUtils.registerEvents(new ChestWatcher(this));
        JPUtils.registerCommand("autosellchest", new ASChestCommand());

    }

    @Override
    public void onDisable() {
        chestsConfig.saveAutoSellChestLocations(autoSellChestLocations);
    }

    private void createAutoSellChest(){
        asChest = new ItemStack(Material.CHEST);
        ItemMeta meta = asChest.getItemMeta();
        meta.setDisplayName(StringUtils.colorString("&aAuto Sell Chest"));
        meta.getPersistentDataContainer().set(AS_CHEST_ITEM_KEY, PersistentDataType.STRING, "marker");
        asChest.setItemMeta(meta);
    }

    public Map<UUID, List<Location>> getAutoSellChestLocations() {
        return autoSellChestLocations;
    }

    public boolean isAutoSellChest(Block block){
        return block.hasMetadata(AS_CHEST_META_KEY);
    }

    public boolean isAutoSellChest(Location location){
        return isAutoSellChest(location.getBlock());
    }

    public void addAutoSellChest(UUID playerUUID, Location locationChest){
        List<Location> playerAutoSellChestLocations = autoSellChestLocations.containsKey(playerUUID) ? autoSellChestLocations.get(playerUUID) : new ArrayList<>();
        playerAutoSellChestLocations.add(locationChest);
    }

    public void removeAutoSellChest(UUID playerUUID, Location locationChest){
        List<Location> playerAutoSellChestLocations = autoSellChestLocations.get(playerUUID);
        playerAutoSellChestLocations.remove(locationChest);
    }

    public boolean isAutoSellChestItem(ItemStack stack){

        if(stack == null) return false;

        ItemMeta meta = stack.getItemMeta();
        if(meta == null) return false;

        return meta.getPersistentDataContainer().has(AS_CHEST_ITEM_KEY, PersistentDataType.STRING);

    }

    public UUID getAutoSellChestOwner(Location locationChest){
        for(Map.Entry<UUID, List<Location>> entry : autoSellChestLocations.entrySet()){
            if(entry.getValue().contains(locationChest)){
                return entry.getKey();
            }
        }
        return null;
    }

    private void markAllChests(){
        for(Map.Entry<UUID, List<Location>> entry : autoSellChestLocations.entrySet()){
            for(Location location : entry.getValue()){
                location.getBlock().setMetadata(AS_CHEST_META_KEY, new FixedMetadataValue(this, entry.getKey().toString()));
            }
        }
    }

}
