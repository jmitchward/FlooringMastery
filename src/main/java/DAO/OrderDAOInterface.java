package DAO;

import DTO.Order;
import DTO.Product;
import DTO.State;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDAOInterface {
    void loadProducts() throws ProductFileNotFound;
    void loadStates() throws TaxFileNotFound;
    void loadOrders() throws OrderFileNotFound;
    LocalDate getDate(String fileName);
    int getNextOrderNum();
    void saveTrigger() throws IOException, CannotOpenFile;
    void saveOrders() throws IOException, CannotOpenFile;
    void exportOrders() throws IOException, CannotOpenFile;
    Product unmarshallProducts(String product);
    State unmarshallStates(String state);
    String marshallOrders(Order order);
    Order unmarshallOrders(String order, LocalDate date);
    List<Order> getOrders(LocalDate date);
    Order getOrder(LocalDate date, int orderNum);
    Order addOrder(Order order);
    Order editOrder(int orderNum, Order orderChanges);
    Order removeOrder(LocalDate date, int orderNum);
    State getState(String state);
    List<State> getAllStates();
    Product getProduct(String product);
    List<Product> getAllProducts();


}
