
/**
 * A customer.
 * 
 * @author Fergal Hainey
 * @version 2010.03.02
 */

import java.util.Random;

public class Customer
{
    private SuperSim superSim;
    private final int arrived;
    private int depart;
    private final int numberOfItems;

    public Customer(SuperSim superSim)
    {
    	this.superSim = superSim;
        this.arrived = this.superSim.getIterationsSoFar();
        numberOfItems = calcNumberOfItems();
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
    public int getDepart()
    {
        return depart;
    }
    
    public int getNumberOfItems()
    {
        return numberOfItems;
    }
    
    public int getArrived()
    {
        return arrived;
    }
    
    // Mutators
    public void process(int iterations)
    {
        depart = iterations + numberOfItems * superSim.getCheckOutConstant();
    }
}
