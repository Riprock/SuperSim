
/**
 * Contains final output statistics from a SuperSim.
 * 
 * @author Fergal Hainey
 * @version 2010.03.24
 */

import java.util.HashMap;
import java.util.Arrays;

public class Stats
{
    private final int totalCustomers;
    private final int totalCustomersProcessed;
    private final int totalExpressCustomers;
    private final double meanIterationsQueueing;
    private final double meanIterationsExpressQueueing;
    private final HashMap<Integer, Integer> checkOutCountChanges;
    private final int maxCheckOutCount;

    /**
     * Constructor for objects of class Stats
     */
    public Stats(
        int totalCustomers,
        int totalCustomersProcessed,
        int totalExpressCustomers,
        double meanIterationsQueueing,
        double meanIterationsExpressQueueing,
        HashMap<Integer, Integer> checkOutCountChanges
    )
    {
        this.totalCustomers = totalCustomers;
        this.totalCustomersProcessed = totalCustomersProcessed;
        this.totalExpressCustomers = totalExpressCustomers;
        this.meanIterationsQueueing = meanIterationsQueueing;
        this.meanIterationsExpressQueueing = meanIterationsExpressQueueing;
        this.checkOutCountChanges = checkOutCountChanges;
        int currentCount = 0;
        int maxCount = 0;
        Integer[] keys = checkOutCountChanges.keySet().toArray(new Integer[checkOutCountChanges.size()]);
        Arrays.sort(keys);
        for (int key : keys) {
            int difference = checkOutCountChanges.get(key);
            currentCount += difference;
            maxCount = Math.max(maxCount, currentCount);
        }
        maxCheckOutCount = maxCount;
    }

    // Acessors goddamn you Java
    public int getTotalCustomers()
    {
        return totalCustomers;
    }
    
    public int getTotalCustomersProcessed()
    {
        return totalCustomersProcessed;
    }
    
    public int getTotalExpressCustomers()
    {
        return totalExpressCustomers;
    }
    
    public double getMeanIterationsQueueing()
    {
        return meanIterationsQueueing;
    }
    
    public double getMeanIterationsExpressQueueing()
    {
        return meanIterationsExpressQueueing;
    }
    
    public HashMap<Integer, Integer> getCHeckOutCountChanges()
    {
        return checkOutCountChanges;
    }
    
    public int getMaxCheckOutCount()
    {
        return maxCheckOutCount;
    }
}
