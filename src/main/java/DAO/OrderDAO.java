package DAO;

import DTO.Order;
import DTO.Product;
import DTO.State;


import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderDAO implements OrderDAOInterface {

    Map<String, State> states = new HashMap<>();
    Map<String, Product> products = new HashMap<>();
    Map<Integer, Order> orderHistory = new HashMap<>();
    private final String ORDER_FOLDER;
    private final String STATES_FILE;
    private final String PRODUCTS_FILE;
    private final String BACKUP_FILE = "Backup/DataExport.txt";
    private File createdFile;
    public static final String DELIMITER = "::";
    public static final String FILE_HEADER = "OrderNumber::CustomerName::State::TaxRate::ProductType::Area::CostPerSquareFoot::LaborCostPerSquareFoot::MaterialCost::LaborCost::Tax::Total";
    private Scanner reader;
    private int nextOrderNum = 1;


    public OrderDAO() throws IOException, ProductFileNotFound, OrderFileNotFound, TaxFileNotFound {
        ORDER_FOLDER = "Orders";
        // Where each Order file for each day is stored
        STATES_FILE = "Data\\Taxes.txt";
        // Where each State object information is stored
        PRODUCTS_FILE = "Data\\Products.txt";
        // Where each Product object information is stored
        String orderFile = ORDER_FOLDER + "\\Orders_" + LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyyyy")) + ".txt";
        // Generate a new file name based on today's date
        createdFile = new File(orderFile);
        // Create the File object for today
        if (!createdFile.exists()) {
            // If the file for today has not been created yet
            createdFile.createNewFile();
            // Create that file here
        }
        loadOrders();
        // Load the orders from each file in the orders folder into the orderHistory HashMap
        loadProducts();
        // Load the products from the product file into the Products HashMap
        loadStates();
        // Load the states from the taxes file into the States HashMap
    }

    public OrderDAO(String testFolder) throws IOException, OrderFileNotFound, ProductFileNotFound, TaxFileNotFound {
        ORDER_FOLDER = testFolder;
        STATES_FILE = "Data\\Taxes.txt";
        PRODUCTS_FILE = "Data\\Products.txt";
        String orderFile = testFolder + "\\Orders_" + LocalDate.now().format(DateTimeFormatter.ofPattern("MMddyyyy")) + ".txt";
        // Generate a new file name based on today's date
        createdFile = new File(orderFile);
        // Create the File object for today
        if (!createdFile.exists()) {
            // If the file for today has not been created yet
            createdFile.createNewFile();
            // Create that file here
        }
        loadOrders();
        // Load the orders from each file in the orders folder into the orderHistory HashMap
        loadProducts();
        // Load the products from the product file into the Products HashMap
        loadStates();
        // Load the states from the taxes file into the States HashMap
    }

    @Override
    public void loadProducts() throws ProductFileNotFound{
        try {
            reader = new Scanner(new BufferedReader(new FileReader(PRODUCTS_FILE)));
        } catch (FileNotFoundException e) {
            throw new ProductFileNotFound("Products unable to be loaded.", e);
        }
        // Generic scanner generation
        // Reader is a class variable

        String currentLine;
        Product currentProduct;
        // Generic holding variables

        while (reader.hasNextLine()) {
            currentLine = reader.nextLine();
            if (currentLine.contains("ProductType")) {
                // If the current line being parsed contains the words ProductType, it is the header line
                continue;
                // Skip it
            } else {
                currentProduct = unmarshallProducts(currentLine);
                // Pass the current String into the unmarshall method
                // Method call returns a Product object
                products.put(currentProduct.getType(), currentProduct);
                // Add the current Product object to the Product HashMap
            }
        }
        reader.close();
        // Close the file reader
    }

    @Override
    public void loadStates() throws TaxFileNotFound {
        try {
            reader = new Scanner(new BufferedReader(new FileReader(STATES_FILE)));
        } catch (FileNotFoundException e) {
            throw new TaxFileNotFound("State taxes unable to be loaded.", e);
        }
        // Generic scanner generation
        // Reader is a class variable

        String currentLine;
        State currentState;
        // Generic holding variables

        while (reader.hasNextLine()) {
            currentLine = reader.nextLine();
            if (currentLine.contains("State")) {
                // If the current line being parsed contains the words State, it is the header line
                continue;
            } else {
                currentState = unmarshallStates(currentLine);
                // Pass the current String into the unmarshall method
                // Method call returns a State object
                states.put(currentState.getState(), currentState);
                // Add the current State object to the State HashMap
            }
        }
        reader.close();
        // Close the reader
    }

    @Override
    public void loadOrders() throws OrderFileNotFound {
        File ordersFolder = new File(ORDER_FOLDER);
        // Create a new file object using the Orders folder path
        File[] ordersList = ordersFolder.listFiles();
        // Pull each File in the orders folder into an array of files
        for (File orders : ordersList) {
            // For each order file in the orders folder
            try {
                reader = new Scanner(new BufferedReader(new FileReader(orders)));
            } catch (FileNotFoundException e) {
                throw new OrderFileNotFound("Orders unable to be loaded.", e);
            }
            // Generic scanner generation
            // Reader is a class variable

            String currentLine;
            Order currentOrder;
            // Generic holding variables

            while (reader.hasNextLine()) {
                currentLine = reader.nextLine();
                if (currentLine.contains("OrderNumber")) {
                    // If the current line being parsed contains the words OrderNumber, it is the header line
                    continue;
                }
                else {
                    currentOrder = unmarshallOrders(currentLine, getDate(orders.getName()));
                    // Pass the current String into the unmarshall method
                    // Method call returns an Order object
                    orderHistory.put(currentOrder.getOrderNum(), currentOrder);
                    // Add the current Order object to the orderHistory HashMap
                    nextOrderNum++;
                    // Increment the new order number generator to reflect the total number of orders
                }
            }
        }
        reader.close();
        // Close the reader
    }

    @Override
    public LocalDate getDate(String fileName) {
        // Method receives an Orders file name from the Orders folder
        String findDate = fileName.substring(7,15);
        // These file names will always have the date located between string indces 7 and 15
        LocalDate foundDate = LocalDate.parse(findDate, DateTimeFormatter.ofPattern("MMddyyyy"));
        // Parse the date pulled out of the order file name into a proper Local Date
        return foundDate;
        // Return this date to the unmarshallOrders function
    }

    @Override
    public int getNextOrderNum() {
        if (orderHistory.get(nextOrderNum) != null) {
            // Constant check anytime this method is called to ensure that the nextOrderNum will be null
            return nextOrderNum+1;
            // If it is not null, add 1 to it
        }
        else {
            return nextOrderNum;
            // Otherwise, it continues to point to the next available order number
        }
    }

    @Override
    public void saveTrigger() throws IOException, CannotOpenFile {
        // Method called by the service layer
        saveOrders();
    }

    @Override
    public void saveOrders() throws IOException, CannotOpenFile {
        PrintWriter writer;
        File ordersFolder = new File(ORDER_FOLDER);
        // Open the folder for the files containing previous orders
        // Create new file object with the path to the order folder
        File[] ordersList = ordersFolder.listFiles();
        // Create a file list of the order files in the older folder

        for (File file : ordersList) {
            // For each file in the orders folder
            try {
                writer = new PrintWriter(new FileWriter(file));
                // Open it for editing.
            } catch (FileNotFoundException e) {
                throw new CannotOpenFile("Cannot write to order history file.");
            }

            writer.println(FILE_HEADER);

            for (Order thisOrder : orderHistory.values()) {
                // For each order in the order history
                if (file.getName().contains(thisOrder.getDate().format(DateTimeFormatter.ofPattern("MMddYYYY")))) {
                    // If the order file name contains the date included in each order history object
                        writer.println(marshallOrders(thisOrder));
                        // marshall it to that file
                }
            }
            writer.close();
        }
    }

    @Override
    public void exportOrders() throws IOException, CannotOpenFile {
        // This method writes to the DataExport file in the back up folder
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileWriter(BACKUP_FILE));
            // Open it for editing.
        } catch (FileNotFoundException e) {
            throw new CannotOpenFile("Cannot write to order history file.");
        }
        writer.println(FILE_HEADER + "::Date");
        // Print the generic file header plus the date
        for (Order thisOrder : orderHistory.values()) {
            writer.println(marshallOrders(thisOrder) + thisOrder.getDate().toString());
            // Print the order and the associated date to the DataExport file
        }
        writer.close();
    }

    @Override
    public Product unmarshallProducts(String product) {
        String[] productString = product.split(DELIMITER);
        // Split the string based on the delimiter
        Product currentProduct = new Product();
        // Generate a new product
        currentProduct.setType(productString[0]);
        currentProduct.setCostPerSqFt(BigDecimal.valueOf(Double.parseDouble(productString[1])));
        currentProduct.setLaborPerSqFt(BigDecimal.valueOf(Double.parseDouble(productString[2])));
        // Set each of the new products values to the appropriate one in the string array
        return currentProduct;
    }

    @Override
    public State unmarshallStates(String state) {
        String[] stateString = state.split(DELIMITER);
        // Split the string based on the delimiter
        State currentState = new State();
        // Generate a new state
        currentState.setState(stateString[0]);
        currentState.setName(stateString[1]);
        currentState.setTaxRate(BigDecimal.valueOf(Double.parseDouble(stateString[2])));
        // Set each of the new states values to the appropriate one in the string array
        return currentState;
    }

    @Override
    public Order unmarshallOrders(String order, LocalDate date) {
        String[] orderString = order.split(DELIMITER);
        // Split the string based on the delimiter
        Order currentOrder = new Order(Integer.parseInt(orderString[0]));
        // Generate a new order using the first index, the order number
        currentOrder.setCustName(orderString[1]);
        currentOrder.setState(orderString[2]);
        currentOrder.setTaxRate(BigDecimal.valueOf(Double.parseDouble(orderString[3])));
        currentOrder.setProductType(orderString[4]);
        currentOrder.setArea(BigDecimal.valueOf(Double.parseDouble(orderString[5])));
        currentOrder.setCostPerSqFt(BigDecimal.valueOf(Double.parseDouble(orderString[6])));
        currentOrder.setLaborPerSqFt(BigDecimal.valueOf(Double.parseDouble(orderString[7])));
        currentOrder.setMaterialCost(BigDecimal.valueOf(Double.parseDouble(orderString[8])));
        currentOrder.setLaborCost(BigDecimal.valueOf(Double.parseDouble(orderString[9])));
        currentOrder.setTax(BigDecimal.valueOf(Double.parseDouble(orderString[10])));
        currentOrder.setTotal(BigDecimal.valueOf(Double.parseDouble(orderString[11])));
        // Set its respective values to the appropriate index
        currentOrder.setDate(date);
        // The unmarshall order function receives the LocalDate from the Order file name, which is assigned to each order
        return currentOrder;
    }

    @Override
    public String marshallOrders(Order order) {
        String orderString = order.getOrderNum() + DELIMITER;
        orderString += order.getCustName() + DELIMITER;
        orderString += order.getState() + DELIMITER;
        orderString += order.getTaxRate() + DELIMITER;
        orderString += order.getProductType() + DELIMITER;
        orderString += order.getArea() + DELIMITER;
        orderString += order.getCostPerSqFt() + DELIMITER;
        orderString += order.getLaborPerSqFt() + DELIMITER;
        orderString += order.getMaterialCost() + DELIMITER;
        orderString += order.getLaborCost() + DELIMITER;
        orderString += order.getTax() + DELIMITER;
        orderString += order.getTotal() + DELIMITER;
        return orderString;
    }

    @Override
    public List<Order> getOrders(LocalDate date) {
        return new ArrayList<>(orderHistory.values());
        // Return all orders, which is parsed in the service layer
    }
    @Override
    public Order getOrder(LocalDate date, int orderNum) {
        return orderHistory.get(orderNum);
        // Return the order based on order number
        // The date variable is not currently used for this task
    }

    @Override
    public Order addOrder(Order order) {
        Order newOrder = orderHistory.put(order.getOrderNum(), order);
        // Add the order to the HashMap
        return newOrder;
        // Returns the created object for reference
    }

    @Override
    public Order editOrder(int orderNum, Order orderChanges) {
        orderHistory.get(orderNum).setCustName(orderChanges.getCustName());
        // These checks are back ups to those found in the controller
        // The controller sets each value that can be changed to the original Order objects values already
        if (orderChanges.getCustName() != "") {
            // If the customer name was not left blank
            orderHistory.get(orderNum).setCustName(orderChanges.getCustName());
            // Set the orders's customer name to the new name it was changed to in the edit
        }
        if (orderChanges.getState() != "") {
            // If the state was not left blank
            orderHistory.get(orderNum).setState(orderChanges.getState());
            // Set the order's state to the new state it was changed to in the edit
        }
        if (orderChanges.getProductType() != "") {
            // If the product type was not left blank
            orderHistory.get(orderNum).setProductType(orderChanges.getProductType());
            // Set the order's product type to the new product type it was changed to in the edit
        }
        if (orderChanges.getArea().compareTo(new BigDecimal("0")) == 1
            || orderChanges.getArea().compareTo(new BigDecimal("0")) == -1) {
            // If the Big Decimal comparison is either 1 or 1, the new value is empty value 0 from the service layer
            orderHistory.get(orderNum).setArea(orderChanges.getArea());
            // Set the orders area to the new area amount it was changed to in the edit
        }
        return orderHistory.get(orderNum);
        // Return the order changed to verify success
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNum) {
        Order removedOrder = orderHistory.remove(orderNum);
        // Find the order based on the order number, which is always unique and remove it
        return removedOrder;
        // Return it to the service layer
    }

    @Override
    public State getState(String state) {
        return states.get(state);
        // Return the state object to the service layer
    }

    @Override
    public List<State> getAllStates() {
        return new ArrayList<>(states.values());
        // Return all states to the service layer
    }

    @Override
    public Product getProduct(String product) {
        return products.get(product);
        // Return the product object to the service layer
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
        // return all products to the service layer
    }
}
