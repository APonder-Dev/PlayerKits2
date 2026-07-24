package pk.ajneb97.model;

import pk.ajneb97.model.internal.KitLayoutPosition;

import java.util.ArrayList;

public class PlayerDataKit {

    private String name;
    private long cooldown; //Cooldown calculated
    private boolean oneTime;
    private boolean bought;
    private ArrayList<KitLayoutPosition> layout;

    public PlayerDataKit(String name) {
        this.name = name;
        this.cooldown = 0;
        this.oneTime = false;
        this.bought = false;
        this.layout = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public ArrayList<KitLayoutPosition> getLayout() {
        return layout;
    }

    public void setLayout(ArrayList<KitLayoutPosition> layout) {
        this.layout = layout;
    }

    /**
     * Returns the saved slot for this kit item, or null if there is no saved position
     * or the kit item at that index no longer matches what was originally saved
     * (kit contents changed since the layout was saved).
     */
    public Integer getLayoutSlot(int itemIndex, String itemId){
        for(KitLayoutPosition position : layout){
            if(position.getItemIndex() == itemIndex){
                if(itemId != null && itemId.equals(position.getItemId())){
                    return position.getSlot();
                }
                return null;
            }
        }
        return null;
    }

    public void clearLayout(){
        layout.clear();
    }
}
