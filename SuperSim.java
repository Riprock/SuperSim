
/**
 * Write a description of class Simulation here.
 * 
 * @author Fergal Hainey
 * @version 2010.03.02
 */

import java.util.Random;
import java.util.ArrayList;

public class SuperSim
{
    private Random rand;
    private ArrayList<CheckOut> checkOuts;
    private ShopFloor shopFloor;
    private CheckOut expressCheckOut;
    private final int iterationLimit;
    private int iterationsSoFar;
    private double customerProb;
    private int totalCustomers;
    // Stuff for the normal distribution
    private int itemsMean;
    private int itemsStandardDeviation;
    private int itemsLowerLimit;
    private int itemsUpperLimit;
    // Constants
    private final int shopFloorConstant;
    private final int expressCheckOutItemsLimit;
    final int checkOutConstant;
    // Non-generic fields for diagnostics
    private int totalCustomersProcessed;
    private int totalWaitIterations;
    private int totalExpressCustomersProcessed;

    /**
     * Constructor for objects of class SuperSim
     */
    public SuperSim(int iterations, double customerProb, int itemsMean, int itemsStandardDeviation, int itemsUpperLimit, int itemsLowerLimit, int checkOutConstant, int shopFloorConstant, int expressCheckOutItemsLimit)
    {
        // Structure
        rand = new Random();
        checkOuts = new ArrayList<CheckOut>();
        CheckOut checkOut = new CheckOut();
        checkOuts.add(checkOut);
        shopFloor = new ShopFloor();
        expressCheckOut = new CheckOut();
        
        // SuperSim
        iterationsSoFar = 0;
        iterationLimit = iterations;
        
        // Initialising
        this.customerProb = customerProb;
        this.itemsMean = itemsMean;
        this.itemsStandardDeviation = itemsStandardDeviation;
        this.itemsLowerLimit = itemsLowerLimit;
        this.itemsUpperLimit = itemsUpperLimit;
        this.checkOutConstant = checkOutConstant;
        this.shopFloorConstant = shopFloorConstant;
        this.expressCheckOutItemsLimit = expressCheckOutItemsLimit;
        
        // Run the simulation
        for (int i = 0; i < iterations; i++) {
            oneIteration();
        }
    }
    
    public SuperSim(int iterations)
    {
        this(iterations, (0.5/60.0), 11, 4, 1, 30, 5, 20, 10);
    }
    
    private void oneIteration()
    {
        // Move customers who are done out the queues
        checkCustomerDepart(expressCheckOut);
        for (CheckOut checkOut : checkOuts) {
            checkCustomerDepart(checkOut);
        }
        
        // Move Customers from ShopFloor to CheckOuts
        ArrayList<Customer> customersToBeMovedToCheckOuts = new ArrayList<Customer>();
        for(Customer customer: shopFloor)
        {
            if (customer.getDepartShopFloor() == iterationsSoFar)
            {
                customersToBeMovedToCheckOuts.add(customer);
            }
        }
        
        for(Customer customer: customersToBeMovedToCheckOuts)
        {
            shopFloor.remove(customer);
            if (customer.getNumberOfItems() <= expressCheckOutItemsLimit) {
                expressCheckOut.add(customer);
            }
            else {
                int bestCheckOut = 0;
                int bestCheckOutSize = 4;
                int checkOutCount = checkOuts.size();
                for (int i = 0; i < checkOutCount; i++) {
                    int checkOutSize = checkOuts.get(i).size();
                    if (checkOutSize < bestCheckOutSize) {
                        bestCheckOut = i;
                        bestCheckOutSize = checkOutSize;
                    }
                }
                if (bestCheckOutSize < 4) {
                    checkOuts.get(bestCheckOut).add(customer);
                }
                else {
                    CheckOut newCheckOut = new CheckOut();
                    newCheckOut.add(customer);
                    checkOuts.add(newCheckOut);
                }
            }
        }
        
        // Is there a new customer?
        if (rand.nextFloat() < customerProb) {
            newCustomer();
        }
        
        // Start processing customer at front of queue if necessary
        checkCustomerProcess(expressCheckOut);
        for (CheckOut checkOut : checkOuts) {
            checkCustomerProcess(checkOut);
        }
        
        iterationsSoFar++;
    }
    
    private boolean checkCustomerDepart(CheckOut checkOut) {
        if (!checkOut.isEmpty() && checkOut.get(0).getDepartCheckOut() == iterationsSoFar) {
            Customer customer = checkOut.get(0);
            gatherDepartStats(customer);
            checkOut.remove(customer);
            return true;
        }
        return false;
    }
    
    private boolean checkCustomerProcess(CheckOut checkOut) {
        if (!checkOut.isEmpty()) {
            Customer customer = checkOut.get(0);
            if (customer.getDepartCheckOut() == 0) {
                customer.process(iterationsSoFar);
                return true;
            }
        }
        return false;
    }
    
    private void gatherDepartStats(Customer customer) {
        totalCustomersProcessed++;
        totalWaitIterations += customer.getArrivedFrontCheckOut() - customer.getArrivedCheckOut();
        if (customer.getNumberOfItems() <= expressCheckOutItemsLimit) {
            totalExpressCustomersProcessed++;
        }
    }
    
    private void newCustomer()
    {
        Customer customer = new Customer(this);
        shopFloor.add(customer);
        
        totalCustomers++;
    }
    
    public void printInfo()
    {
        System.out.println(iterationLimit + " iterations");
        System.out.println(customerProb + " probability of a new customer every iteration");
        System.out.println(checkOuts.size() + " Check-Outs needed in addition to the express Check-Out");
        System.out.println(totalCustomers + " customers in total");
        System.out.println(totalCustomersProcessed + " customers processed");
        System.out.println(totalExpressCustomersProcessed + " customers used the express Check-Out");
        System.out.println("mean of " + ((float) totalWaitIterations / (float) totalCustomersProcessed) + " iterations before customer reaches front of queue");
        System.out.println();
    }
    
    // Accessors
    // Distribution
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
    // Simulation
    public int getIterationsSoFar()
    {
        return iterationsSoFar;
    }
    
    public Random getRand()
    {
        return rand;
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
}
