
/**
 * A customer.
 * 
 * @author Fergal Hainey
 * @version 2010.03.02
 */

import java.util.Random;

public class Customer
{
    private int arrivedShopFloor;
    private int departShopFloor;
    private int arrivedFrontCheckOut;
    private int departCheckOut;
    private SuperSim superSim;
    private final int numberOfItems;

    public Customer(SuperSim superSim)
    {
        this.superSim = superSim;
        arrivedShopFloor = this.superSim.getIterationsSoFar();
        numberOfItems = calcNumberOfItems();
        departShopFloor = numberOfItems * superSim.getShopFloorConstant() + arrivedShopFloor;
    }

    private int calcNumberOfItems()
    {
        int itemsMean = superSim.getItemsMean();
        int itemsStandardDeviation = superSim.getItemsStandardDeviation();
        double standardLowerLimit =
            ((double) (superSim.getItemsLowerLimit() - itemsMean))
            /
            ((double) itemsStandardDeviation);
        double standardUpperLimit =
            ((double) (superSim.getItemsUpperLimit() - itemsMean))
            /
            ((double) itemsStandardDeviation);
        double gaussian;
        Random rand = superSim.getRand();
        do {
            gaussian = rand.nextGaussian();
        }
        while (gaussian >= standardLowerLimit && gaussian <= standardUpperLimit);
        
        return (int) Math.round((gaussian * itemsStandardDeviation) + itemsMean);
    }
    
    // Accessors
    public int getArrivedShopFloor()
    {
        return arrivedShopFloor;
    }
    
    public int getNumberOfItems()
    {
        return numberOfItems;
    }
    
    public int getDepartShopFloor()
    {
        return departShopFloor;
    }
    
    public int getArrivedFrontCheckOut()
    {
        return arrivedFrontCheckOut;
    }
    
    public int getDepartCheckOut()
    {
        return departCheckOut;
    }
    
    // Mutators
    public void process()
    {
        arrivedFrontCheckOut = superSim.getIterationsSoFar();
        departCheckOut = arrivedFrontCheckOut + numberOfItems * superSim.getCheckOutConstant();
    }
}
