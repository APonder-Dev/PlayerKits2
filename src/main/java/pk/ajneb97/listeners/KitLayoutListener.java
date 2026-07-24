package pk.ajneb97.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pk.ajneb97.PlayerKits2;
import pk.ajneb97.managers.KitLayoutManager;
import pk.ajneb97.model.inventory.InventoryPlayer;

public class KitLayoutListener implements Listener {

    private PlayerKits2 plugin;
    public KitLayoutListener(PlayerKits2 plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void clickInventory(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        KitLayoutManager manager = plugin.getKitLayoutManager();
        InventoryPlayer inventoryPlayer = manager.getInventoryPlayer(player);
        if(inventoryPlayer != null){
            manager.clickInventory(inventoryPlayer,event);
        }
    }

    @EventHandler
    public void dragInventory(InventoryDragEvent event){
        Player player = (Player) event.getWhoClicked();
        KitLayoutManager manager = plugin.getKitLayoutManager();
        if(manager.getInventoryPlayer(player) != null){
            //No legitimate use case needs multi-slot stack dragging in a fixed layout editor.
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        KitLayoutManager manager = plugin.getKitLayoutManager();
        InventoryPlayer inventoryPlayer = manager.getInventoryPlayer(player);
        if(inventoryPlayer != null){
            manager.saveLayoutFromInventory(inventoryPlayer);
            manager.removeInventoryPlayer(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        KitLayoutManager manager = plugin.getKitLayoutManager();
        InventoryPlayer inventoryPlayer = manager.getInventoryPlayer(player);
        if(inventoryPlayer != null){
            manager.saveLayoutFromInventory(inventoryPlayer);
            manager.removeInventoryPlayer(player);
        }
    }
}
