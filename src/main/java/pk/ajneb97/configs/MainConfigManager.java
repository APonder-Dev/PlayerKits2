package pk.ajneb97.configs;

import org.bukkit.configuration.file.FileConfiguration;
import pk.ajneb97.PlayerKits2;
import pk.ajneb97.configs.model.CommonConfig;
import pk.ajneb97.model.Kit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainConfigManager {

    private PlayerKits2 plugin;
    private CommonConfig configFile;

    //Options
    private Kit newKitDefault;
    private boolean kitPreview;
    private boolean closeInventoryOnClaim;
    private boolean claimKitShortCommand;
    private boolean kitPreviewRequiresKitPermission;
    private boolean newKitDefaultSaveModeOriginal;
    private String firstJoinKit;
    private String newKitDefaultInventory;
    private boolean isMySQL;
    private boolean updateNotify;
    private boolean useMiniMessage;

    private boolean kitLayoutEnabled;
    private int kitLayoutButtonSlot;
    private String kitLayoutButtonMaterial;
    private String kitLayoutButtonName;
    private List<String> kitLayoutButtonLore;
    private String kitLayoutTitle;
    private String kitLayoutResetButtonMaterial;
    private String kitLayoutResetButtonName;
    private List<String> kitLayoutResetButtonLore;
    private String kitLayoutBackButtonMaterial;
    private String kitLayoutBackButtonName;
    private List<String> kitLayoutBackButtonLore;
    private String kitLayoutSaveButtonMaterial;
    private String kitLayoutSaveButtonName;
    private List<String> kitLayoutSaveButtonLore;
    private String kitLayoutSavedButtonName;

    public MainConfigManager(PlayerKits2 plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("config.yml",plugin,null, false);
        this.configFile.registerConfig();
        checkUpdate();
    }

    public void configure(){
        FileConfiguration config = configFile.getConfig();
        newKitDefault = KitsConfigManager.getKitFromConfig(config,plugin,null,"new_kit_default_values.");
        kitPreview = config.getBoolean("kit_preview");
        closeInventoryOnClaim = config.getBoolean("close_inventory_on_claim");
        kitPreviewRequiresKitPermission = config.getBoolean("kit_preview_requires_kit_permission");
        firstJoinKit = config.getString("first_join_kit");
        newKitDefaultInventory = config.getString("new_kit_default_inventory");
        isMySQL = config.getBoolean("mysql_database.enabled");
        updateNotify = config.getBoolean("update_notify");
        claimKitShortCommand = config.getBoolean("claim_kit_short_command");
        useMiniMessage = config.getBoolean("use_minimessage");
        newKitDefaultSaveModeOriginal = config.getBoolean("new_kit_default_save_mode_original");

        kitLayoutEnabled = config.getBoolean("kit_layout.enabled");
        kitLayoutButtonSlot = config.getInt("kit_layout.button.slot");
        kitLayoutButtonMaterial = config.getString("kit_layout.button.material");
        kitLayoutButtonName = config.getString("kit_layout.button.name");
        kitLayoutButtonLore = config.getStringList("kit_layout.button.lore");
        kitLayoutTitle = config.getString("kit_layout.title");
        kitLayoutResetButtonMaterial = config.getString("kit_layout.reset_button.material");
        kitLayoutResetButtonName = config.getString("kit_layout.reset_button.name");
        kitLayoutResetButtonLore = config.getStringList("kit_layout.reset_button.lore");
        kitLayoutBackButtonMaterial = config.getString("kit_layout.back_button.material");
        kitLayoutBackButtonName = config.getString("kit_layout.back_button.name");
        kitLayoutBackButtonLore = config.getStringList("kit_layout.back_button.lore");
        kitLayoutSaveButtonMaterial = config.getString("kit_layout.save_button.material");
        kitLayoutSaveButtonName = config.getString("kit_layout.save_button.name");
        kitLayoutSaveButtonLore = config.getStringList("kit_layout.save_button.lore");
        kitLayoutSavedButtonName = config.getString("kit_layout.save_button.saved_name");
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }

    public void checkUpdate(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            if(!text.contains("use_minimessage:")){
                getConfig().set("use_minimessage",false);
                configFile.saveConfig();
            }
            if(!text.contains("verifyServerCertificate:")){
                getConfig().set("mysql_database.pool.connectionTimeout",5000);
                getConfig().set("mysql_database.advanced.verifyServerCertificate",false);
                getConfig().set("mysql_database.advanced.useSSL",true);
                getConfig().set("mysql_database.advanced.allowPublicKeyRetrieval",true);
                configFile.saveConfig();
            }
            if(!text.contains("new_kit_default_save_mode_original:")){
                getConfig().set("new_kit_default_save_mode_original", true);
                configFile.saveConfig();
            }
            if(!text.contains("kit_layout:")){
                getConfig().set("kit_layout.enabled", true);
                getConfig().set("kit_layout.button.slot", 46);
                getConfig().set("kit_layout.button.material", "HOPPER");
                getConfig().set("kit_layout.button.name", "&6&lArrange Your Kit");
                getConfig().set("kit_layout.button.lore", new ArrayList<>(java.util.Arrays.asList(
                        "&7Click to freely arrange the items",
                        "&7of this kit into your own preferred",
                        "&7inventory layout.")));
                getConfig().set("kit_layout.title", "&8&lArranging: %kit%");
                getConfig().set("kit_layout.reset_button.material", "BARRIER");
                getConfig().set("kit_layout.reset_button.name", "&c&lReset Layout");
                getConfig().set("kit_layout.reset_button.lore", new ArrayList<>(java.util.Arrays.asList(
                        "&7Restore the default arrangement",
                        "&7of this kit.")));
                getConfig().set("kit_layout.back_button.material", "ARROW");
                getConfig().set("kit_layout.back_button.name", "&7&lGo Back");
                getConfig().set("kit_layout.back_button.lore", new ArrayList<>(java.util.Arrays.asList(
                        "&7Saves your layout and returns",
                        "&7to the kit preview.")));
                getConfig().set("kit_layout.save_button.material", "EMERALD");
                getConfig().set("kit_layout.save_button.name", "&a&lSave Layout");
                getConfig().set("kit_layout.save_button.lore", new ArrayList<>(java.util.Arrays.asList(
                        "&7Click to save your current",
                        "&7item arrangement.")));
                getConfig().set("kit_layout.save_button.saved_name", "&a&lSaved!");
                configFile.saveConfig();
            }
            if(!text.contains("save_button:")){
                getConfig().set("kit_layout.save_button.material", "EMERALD");
                getConfig().set("kit_layout.save_button.name", "&a&lSave Layout");
                getConfig().set("kit_layout.save_button.lore", new ArrayList<>(java.util.Arrays.asList(
                        "&7Click to save your current",
                        "&7item arrangement.")));
                configFile.saveConfig();
            }
            if(!text.contains("saved_name:")){
                getConfig().set("kit_layout.save_button.saved_name", "&a&lSaved!");
                configFile.saveConfig();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public Kit getNewKitDefault() {
        return newKitDefault;
    }

    public boolean isKitPreview() {
        return kitPreview;
    }

    public boolean isCloseInventoryOnClaim() {
        return closeInventoryOnClaim;
    }

    public boolean isKitPreviewRequiresKitPermission() {
        return kitPreviewRequiresKitPermission;
    }

    public String getFirstJoinKit() {
        return firstJoinKit;
    }

    public String getNewKitDefaultInventory() {
        return newKitDefaultInventory;
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public boolean isUpdateNotify() {
        return updateNotify;
    }

    public boolean isClaimKitShortCommand() {
        return claimKitShortCommand;
    }

    public boolean isNewKitDefaultSaveModeOriginal() {
        return newKitDefaultSaveModeOriginal;
    }

    public boolean isUseMiniMessage() {
        return useMiniMessage;
    }

    public boolean isKitLayoutEnabled() {
        return kitLayoutEnabled;
    }

    public int getKitLayoutButtonSlot() {
        return kitLayoutButtonSlot;
    }

    public String getKitLayoutButtonMaterial() {
        return kitLayoutButtonMaterial;
    }

    public String getKitLayoutButtonName() {
        return kitLayoutButtonName;
    }

    public List<String> getKitLayoutButtonLore() {
        return kitLayoutButtonLore;
    }

    public String getKitLayoutTitle() {
        return kitLayoutTitle;
    }

    public String getKitLayoutResetButtonMaterial() {
        return kitLayoutResetButtonMaterial;
    }

    public String getKitLayoutResetButtonName() {
        return kitLayoutResetButtonName;
    }

    public List<String> getKitLayoutResetButtonLore() {
        return kitLayoutResetButtonLore;
    }

    public String getKitLayoutBackButtonMaterial() {
        return kitLayoutBackButtonMaterial;
    }

    public String getKitLayoutBackButtonName() {
        return kitLayoutBackButtonName;
    }

    public List<String> getKitLayoutBackButtonLore() {
        return kitLayoutBackButtonLore;
    }

    public String getKitLayoutSaveButtonMaterial() {
        return kitLayoutSaveButtonMaterial;
    }

    public String getKitLayoutSaveButtonName() {
        return kitLayoutSaveButtonName;
    }

    public List<String> getKitLayoutSaveButtonLore() {
        return kitLayoutSaveButtonLore;
    }

    public String getKitLayoutSavedButtonName() {
        return kitLayoutSavedButtonName;
    }
}
