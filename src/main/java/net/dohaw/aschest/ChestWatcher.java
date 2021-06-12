package net.dohaw.aschest;

import de.takacick.coinapi.CoinAPI;
import de.takacick.shop.Shop;
import net.dohaw.corelib.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestWatcher implements Listener {

    private final Map<UUID, Integer> sellingSessions = new HashMap<>();

    private Map<Material, Integer> pricesPerItem;
    private ASChestPlugin plugin;

    public ChestWatcher(ASChestPlugin plugin){
        this.plugin = plugin;
        this.pricesPerItem = Shop.getInstance().getConfiguration().getSell();
    }

    @EventHandler
    public void onPlaceChest(BlockPlaceEvent e){

        Block blockPlaced = e.getBlockPlaced();
        ItemStack itemInHand = e.getItemInHand();
        if(plugin.isAutoSellChestItem(itemInHand)){
            Player player = e.getPlayer();
            plugin.addAutoSellChest(player.getUniqueId(), blockPlaced.getLocation());
            player.sendMessage(StringUtils.colorString("&aYou have placed down an auto sell chest!"));
        }

    }

    @EventHandler
    public void onOpenASChest(InventoryOpenEvent e){

        Inventory inv = e.getInventory();
        if(!plugin.isAutoSellChest(inv.getLocation())) return;

        Player player = (Player) e.getPlayer();
        sellingSessions.put(player.getUniqueId(), 0);

    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e)
    {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        Inventory clickedInventory = e.getClickedInventory();
        ItemStack current = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        if (!plugin.isAutoSellChest(inv.getLocation()) || clickedInventory == null || cursor == null) return;

        boolean isClickingTopInv = clickedInventory.equals(e.getView().getTopInventory());
        boolean isShiftClickIntoInv = e.isShiftClick() && current != null && current.getType() != Material.AIR && !isClickingTopInv;
        boolean isPuttingIntoInv = current != null && current.getType() == Material.AIR && isClickingTopInv && cursor.getType() != Material.AIR;

        if (isShiftClickIntoInv || isPuttingIntoInv) {
            int amount = isShiftClickIntoInv ? current.getAmount() : cursor.getAmount();
            Material material = isShiftClickIntoInv ? current.getType() : cursor.getType();
            e.setCancelled(true);
            if(pricesPerItem.containsKey(material)){

                int sellPrice = pricesPerItem.get(material) * amount;

                CoinAPI.getInstance().getCoinDatabase().addCoins(p.getUniqueId(), sellPrice);

                if(isPuttingIntoInv){
                    p.setItemOnCursor(new ItemStack(Material.AIR));
                }else{
                    e.setCurrentItem(new ItemStack(Material.AIR));
                }
                p.playSound(p.getLocation(), Sound.ENTITY_PAINTING_PLACE, 0.5f, 0);

                // Update the sell session revenue amount
                int currentRevenue = sellingSessions.get(p.getUniqueId());
                sellingSessions.put(p.getUniqueId(), currentRevenue + sellPrice);

            }else{
                p.sendMessage(StringUtils.colorString("&cYou can't sell this item!"));
            }
        }

    }

    /*
        Notifies the chest owner every single time someone s
     */
    @EventHandler
    public void onCloseASChest(InventoryCloseEvent e){

        Inventory inv = e.getInventory();
        Location invLocation = inv.getLocation();
        if(invLocation != null && plugin.isAutoSellChest(invLocation)){

            UUID chestOwnerUUID = plugin.getAutoSellChestOwner(invLocation);

            Player seller = (Player) e.getPlayer();
            Player chestOwner = Bukkit.getPlayer(chestOwnerUUID);
            int revenue = sellingSessions.remove(seller.getUniqueId());

            if(revenue != 0 && chestOwner != null){
                chestOwner.sendMessage(StringUtils.colorString("&a+&6&b"+ revenue));
            }

        }

    }

    @EventHandler
    public void onBreakChest(BlockBreakEvent e){

        Block block = e.getBlock();
        if(plugin.isAutoSellChest(block)){
            Player player = e.getPlayer();
            plugin.removeAutoSellChest(player.getUniqueId(), block.getLocation());
            player.sendMessage(StringUtils.colorString("&bYou have broken a auto sell chest!"));
        }

    }

}
