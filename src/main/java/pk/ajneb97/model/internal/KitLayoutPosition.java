package pk.ajneb97.model.internal;

public class KitLayoutPosition {

    private int itemIndex;
    private String itemId;
    private int slot;

    public KitLayoutPosition(int itemIndex, String itemId, int slot) {
        this.itemIndex = itemIndex;
        this.itemId = itemId;
        this.slot = slot;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public String getItemId() {
        return itemId;
    }

    public int getSlot() {
        return slot;
    }

    public String serialize(){
        return itemIndex+"|"+itemId+"|"+slot;
    }

    public static KitLayoutPosition deserialize(String text){
        String[] sep = text.split("\\|",3);
        if(sep.length < 3){
            return null;
        }
        try{
            return new KitLayoutPosition(Integer.parseInt(sep[0]), sep[1], Integer.parseInt(sep[2]));
        }catch(NumberFormatException e){
            return null;
        }
    }
}
