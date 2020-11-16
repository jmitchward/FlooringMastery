package DAOTest;

import DAO.*;
import DTO.Order;
import DTO.Product;
import DTO.State;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderDAOStub implements OrderDAOInterface {

    public Order onlyOrder;
    public Product onlyProduct;
    public State onlyState;

    public OrderDAOStub() {
        onlyOrder = new Order(1);
        onlyOrder.setCustName("Test Object");
        onlyOrder.setState("KY");
        onlyOrder.setProductType("Wood");
        onlyOrder.setArea(new BigDecimal("101"));
        onlyOrder.setDate(LocalDate.now());

        onlyProduct = new Product();
        onlyProduct.setType("Wood");
        onlyProduct.setCostPerSqFt(new BigDecimal("5.15"));
        onlyProduct.setLaborPerSqFt(new BigDecimal("4.75"));

        onlyState = new State();
        onlyState.setState("KY");
        onlyState.setName("Kentucky");
        onlyState.setTaxRate(new BigDecimal("6.00"));
    }

    public OrderDAOStub(Order onlyOrder) {
        this.onlyOrder = onlyOrder;
    }

    @Override
    public void loadProducts() throws ProductFileNotFound {

    }

    @Override
    public void loadStates() throws TaxFileNotFound {

    }

    @Override
    public void loadOrders() throws OrderFileNotFound {

    }

    @Override
    public LocalDate getDate(String fileName) {
        return LocalDate.now();
    }

    @Override
    public int getNextOrderNum() {
        return 2;
    }

    @Override
    public void saveTrigger() throws IOException, CannotOpenFile {

    }

    @Override
    public void saveOrders() throws IOException, CannotOpenFile {

    }

    @Override
    public void exportOrders() throws IOException, CannotOpenFile {

    }

    @Override
    public Product unmarshallProducts(String product) {
        return null;
    }

    @Override
    public State unmarshallStates(String state) {
        return null;
    }

    @Override
    public String marshallOrders(Order order) {
        return null;
    }

    @Override
    public Order unmarshallOrders(String order, LocalDate date) {
        return null;
    }

    @Override
    public List<Order> getOrders(LocalDate date) {
        return null;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNum) {
        if (date.isEqual(onlyOrder.getDate()) && orderNum == onlyOrder.getOrderNum()) {
            return onlyOrder;
        }
        else return null;
    }

    @Override
    public Order addOrder(Order order) {
        if (order.getOrderNum() == onlyOrder.getOrderNum()) {
            return onlyOrder;
        }
        else {
            return null;
        }
    }

    @Override
    public Order editOrder(int orderNum, Order orderChanges) {
        return onlyOrder;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNum) {
        if (orderNum == onlyOrder.getOrderNum()) {
            return onlyOrder;
        }
        else {
            return null;
        }
    }

    @Override
    public State getState(String state) {
        if (state == onlyState.getState()) {
            return onlyState;
        }
        else {
            return null;
        }
    }

    @Override
    public List<State> getAllStates() {
        return null;
    }

    @Override
    public Product getProduct(String product) {
        if (product == onlyProduct.getType()) {
            return onlyProduct;
        }
        else {
            return null;
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return null;
    }
}
