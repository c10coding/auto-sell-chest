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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public final class ASChestPlugin extends JavaPlugin {

    private ChestsConfig chestsConfig;

    private List<Location> autoSellChestLocations = new ArrayList<>();

    public final NamespacedKey AS_CHEST_ITEM_KEY = NamespacedKey.minecraft("as-chest-item");
    public static ItemStack asChest;

    @Override
    public void onEnable() {

        CoreLib.setInstance(this);
        createAutoSellChest();

        JPUtils.validateFiles("chests.yml");

        this.chestsConfig = new ChestsConfig();
        this.autoSellChestLocations = chestsConfig.getAutoSellChestLocations();

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

    public List<Location> getAutoSellChestLocations() {
        return autoSellChestLocations;
    }

    public boolean isAutoSellChest(Block block){
        return isAutoSellChest(block.getLocation());
    }

    public boolean isAutoSellChest(Location location){
        return autoSellChestLocations.contains(location);
    }

    public boolean isAutoSellChestItem(ItemStack stack){

        if(stack == null) return false;

        ItemMeta meta = stack.getItemMeta();
        if(meta == null) return false;

        return meta.getPersistentDataContainer().has(AS_CHEST_ITEM_KEY, PersistentDataType.STRING);

    }

}
