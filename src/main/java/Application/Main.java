package Application;

import Controller.OrderController;
import DAO.CannotOpenFile;
import DAO.OrderFileNotFound;
import DAO.ProductFileNotFound;
import DAO.TaxFileNotFound;
import ServiceLayer.EmptyFieldException;
import ServiceLayer.InvalidDateException;
import ServiceLayer.OrderNotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Main {
    public static void main(String [] args) throws InvalidDateException, CannotOpenFile, IOException, OrderFileNotFound, ProductFileNotFound, TaxFileNotFound, EmptyFieldException, OrderNotFoundException {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        OrderController controller = context.getBean("controller", OrderController.class);
        controller.programStart();
    }
}
