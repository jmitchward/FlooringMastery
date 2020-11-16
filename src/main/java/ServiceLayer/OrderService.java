package ServiceLayer;

import DAO.*;
import DTO.Order;
import DTO.Product;
import DTO.State;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService implements OrderServiceInterface {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    OrderDAOInterface dao;

    public OrderService(OrderDAOInterface dao) {
        this.dao = dao;
    }

    @Override
    public int fetchNewNum() {
        return dao.getNextOrderNum();
        // Returns an auto incremented order number
    }

    @Override
    public BigDecimal calculateCost(BigDecimal area, BigDecimal cost) {
        return area.multiply(cost).setScale(2,  RoundingMode.HALF_DOWN);
        // Returns the Big Decimal result
    }

    @Override
    public BigDecimal calculateTax(BigDecimal material, BigDecimal labor, BigDecimal taxRate) {
        return (material.add(labor).setScale(2, RoundingMode.HALF_DOWN).multiply(taxRate).setScale(2, RoundingMode.HALF_DOWN));
        // Returns the Big Decimal result w/ a 2 decimal place context
    }

    @Override
    public BigDecimal calculateTotal(BigDecimal material, BigDecimal labor, BigDecimal tax) {
        return (material.add(labor).setScale(2, RoundingMode.HALF_DOWN)).add(tax).setScale(2,  RoundingMode.HALF_DOWN);
        // Returns the Big Decimal result w/ a 2 decimal place context
    }

    @Override
    public void validateOrder(Order order) throws EmptyFieldException {
        if (order.getCustName().equals("")
                || order.getDate().isBefore(LocalDate.now())
                || order.getState() == ""
                || order.getProductType() == ""
                || order.getArea().compareTo(new BigDecimal("100")) == 0
                || order.getArea().compareTo(new BigDecimal("100")) == -1) {
            throw new EmptyFieldException("Error. Fields must be filled in.");
            // Used in the add order path to ensure an order is complete
        }
    }

    @Override
    public void addOrder(Order order) throws EmptyFieldException {
        validateOrder(order);
        // For the received order, make sure its filled in
        // populateFields(order);
        // Calculate the values based on the object properties
        dao.addOrder(order);
        // Add the now complete Order object to the Hash Map
    }

    public Order populateFields(Order newOrder) {
        newOrder.setCostPerSqFt(dao.getProduct(newOrder.getProductType()).getCostPerSqFt());
        // Pull the Cost Per Sq Ft from the Product object
        newOrder.setLaborPerSqFt(dao.getProduct(newOrder.getProductType()).getLaborPerSqFt());
        // Pull the Labor Cost Per Sq Ft from the Product object
        newOrder.setTaxRate(dao.getState(newOrder.getState()).getTaxRate());
        // Pull the Tax Rate from the State object
        newOrder.setMaterialCost(calculateCost(newOrder.getArea(), newOrder.getCostPerSqFt()));
        // Material Cost
        newOrder.setLaborCost(calculateCost(newOrder.getArea(), newOrder.getLaborPerSqFt()));
        // Labor Cost
        newOrder.setTax(calculateTax(newOrder.getMaterialCost(), newOrder.getLaborCost(), (newOrder.getTaxRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN))));
        // Tax
        newOrder.setTotal(calculateTotal(newOrder.getMaterialCost(), newOrder.getLaborCost(), newOrder.getTax()));
        // Total
        return newOrder;
    }

    @Override
    public Order editOrder(Order orderEdit) throws OrderNotFoundException {
        Order foundOrder = dao.getOrder(orderEdit.getDate(), orderEdit.getOrderNum());
        // Get the order being edited by using the date and order number copied to the order changes object.
        if (foundOrder == null) {
            throw new OrderNotFoundException("No such order exists.");
            // Make sure that the order is again actually there.
        } else {
            dao.editOrder(foundOrder.getOrderNum(), orderEdit);
            // Call the DAO to swap out the changed values in the Order object already in the Hash Map
            populateFields(foundOrder);
            // Recalculate the fields based on any changes.
            return foundOrder;
        }
    }

    public List<Order> getOrders(LocalDate date) {
        List<Order> ordersOnDate = dao.getOrders(date).stream()
                .filter(order -> order.getDate().compareTo(date) == 0)
                .collect(Collectors.toList());
        // Stream to gather all orders on a given date into a list of Order objects
        return ordersOnDate;
        // Return this list to the controller
    }
    public Order getOrder(LocalDate date, int orderNum) throws OrderNotFoundException {
        if (dao.getOrder(date, orderNum) == null) {
            // If the DAO cannot retrieve the Order object based on the date and order number, it does not exist
            throw new OrderNotFoundException("No such order exists.");
        } else {
            return dao.getOrder(date, orderNum);
            // Return the found item to the controller
        }
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNum) throws OrderNotFoundException {
        Order foundOrder = dao.getOrder(date, orderNum);
        // Find the order in the DAO
        if (foundOrder == null) {
            // If it cannot be found, it does not exist
            throw new OrderNotFoundException("No such order exists.");
        } else {
            dao.removeOrder(date, orderNum);
            // If it DOES exist, call the DAO method to remove it
            return foundOrder;
            // Return it to the controller-> view to display
        }
    }

    @Override
    public LocalDate checkDate(String date) throws InvalidDateException {
        try {
            return LocalDate.parse(date, formatter);
            // Try to parse the String entered
        } catch (DateTimeException e) {
            throw new InvalidDateException("Invalid Date Entry");
            // If it cant, throw an error
        }
    }

    @Override
    public boolean checkProductType(String product) {
        Product retrievedProduct = dao.getProduct(product);
        // Try to retrieve the product object based on the user input product string
        return retrievedProduct != null;
        // If it is not null, then it is a valid product type
    }

    @Override
    public boolean checkState(String state) {
        State retrievedState = dao.getState(state);
        // Try to retrieve the state object based on the user input state string
        return retrievedState != null;
        // If it is not null, then it is a valid state
    }

    @Override
    public boolean checkArea(BigDecimal area) {
        return (area.compareTo(new BigDecimal("100")) != 0)
                && area.compareTo(new BigDecimal("100")) != -1;
        // if the area value is either equal to or less than 100, this function returns false
        // the Big Decimal compareTo() function returns 0 when equal, -1 when lesser than.
    }

    @Override
    public void exportOrders() throws IOException, CannotOpenFile {
        dao.exportOrders();
        // Pass through export hash map to the backup folder
    }

    @Override
    public List<State> returnStates() {
        return dao.getAllStates();
    }

    @Override
    public List<Product> returnProducts() {
        return dao.getAllProducts();
    }

    @Override
    public void shutDown() throws IOException, CannotOpenFile {
        dao.saveTrigger();
        // Pass through save hash map
    }

}
