
/**
 * Parameters for a SuperSim
 * 
 * @author Fergal Hainey
 * @version 2010.03.25
 */
public class SuperSimParams
{
    private double customerProb;
    // Stuff for the normal distribution
    private int itemsMean;
    private int itemsStandardDeviation;
    private int itemsLowerLimit;
    private int itemsUpperLimit;
    // Constants
    private final int checkOutConstant;
    private final int shopFloorConstant;
    private final int expressCheckOutItemsLimit;
    private final int checkOutCustomerLimit;
    private final double closeCheckOutConstant;

    /**
     * @param customerProb The probability of a new customer arriving every iteration. If negative, SuperSim will look in 
     * the file customerprobs.csv for CSV data for plotting customer arrival probability, if the file doesn't exist the 
     * user will be prompted for a file. The data file needs just two columns: iteration and probability. The first data 
     * point must be for iteration 0 or the simulation will exit. Values for between specified iterations will be 
     * calculated assuming a linear progression between data points.
     * @param itemsMean The mean of the normal distribution used for calculating how many items each customer will buy.
     * @param itemsStandardDeviation the standard deviation for the normal distribution used for calculating how many 
     * items each customer will buy.
     * @param itemsLowerLimit The lower limit for the amount of items each customer will buy.
     * @param itemsUpperLimit The upper limit for the amount of items each customer will buy.
     * @param checkOutConstant The amount of iterations it takes to process each item at the Check-Outs.
     * @param shopFloorConstant The amount of iterations it takes to find each item on the shop floor before going to the 
     * Check-Outs.
     * @param expressCheckOutItemsLimit The amount of items under and including which customers can use the express 
     * Check-Out.
     */
    public SuperSimParams(
        double customerProb,
        int checkOutCustomerLimit,
        double closeCheckOutConstant,
        int itemsMean,
        int itemsStandardDeviation,
        int itemsLowerLimit,
        int itemsUpperLimit,
        int checkOutConstant,
        int shopFloorConstant,
        int expressCheckOutItemsLimit
    )
    {
        this.customerProb = customerProb;
        this.itemsMean = itemsMean;
        this.itemsStandardDeviation = itemsStandardDeviation;
        this.itemsLowerLimit = itemsLowerLimit;
        this.itemsUpperLimit = itemsUpperLimit;
        this.checkOutConstant = checkOutConstant;
        this.shopFloorConstant = shopFloorConstant;
        this.expressCheckOutItemsLimit = expressCheckOutItemsLimit;
        this.checkOutCustomerLimit = checkOutCustomerLimit;
        this.closeCheckOutConstant = closeCheckOutConstant;
    }
    
    public double getCustomerProb()
    {
        return customerProb;
    }
    
    public int getItemsMean()
    {
        return itemsMean;
    }
    
    public int getItemsStandardDeviation()
    {
        return itemsStandardDeviation;
    }
    
    public int getItemsLowerLimit()
    {
        return itemsLowerLimit;
    }
    
    public int getItemsUpperLimit()
    {
        return itemsUpperLimit;
    }
    
    // Constants
    public int getCheckOutConstant()
    {
        return checkOutConstant;
    }
    
    public int getShopFloorConstant()
    {
        return shopFloorConstant;
    }
    
    public int getExpressCheckOutItemsLimit()
    {
        return expressCheckOutItemsLimit;
    }
    
    public int getCheckOutCustomerLimit()
    {
        return checkOutCustomerLimit;
    }
    
    public double getCloseCheckOutConstant()
    {
        return closeCheckOutConstant;
    }
}
