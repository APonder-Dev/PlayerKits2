package pk.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pk.ajneb97.PlayerKits2;
import pk.ajneb97.configs.MainConfigManager;
import pk.ajneb97.model.Kit;
import pk.ajneb97.model.internal.KitLayoutPosition;
import pk.ajneb97.model.inventory.InventoryPlayer;
import pk.ajneb97.model.item.KitItem;
import pk.ajneb97.utils.InventoryItem;
import pk.ajneb97.utils.InventoryUtils;
import pk.ajneb97.utils.ItemUtils;
import pk.ajneb97.utils.MiniMessageUtils;
import pk.ajneb97.utils.OtherUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the per-player virtual kit layout editor: a GUI where a player can freely
 * rearrange the items of a kit into their own preferred slots before claiming it.
 * All items shown here are virtual (freshly created from the kit's configuration) and
 * are never given to the player from this inventory - only the resulting slot layout
 * is persisted, and it is applied at claim time by KitsManager.
 */
public class KitLayoutManager {

    public static final int HELMET_SLOT = 9;
    public static final int CHESTPLATE_SLOT = 10;
    public static final int LEGGINGS_SLOT = 11;
    public static final int BOOTS_SLOT = 12;
    public static final int OFFHAND_SLOT = 13;
    public static final int RESET_SLOT = 16;
    public static final int BACK_SLOT = 17;
    public static final int INTERACTIVE_START = 18;
    public static final int INTERACTIVE_END = 53;
    public static final int INTERACTIVE_OFFSET = 18;

    public static final String TAG_INDEX = "playerkits_layout_index";
    public static final String TAG_ID = "playerkits_layout_id";

    private PlayerKits2 plugin;
    private ArrayList<InventoryPlayer> players;

    public KitLayoutManager(PlayerKits2 plugin){
        this.plugin = plugin;
        this.players = new ArrayList<>();
    }

    public ArrayList<InventoryPlayer> getPlayers() {
        return players;
    }

    public InventoryPlayer getInventoryPlayer(Player player){
        for(InventoryPlayer inventoryPlayer : players){
            if(inventoryPlayer.getPlayer().equals(player)){
                return inventoryPlayer;
            }
        }
        return null;
    }

    public void removeInventoryPlayer(Player player){
        for(int i=0;i<players.size();i++){
            if(players.get(i).getPlayer().equals(player)){
                players.remove(i);
            }
        }
    }

    public void openInventory(InventoryPlayer inventoryPlayer){
        Kit kit = plugin.getKitsManager().getKitByName(inventoryPlayer.getKitName());
        Player player = inventoryPlayer.getPlayer();
        if(kit == null){
            return;
        }

        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        if(!mainConfigManager.isKitLayoutEnabled()){
            sendConfigMessage(player,"kitLayoutDisabled");
            return;
        }

        inventoryPlayer.setInventoryName("kit_layout");

        String title = mainConfigManager.getKitLayoutTitle().replace("%kit%",kit.getName());
        Inventory inv;
        if(mainConfigManager.isUseMiniMessage()){
            inv = MiniMessageUtils.createInventory(54,title);
        }else{
            inv = Bukkit.createInventory(null,54,MessagesManager.getLegacyColoredMessage(title));
        }

        populateInventory(inv,inventoryPlayer,kit);

        player.openInventory(inv);
        players.add(inventoryPlayer);
    }

    private void sendConfigMessage(Player player,String key){
        MessagesManager msgManager = plugin.getMessagesManager();
        msgManager.sendMessage(player,plugin.getConfigsManager().getMessagesConfigManager().getConfig().getString(key),true);
    }

    private void populateInventory(Inventory inv, InventoryPlayer inventoryPlayer, Kit kit){
        Player player = inventoryPlayer.getPlayer();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        KitItemManager kitItemManager = plugin.getKitItemManager();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

        inv.clear();

        //Border decoration
        for(int i=0;i<9;i++){
            decorate(inv,i);
        }
        decorate(inv,14);
        decorate(inv,15);

        //Reset button
        Material resetMaterial = parseMaterial(mainConfigManager.getKitLayoutResetButtonMaterial(),Material.BARRIER);
        new InventoryItem(inv,RESET_SLOT,resetMaterial)
                .name(mainConfigManager.getKitLayoutResetButtonName())
                .lore(new ArrayList<>(mainConfigManager.getKitLayoutResetButtonLore()))
                .ready();

        //Back button
        Material backMaterial = parseMaterial(mainConfigManager.getKitLayoutBackButtonMaterial(),Material.ARROW);
        new InventoryItem(inv,BACK_SLOT,backMaterial)
                .name(mainConfigManager.getKitLayoutBackButtonName())
                .lore(new ArrayList<>(mainConfigManager.getKitLayoutBackButtonLore()))
                .ready();

        //Split kit items into armor/offhand (fixed display) and storage (interactive) items
        ArrayList<KitItem> items = kit.getItems();
        KitItem helmetItem = null, chestplateItem = null, leggingsItem = null, bootsItem = null, offhandItem = null;

        for(KitItem kitItem : items){
            if(kit.isAutoArmor()){
                String id = kitItem.getStableId();
                if(id != null){
                    if(helmetItem == null && (id.contains("_HELMET") || id.contains("PLAYER_HEAD") || id.contains("SKULL_ITEM"))){
                        helmetItem = kitItem;
                        continue;
                    }else if(chestplateItem == null && (id.contains("_CHESTPLATE") || id.contains("ELYTRA"))){
                        chestplateItem = kitItem;
                        continue;
                    }else if(leggingsItem == null && id.contains("_LEGGINGS")){
                        leggingsItem = kitItem;
                        continue;
                    }else if(bootsItem == null && id.contains("_BOOTS")){
                        bootsItem = kitItem;
                        continue;
                    }
                }
            }
            if(offhandItem == null && kitItem.isOffhand()){
                offhandItem = kitItem;
                continue;
            }
        }

        placeArmorDisplay(inv,HELMET_SLOT,helmetItem,player,kit,kitItemManager);
        placeArmorDisplay(inv,CHESTPLATE_SLOT,chestplateItem,player,kit,kitItemManager);
        placeArmorDisplay(inv,LEGGINGS_SLOT,leggingsItem,player,kit,kitItemManager);
        placeArmorDisplay(inv,BOOTS_SLOT,bootsItem,player,kit,kitItemManager);
        placeArmorDisplay(inv,OFFHAND_SLOT,offhandItem,player,kit,kitItemManager);

        //Remaining items go into the interactive area
        boolean[] usedGuiSlots = new boolean[54];
        List<Integer> pendingIndexes = new ArrayList<>();

        for(int index=0;index<items.size();index++){
            KitItem kitItem = items.get(index);
            if(kitItem == helmetItem || kitItem == chestplateItem || kitItem == leggingsItem
                    || kitItem == bootsItem || kitItem == offhandItem){
                continue;
            }

            Integer savedSlot = playerDataManager.getKitLayoutSlot(player,kit.getName(),index,kitItem.getStableId());
            int guiSlot = -1;
            if(savedSlot != null && savedSlot >= 0 && savedSlot <= 35){
                int candidate = savedSlot + INTERACTIVE_OFFSET;
                if(candidate >= INTERACTIVE_START && candidate <= INTERACTIVE_END && !usedGuiSlots[candidate]){
                    guiSlot = candidate;
                }
            }

            if(guiSlot != -1){
                placeStorageItem(inv,guiSlot,index,kitItem,player,kit,kitItemManager);
                usedGuiSlots[guiSlot] = true;
            }else{
                pendingIndexes.add(index);
            }
        }

        //Fill unpositioned items into the first free interactive slots
        int cursor = INTERACTIVE_START;
        for(int index : pendingIndexes){
            while(cursor <= INTERACTIVE_END && usedGuiSlots[cursor]){
                cursor++;
            }
            if(cursor > INTERACTIVE_END){
                break;
            }
            KitItem kitItem = items.get(index);
            placeStorageItem(inv,cursor,index,kitItem,player,kit,kitItemManager);
            usedGuiSlots[cursor] = true;
            cursor++;
        }
    }

    private void placeArmorDisplay(Inventory inv,int slot,KitItem kitItem,Player player,Kit kit,KitItemManager kitItemManager){
        if(kitItem == null){
            decorate(inv,slot);
            return;
        }
        ItemStack item = kitItemManager.createItemFromKitItem(kitItem,player,kit);
        inv.setItem(slot,item);
    }

    private void placeStorageItem(Inventory inv,int guiSlot,int index,KitItem kitItem,Player player,Kit kit,KitItemManager kitItemManager){
        ItemStack item = kitItemManager.createItemFromKitItem(kitItem,player,kit);
        item = ItemUtils.setTagStringItem(plugin,item,TAG_INDEX,index+"");
        String stableId = kitItem.getStableId();
        item = ItemUtils.setTagStringItem(plugin,item,TAG_ID,stableId != null ? stableId : "");
        inv.setItem(guiSlot,item);
    }

    private void decorate(Inventory inv,int slot){
        if(OtherUtils.isLegacy()){
            new InventoryItem(inv,slot,Material.valueOf("STAINED_GLASS_PANE")).dataValue((short) 15).name("").ready();
        }else{
            new InventoryItem(inv,slot,Material.BLACK_STAINED_GLASS_PANE).name("").ready();
        }
    }

    private Material parseMaterial(String name,Material fallback){
        if(name == null){
            return fallback;
        }
        Material material = Material.matchMaterial(name);
        return material != null ? material : fallback;
    }

    public void clickInventory(InventoryPlayer inventoryPlayer, InventoryClickEvent event){
        Player player = inventoryPlayer.getPlayer();
        event.setCancelled(true);

        if(event.getClickedInventory() == null){
            return;
        }

        Inventory top = InventoryUtils.getTopInventory(player);
        if(!event.getClickedInventory().equals(top)){
            return;
        }

        int slot = event.getSlot();
        ClickType clickType = event.getClick();

        if(slot == RESET_SLOT){
            resetLayout(inventoryPlayer);
            return;
        }
        if(slot == BACK_SLOT){
            goBack(inventoryPlayer);
            return;
        }

        if(slot >= INTERACTIVE_START && slot <= INTERACTIVE_END){
            if(clickType == ClickType.LEFT || clickType == ClickType.RIGHT){
                event.setCancelled(false);
            }
        }
    }

    public void resetLayout(InventoryPlayer inventoryPlayer){
        Player player = inventoryPlayer.getPlayer();
        Kit kit = plugin.getKitsManager().getKitByName(inventoryPlayer.getKitName());
        if(kit == null){
            return;
        }

        plugin.getPlayerDataManager().resetKitLayout(player,kit.getName());

        Inventory inv = InventoryUtils.getTopInventory(player);
        if(inv != null){
            populateInventory(inv,inventoryPlayer,kit);
        }

        sendConfigMessage(player,"kitLayoutReset");
    }

    public void goBack(InventoryPlayer inventoryPlayer){
        saveLayoutFromInventory(inventoryPlayer);
        removeInventoryPlayer(inventoryPlayer.getPlayer());

        inventoryPlayer.setInventoryName(inventoryPlayer.getPreviousInventoryName());
        plugin.getInventoryManager().openInventory(inventoryPlayer);
    }

    public void saveLayoutFromInventory(InventoryPlayer inventoryPlayer){
        Player player = inventoryPlayer.getPlayer();
        String kitName = inventoryPlayer.getKitName();
        if(kitName == null){
            return;
        }

        Inventory inv = InventoryUtils.getTopInventory(player);
        if(inv == null){
            return;
        }

        //Defensive: make sure no virtual item leaves the GUI on the player's cursor.
        ItemStack cursorItem = player.getItemOnCursor();
        if(cursorItem != null && !cursorItem.getType().equals(Material.AIR)){
            boolean placed = false;
            for(int guiSlot=INTERACTIVE_START; guiSlot<=INTERACTIVE_END; guiSlot++){
                ItemStack current = inv.getItem(guiSlot);
                if(current == null || current.getType().equals(Material.AIR)){
                    inv.setItem(guiSlot,cursorItem);
                    placed = true;
                    break;
                }
            }
            if(!placed){
                //No free slot left, the item is simply discarded (it never existed for real).
            }
            player.setItemOnCursor(null);
        }

        ArrayList<KitLayoutPosition> layout = new ArrayList<>();
        for(int guiSlot=INTERACTIVE_START; guiSlot<=INTERACTIVE_END; guiSlot++){
            ItemStack item = inv.getItem(guiSlot);
            if(item == null || item.getType().equals(Material.AIR)){
                continue;
            }
            String indexTag = ItemUtils.getTagStringItem(plugin,item,TAG_INDEX);
            if(indexTag == null){
                continue;
            }
            try{
                int index = Integer.parseInt(indexTag);
                String idTag = ItemUtils.getTagStringItem(plugin,item,TAG_ID);
                layout.add(new KitLayoutPosition(index, idTag != null ? idTag : "", guiSlot-INTERACTIVE_OFFSET));
            }catch(NumberFormatException ignored){
            }
        }

        plugin.getPlayerDataManager().saveKitLayout(player,kitName,layout);
        sendConfigMessage(player,"kitLayoutSaved");
    }
}
