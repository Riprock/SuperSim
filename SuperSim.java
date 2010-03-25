
/**
 * Write a description of class Simulation here.
 * 
 * @author Fergal Hainey, Janie Sinclair, Chiedu Agborh, Yowana Kuvoka
 * @version 2010.03.02
 */

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import simpleIO.TextReader;
import simpleIO.TextWriter;
import java.io.IOException;

public class SuperSim
{
    private Random rand;
    private ArrayList<CheckOut> checkOuts;
    private HashMap<String, Item> stockList;
    private String[] stockListBarcodes;
    private ShopFloor shopFloor;
    private CheckOut expressCheckOut;
    private final int iterationLimit;
    private int iterationsSoFar;
    private double customerProb;
    private Integer[] probDataIterations;
    private Double[] probDataProbs;
    private int lastProbIndex;
    // Stuff for the normal distribution
    private int itemsMean;
    private int itemsStandardDeviation;
    private int itemsLowerLimit;
    private int itemsUpperLimit;
    // Constants
    private final int shopFloorConstant;
    private final int expressCheckOutItemsLimit;
    private final int checkOutConstant;
    private final int checkOutCustomerLimit;
    private final double closeCheckOutConstant;
    // Non-generic fields for diagnostics
    private int totalCustomers;
    private ArrayList<Customer> processedCustomers;
    private Stats stats;
    private HashMap<Integer, Integer> checkOutCountChanges;

    /**
     * A simulation for a branch of SuperSim.
     * 
     * Will look in the file items.csv for CSV data for the items stocked by the branch. If this file is not found the 
     * user will be prompted for one. The data file needs three columns: item name, price (in pence - no floating 
     * point arithmetic allowed) and barcode (a 12 character string of numbers).
     * 
     * @param iterations How many iterations to run the simulation for. For best results, think of an iteration as a second.
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
    public SuperSim(int iterations, double customerProb, int checkOutCustomerLimit, double closeCheckOutConstant, int itemsMean, int itemsStandardDeviation, int itemsLowerLimit, int itemsUpperLimit, int checkOutConstant, int shopFloorConstant, int expressCheckOutItemsLimit)
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
        this.checkOutCustomerLimit = checkOutCustomerLimit;
        this.closeCheckOutConstant = closeCheckOutConstant;
        // stats
        processedCustomers = new ArrayList<Customer>();
        checkOutCountChanges = new HashMap<Integer, Integer>();
        
        // get Items
        stockList = new HashMap<String, Item>();
        populateStockList();
        stockListBarcodes = stockList.keySet().toArray(new String[stockList.size()]);
        
        // Variable customerProb?
        try {
            if (customerProb < 0) {
                TextReader tr = new TextReader("customerprobs.csv");
                // Why didn't whoever made TextReader make it more like an Iterable?
                String line;
                ArrayList<Integer> tempIterations = new ArrayList<Integer>();
                ArrayList<Double> tempProbs = new ArrayList<Double>();
                boolean firstLine = true;
                while ((line = tr.readLine()) != null) {
                    String[] fields = line.split(",");
                    if (firstLine && Integer.parseInt(fields[0]) != 0) {
                        throw new IOException("First iteration datapoint for customer probability is not 0!");
                    }
                    firstLine = false;
                    tempIterations.add(Integer.parseInt(fields[0]));
                    tempProbs.add(Double.parseDouble(fields[1]));
                }
                probDataIterations = tempIterations.toArray(new Integer[tempIterations.size()]);
                probDataProbs = tempProbs.toArray(new Double[tempProbs.size()]);
                tempIterations = null;
                tempProbs = null;
                Runtime.getRuntime().gc();
            }
        }
        catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        
        // Run the simulation
        for (int i = 0; i < iterations; i++) {
            oneIteration();
        }
        
        int totalWaitIterations = 0;
        int totalExpressWaitIterations = 0;
        int expressCount = 0;
        for (Customer customer : processedCustomers) {
            if (customer.getItems().size() <= expressCheckOutItemsLimit) {
                expressCount++;
                totalExpressWaitIterations += customer.getArrivedFrontCheckOut() - customer.getDepartShopFloor();
            }
            totalWaitIterations += customer.getArrivedFrontCheckOut() - customer.getDepartShopFloor();
        }
        stats = new Stats(
            totalCustomers,
            processedCustomers.size(),
            expressCount,
            ((double) totalWaitIterations / (double) processedCustomers.size()),
            ((double) totalExpressWaitIterations / (double) expressCount),
            checkOutCountChanges
        );
    }
    
    /**
     * A simulation for a branch of SuperSim with default values.
     * 
     * See the long constructor for more details. The default values used are: customerProb -1, itemsMean 11, 
     * itemsStandardDeviation 4, itemsLowerLimit 1, itemsUpperLimit 30, checkOutConstant 15, shopFloorContant 60, 
     * expressCheckOutItemsLimit 10.
     * 
     * @param iterations How many iterations to run the simulation for. For best results, think of an iteration as a second.
     */
    public SuperSim(int iterations)
    {
        this(/*43200*/iterations, /*(0.5/60.0)*/-1, 4, 2, 11, 4, 1, 30, 15, 60, 10);
    }
    
    // read all items from text file to an arraylist of items
    private boolean populateStockList()
    {
        TextReader tr = new TextReader("items.csv");
        
        String line;
        while ((line = tr.readLine()) != null) {
            String[] fields = line.split(",");
            stockList.put(fields[2], new Item(fields[0], Integer.parseInt(fields[1]), fields[2]));
        }
        return true;
    }
    
