
/**
 * Item objects have a name price and barcode details of which
 * are read from a txt file
 * 
 * @author Janie Sinclair
 * @version 18-03-10
 */
public class Item
{
    private String name;
    private int price;
    private String barcode;

    /**
     * Constructor for objects of class Item
     */
    public Item(String name, int price, String barcode)
    {
        this.name = name;
        this.price = price;
        this.barcode = barcode;
    }
    
    public String getName(){
        return name;
    }
    
    public int getPrice(){
        return price;
    }
    
    public String getBarcode(){
        return barcode;
    }
    
}
