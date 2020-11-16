package DAOTest;

import DAO.OrderDAO;
import DAO.OrderFileNotFound;
import DAO.ProductFileNotFound;
import DAO.TaxFileNotFound;
import DTO.Order;
import DTO.Product;
import DTO.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDAOTest {

    OrderDAO testDAO;

    @BeforeEach
    public void setUp() throws OrderFileNotFound, ProductFileNotFound, TaxFileNotFound, IOException {
        String testFolder = "testOrders";
        testDAO = new OrderDAO(testFolder);
    }

    @Test
    public void testAddOrder() {
        // Arrange
        Order testOrder = new Order(testDAO.getNextOrderNum());
        testOrder.setCustName("Test Object");
        testOrder.setState("KY");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("0"));
        testOrder.setDate(LocalDate.now());
        // Act
        testDAO.addOrder(testOrder);
        // Assert
        Order retrievedOrder = testDAO.getOrder(testOrder.getDate(), testOrder.getOrderNum());
        assertEquals(retrievedOrder.getCustName(), testOrder.getCustName());
        assertEquals(retrievedOrder.getState(), testOrder.getState());
        assertEquals(retrievedOrder.getProductType(), testOrder.getProductType());
        assertEquals(retrievedOrder.getArea(), testOrder.getArea());
    }

    @Test
    public void testGetDate() {
        // Arrange
        String fileName = "Orders_11042020.txt";
        // Act
        LocalDate getDate = testDAO.getDate(fileName);
        // Assert
        assertEquals("2020-11-04", getDate.toString());
    }

    @Test
    public void testEditOrder() {
        // Arrange
        Order testOrder = new Order(testDAO.getNextOrderNum());
        testOrder.setCustName("Test Object");
        testOrder.setState("KY");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("0"));
        testOrder.setDate(LocalDate.now());

        Order orderChanges = new Order(testOrder.getOrderNum());
        orderChanges.setCustName("");
        orderChanges.setState("CA");
        orderChanges.setProductType("Tile");
        orderChanges.setArea(new BigDecimal("0"));
        orderChanges.setDate(LocalDate.now());
        // Act
        testDAO.addOrder(testOrder);
        testDAO.editOrder(testOrder.getOrderNum(), orderChanges);
        Order retrievedOrder = testDAO.getOrder(testOrder.getDate(), testOrder.getOrderNum());
        // Assert
        assertEquals(testOrder.getCustName(), retrievedOrder.getCustName());
        assertEquals(orderChanges.getState(), retrievedOrder.getState());
        assertEquals(orderChanges.getProductType(), retrievedOrder.getProductType());
        assertEquals(testOrder.getArea(), retrievedOrder.getArea());
    }

    @Test
    public void testRemoveOrder() {
        // Arrange
        Order testOrder = new Order(1);
        testOrder.setCustName("Test Object");
        testOrder.setState("KY");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("101"));
        testOrder.setDate(LocalDate.now());

        Order secondOrder = new Order(2);
        secondOrder.setCustName("Test Object 2");
        secondOrder.setState("CA");
        secondOrder.setProductType("Tile");
        secondOrder.setArea(new BigDecimal("101"));
        secondOrder.setDate(LocalDate.now());
        // Act
        testDAO.addOrder(testOrder);
        testDAO.addOrder(secondOrder);
        testDAO.removeOrder(testOrder.getDate(), testOrder.getOrderNum());
        // Assert
        assertEquals(1, testDAO.getOrders(secondOrder.getDate()).size());
        assertEquals(null, testDAO.getOrder(testOrder.getDate(), testOrder.getOrderNum()));
        assertEquals(secondOrder.getCustName(), testDAO.getOrder(secondOrder.getDate(), secondOrder.getOrderNum()).getCustName());
    }

    @Test
    public void testGetNextOrderNum() {
        // Assign
        int newOrderNum = testDAO.getNextOrderNum();
        Order testOrder = new Order(newOrderNum);
        // Act
        testDAO.addOrder(testOrder);
        int secondOrderNum = testDAO.getNextOrderNum();
        // Assert
        assertNotEquals(newOrderNum, secondOrderNum);
    }

    @Test
    public void testGetAllOrders() {
        // Arrange
        Order testOrder = new Order(1);
        testOrder.setCustName("Test Object");
        testOrder.setState("KY");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("101"));
        testOrder.setDate(LocalDate.now());

        Order secondOrder = new Order(2);
        secondOrder.setCustName("Test Object 2");
        secondOrder.setState("CA");
        secondOrder.setProductType("Tile");
        secondOrder.setArea(new BigDecimal("101"));
        secondOrder.setDate(LocalDate.now());
        // Act
        testDAO.addOrder(testOrder);
        testDAO.addOrder(secondOrder);
        List<Order> allOrders = testDAO.getOrders(testOrder.getDate());
        // Assert
        assertEquals(2, allOrders.size());
        assertTrue(allOrders.contains(testOrder));
        assertTrue(allOrders.contains(secondOrder));
    }

    @Test
    public void testGetState() {
        // Assign & Act
        State state = testDAO.getState("KY");
        // Assert
        assertEquals(state.getName(), "Kentucky");
        assertEquals(state.getState(), "KY");
        assertEquals(state.getTaxRate(), new BigDecimal("6.0"));
    }

    @Test
    public void testGetProduct() {
        // Assign & Act
        Product product = testDAO.getProduct("Wood");
        // Assert
        assertEquals(product.getType(), "Wood");
        assertEquals(product.getCostPerSqFt(), new BigDecimal("5.15"));
        assertEquals(product.getLaborPerSqFt(), new BigDecimal("4.75"));
    }
}
