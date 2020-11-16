package ServiceLayerTest;

import DAO.*;
import DAOTest.OrderDAOStub;
import DTO.Order;
import DTO.Product;
import ServiceLayer.EmptyFieldException;
import ServiceLayer.InvalidDateException;
import ServiceLayer.OrderNotFoundException;
import ServiceLayer.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    private final OrderService service;

    public OrderServiceTest() throws OrderFileNotFound, ProductFileNotFound, TaxFileNotFound, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        service = context.getBean("service", OrderService.class);

    }

    @BeforeEach
    public void setUp() {

    }

        // State = KY
        // TaxRate = 6.00
        // Product Type = Wood
        // Area = 101

    @Test
    public void testServiceCalculations() {
        // Assign & Act
        // Wood
        BigDecimal taxRate = new BigDecimal("0.06");
        BigDecimal programTaxRate = new BigDecimal("6.00");
        BigDecimal calculatedTaxRate = (programTaxRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN));
        BigDecimal area = new BigDecimal("150");
        BigDecimal costPerSqFt = new BigDecimal("5.15");
        BigDecimal laborCostPerSqFt = new BigDecimal("4.75");
        BigDecimal materialCost = service.calculateCost(area, costPerSqFt) ;
        BigDecimal laborCost = service.calculateCost(area, laborCostPerSqFt);
        BigDecimal tax = service.calculateTax(materialCost, laborCost, taxRate);
        BigDecimal total = service.calculateTotal(materialCost, laborCost, tax);
        assertEquals(new BigDecimal("772.50"), materialCost);
        assertEquals(new BigDecimal("712.50"), laborCost);
        assertEquals(new BigDecimal("89.10"),tax );
        assertEquals(new BigDecimal("1574.10"), total);
        assertEquals(taxRate, calculatedTaxRate);
    }

    @Test
    public void testValidateOrderError() {
        Order testOrder = new Order(1);
        testOrder.setCustName("Test Object");
        testOrder.setState("");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("101"));
        testOrder.setDate(LocalDate.now());
        try {
            service.validateOrder(testOrder);
        } catch (EmptyFieldException e) {
            return;
        }
        fail("Empty field exception not triggered");
    }

    @Test
    public void testValidateOrder() {
        Order testOrder = new Order(1);
        testOrder.setCustName("Test Object");
        testOrder.setState("KY");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("101"));
        testOrder.setDate(LocalDate.now());
        try {
            service.validateOrder(testOrder);
        } catch (EmptyFieldException e) {
            fail("Error triggered when it should not be");
        }
        return;
    }

    @Test
    public void testPopulateFields() {
        BigDecimal programTaxRate = new BigDecimal("6.00");
        BigDecimal calculatedTaxRate = (programTaxRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN));
        BigDecimal costPerSqFt = new BigDecimal("5.15");
        BigDecimal laborCostPerSqFt = new BigDecimal("4.75");
        Order testOrder = new Order(1);
        testOrder.setCustName("Test Object");
        testOrder.setState("KY");
        testOrder.setProductType("Wood");
        testOrder.setArea(new BigDecimal("150"));
        testOrder.setDate(LocalDate.now());
        testOrder.setCostPerSqFt(costPerSqFt);
        testOrder.setLaborPerSqFt(laborCostPerSqFt);
        testOrder.setTaxRate(programTaxRate);

        service.populateFields(testOrder);

        assertEquals(new BigDecimal("772.50"), testOrder.getMaterialCost());
        assertEquals(new BigDecimal("712.50"), testOrder.getLaborCost());
        assertEquals(new BigDecimal("89.10"),testOrder.getTax() );
        assertEquals(new BigDecimal("1574.10"), testOrder.getTotal());
    }

    @Test
    public void testCheckDate() {
        String date = "10-25-2020";
        LocalDate returnedDate;
        try {
            returnedDate = service.checkDate(date);
            assertEquals(returnedDate.format(DateTimeFormatter.ofPattern("MM-dd-YYYY")), date);
        } catch (InvalidDateException e) {
            fail("Error was not expected.");
        }
    }

    @Test
    public void testCheckProductType() {
        String product = "Wood";
        boolean found = service.checkProductType(product);
        assertEquals(true, found);
        product = "Cement";
        found = service.checkProductType(product);
        assertEquals(false, found);
    }

    @Test
    public void testCheckState() {
        String state = "KY";
        boolean found = service.checkState(state);
        assertEquals(true, found);
        state = "TN";
        found = service.checkState(state);
        assertEquals(false, found);
    }

    @Test
    public void testCheckArea() {
        BigDecimal area = new BigDecimal("101");
        boolean found = service.checkArea(area);
        assertEquals(true, found);
        area = new BigDecimal("99");
        found = service.checkArea(area);
        assertEquals(false, found);
    }

    @Test
    public void testCheckRemoveOrderA() {
        LocalDate date = LocalDate.now();
        try {
            Order onlyOrder = service.removeOrder(date, 1);
            assertEquals(onlyOrder.getDate(), date);
            assertEquals(onlyOrder.getOrderNum(), 1);
        } catch (OrderNotFoundException e) {
            fail("Order exists in the stub.");
        }
    }

    @Test
    public void testCheckRemoveOrderB() {
        LocalDate date = LocalDate.of(2020,11, 04);
        try {
            Order onlyOrder = service.removeOrder(date, 1);
        } catch (OrderNotFoundException e) {
            return;
        }
        fail("Order on that date does not exist");
    }
}