    private void oneIteration()
    {
        // Move customers who are done out the queues
        checkCustomerDepart(expressCheckOut);
        for (CheckOut checkOut : checkOuts) {
            checkCustomerDepart(checkOut);
        }
        
        int oldCheckOutCount = checkOuts.size();
        // If there's more than one CheckOut and the avergage Customers in each one is less than the constant, 
        // then close a CheckOut if there's an empty one to be closed.
        if (checkOuts.size() > 1) {
            int customersAtCheckOuts = 0;
            for (CheckOut checkOut : checkOuts) {
                customersAtCheckOuts += checkOut.size();
            }
            double mean = (double) customersAtCheckOuts / (double) checkOuts.size();
            if (mean <= closeCheckOutConstant) {
                for (CheckOut checkOut : checkOuts) {
                    if (checkOut.isEmpty()) {
                        checkOuts.remove(checkOut);
                        break;
                    }
                }
            }
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
                int bestCheckOutSize = checkOutCustomerLimit;
                int checkOutCount = checkOuts.size();
                for (int i = 0; i < checkOutCount; i++) {
                    int checkOutSize = checkOuts.get(i).size();
                    if (checkOutSize < bestCheckOutSize) {
                        bestCheckOut = i;
                        bestCheckOutSize = checkOutSize;
                    }
                }
                if (bestCheckOutSize < checkOutCustomerLimit) {
                    checkOuts.get(bestCheckOut).add(customer);
                }
                else {
                    CheckOut newCheckOut = new CheckOut();
                    newCheckOut.add(customer);
                    checkOuts.add(newCheckOut);
                }
            }
        }
        int checkOutCountDifference = checkOuts.size() - oldCheckOutCount;
        if (checkOutCountDifference != 0) {
            checkOutCountChanges.put(iterationsSoFar, checkOutCountDifference);
        }
        
        double currentCustomerProb = customerProb;
        // Calculate currentCustomerProb if variable.
        if (customerProb < 0) {
            for (int i = lastProbIndex; i < probDataIterations.length; i++) {
                if (probDataIterations[i] == iterationsSoFar) {
                    currentCustomerProb = probDataProbs[i];
                    break;
                }
                else if (probDataIterations[i] > iterationsSoFar) {
                    int iterationRange = probDataIterations[i] - probDataIterations[i-1];
                    double probRange = probDataProbs[i] - probDataProbs[i-1];
                    double proportionOfRange = (double) (iterationsSoFar - probDataIterations[i-1]) / (double) iterationRange;
                    currentCustomerProb = probDataProbs[i-1] + (probRange * proportionOfRange);
                    lastProbIndex = i;
                    break;
                }
                // Due to breaks this should only be the final result when at the end of the array (basically flatline from last data point)
                currentCustomerProb = probDataProbs[i];
            }
        }
                    
        // Is there a new customer?
        if (rand.nextFloat() < currentCustomerProb) {
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
                customer.process();
                return true;
            }
        }
        return false;
    }
    
    private void gatherDepartStats(Customer customer) {
        processedCustomers.add(customer);
    }
    
    private void newCustomer()
    {
        Customer customer = new Customer(this);
        shopFloor.add(customer);
        
        totalCustomers++;
    }
    
    /**
     * Print some simple statistics to STDOUT.
     * 
     * Good for getting a quick idea of how a simulation went.
     */
    public void printInfo()
    {
        System.out.println(iterationLimit + " iterations");
        System.out.println(((customerProb < 0) ? "Variable" : customerProb) + " probability of a new customer every iteration");
        System.out.println(stats.getMaxCheckOutCount() + " Check-Outs needed in addition to the express Check-Out");
        System.out.println(totalCustomers + " customers in total");
        System.out.println(processedCustomers.size() + " customers processed");
        System.out.println(stats.getTotalExpressCustomers() + " customers used the express Check-Out");
        System.out.println("mean of " + stats.getMeanIterationsQueueing() + " iterations before customer reaches front of Check-Out queue");
        System.out.println("mean of " + stats.getMeanIterationsExpressQueueing() + " iterations before customer reaches front of express Check-Out queue");
        System.out.println();
    }
    
    /**
     * Write out information about what customers bought, which can be used for tracking loyalty purchases.
     * 
     * The user will be prompted for a file to write the data out to. Each customers data is separated by an empty line. 
     * The first line for each customer is their unique ID, the following lines are the barcodes of the items they bought 
     * and the last line is the total amount they spent in pence (no floating point arithmetic allowed).
     */
    public void writeLoyalty()
    {
        String loyaltyOutput = "";
        for (Customer customer : processedCustomers) {
            loyaltyOutput += (customer.getId() + "\n");
            int totalPence = 0;
            for (Item item : customer.getItems()) {
                String barcode = item.getBarcode();
                totalPence += stockList.get(barcode).getPrice();
                loyaltyOutput += (barcode + "\n");
            }
            loyaltyOutput += (totalPence + "\n\n");
        }
    
        TextWriter out = new TextWriter("");
        out.writeString(loyaltyOutput);
        out.close();
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
    
    public HashMap<String, Item> getStockList()
    {
        return stockList;
    }
    
    public String[] getStockListBarcodes()
    {
        return stockListBarcodes;
    }
    
    public Stats getStats()
    {
        return stats;
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
