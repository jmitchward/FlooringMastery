package ServiceLayer;

import DAO.CannotOpenFile;
import DAO.OrderFileNotFound;
import DAO.ProductFileNotFound;
import DAO.TaxFileNotFound;
import DTO.Order;
import DTO.Product;
import DTO.State;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderServiceInterface {
//    void startUp() throws OrderFileNotFound, ProductFileNotFound, TaxFileNotFound;
    int fetchNewNum();
    BigDecimal calculateCost(BigDecimal area, BigDecimal cost);
    BigDecimal calculateTax(BigDecimal material, BigDecimal labor, BigDecimal taxRate);
    BigDecimal calculateTotal(BigDecimal material, BigDecimal labor, BigDecimal tax);
    void validateOrder(Order order) throws EmptyFieldException;
    void addOrder(Order order) throws EmptyFieldException;
    Order editOrder(Order orderEdit) throws OrderNotFoundException;
    Order removeOrder(LocalDate date, int orderNum) throws OrderNotFoundException;
    LocalDate checkDate(String date) throws InvalidDateException;
    boolean checkProductType(String product);
    boolean checkState(String state);
    boolean checkArea(BigDecimal area);
    void exportOrders() throws IOException, CannotOpenFile;
    List<State> returnStates();
    List<Product> returnProducts();
    void shutDown() throws IOException, CannotOpenFile;
}
