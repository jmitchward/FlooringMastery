package Controller;

import DAO.CannotOpenFile;
import DAO.OrderFileNotFound;
import DAO.ProductFileNotFound;
import DAO.TaxFileNotFound;
import DTO.Order;
import ServiceLayer.EmptyFieldException;
import ServiceLayer.InvalidDateException;
import ServiceLayer.OrderNotFoundException;
import ServiceLayer.OrderService;
import View.OrderView;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderController {
    OrderView view;
    OrderService service;

    OrderController(OrderView view, OrderService service) {
        this.view = view;
        this.service = service;
        }

    public void programStart() throws IOException, CannotOpenFile, InvalidDateException, OrderFileNotFound, TaxFileNotFound, ProductFileNotFound, EmptyFieldException, OrderNotFoundException {
        view.displayMenu();
        // Display the menu options
        int selection = view.menuSelection();
        // Selection variable for menu navigation
        menuNavigator(selection);
        // Call the menu function with the requested user action
    }

    void menuNavigator(int selection) throws InvalidDateException, IOException, CannotOpenFile, OrderFileNotFound, ProductFileNotFound, TaxFileNotFound, EmptyFieldException, OrderNotFoundException {
        switch (selection) {
            case 1:
                displayOrders();
                programStart();
            case 2:
                addOrder();
                programStart();
            case 3:
                editOrder();
                programStart();
            case 4:
                removeOrder();
                programStart();
            case 5:
                exportData();
                // This function call cascades to the DAO, writing each order in the order history to the back up file
                programStart();
            case 6:
                view.displayExit();
                // Simple exit message
                service.shutDown();
                // This function call cascades to the DAO, saving each order in the order history to its respective file
                System.exit(0);
            default:
                view.displayInvalidSelection();
                // Simple invalid input message
                programStart();
        }
    }

    void displayOrders() {
        LocalDate date = view.getDate();
        // Retrieve the date from the view, the getDate function will always return a LocalDate
        view.displayOrders(service.getOrders(date));
        // Call the view function to display the orders retrieved by the service layer given the LocalDate
    }

    void addOrder() {
        boolean errorState;

        Order newOrder = new Order(service.fetchNewNum());
        // Create a new Order object using the order number generator

        newOrder.setCustName(view.getCustName());
        // Set the customer name to the retrieved value. Input checks happen on add confirmation

        do {
            String state = view.getState(service.returnStates());
            if (service.checkState(state)) {
                // Fetch the user input for the state, send it to the service layer to ensure it is valid
                // This method returns true if it is valid, false if it is not
                errorState = false;
                // Exit the do-while loop for state check
                newOrder.setState(state);
                // Store the valid state
            }
            else {
                view.displayError("Invalid State");
                // When the If statement does not trigger, the service layer has returned false
                errorState = true;
                // Begin while loop prompting the user for new input
            }
        } while (errorState);

        do {
            String product = view.getProduct(service.returnProducts());
            if (service.checkProductType(product)) {
                // Fetch the user input for the product, send it to the service layer to ensure it is valid
                // This method returns true if it is valid, false if it is not
                errorState = false;
                // Exit the do-while loop for state check
                newOrder.setProductType(product);
                // Store the valid product
            }
            else {
                view.displayError("Invalid Product");
                // When the If statement does not trigger, the service layer has returned false
                errorState = true;
                // Begin while loop prompting the user for new input
            }
        } while (errorState);

        do {
            BigDecimal area = view.getArea();
            if (service.checkArea(area)) {
                // Fetch the user input for the area, send it to the service layer to ensure it is valid
                // This method returns true if it is valid, false if it is not
                errorState = false;
                // Exit the do-while loop for state check
                newOrder.setArea(area);
                // Store the valid area
            }
            else {
                view.displayError("Invalid area");
                // When the If statement does not trigger, the service layer has returned false
                errorState = true;
                // Begin while loop prompting the user for new input
            }
        } while (errorState);

        newOrder.setDate(LocalDate.now());
        service.populateFields(newOrder);
        view.displayView(newOrder);
        // All new orders will be marked with today's date
        if (view.getConfirmation()) {
            // Prompt the user to confirm changes, the view method returns true if confirmed
            try {
                service.addOrder(newOrder);
                // If the addition is confirmed, advance to the service layer
                // The service layer method will throw an error if any field is blank

            } catch (EmptyFieldException e) {
                // Catch the error from the service layer
                view.displayError(e.getMessage());
                // Scold the user for their lethargy
                addOrder();
                // Recursive call to do it better
            }

        }
        else {
            view.displayError("Created order abandoned.");
            // If the view method returns false, the changes were not confirmed
        }
    }


    public void editOrder() throws InvalidDateException, CannotOpenFile, OrderFileNotFound, IOException, TaxFileNotFound, EmptyFieldException, OrderNotFoundException, ProductFileNotFound {
        int changes = 0;
        Order foundOrder;
        int orderNum;
        LocalDate date;
        // Defining scope of variables being used in the do-whiles
        boolean invalidSearch = false;
        do {
            date = view.getDate();
            // Retrieve the date the order was made from the user
            view.displayOrders(service.getOrders(date));
            // Display the orders on the given date
            if (service.getOrders(date).size() == 0) {
                programStart();
            }
            orderNum = view.getOrderNum();
            // Retrieve the order number from the user
            try {
                foundOrder = service.getOrder(date, orderNum);
                Order orderChanges = new Order(foundOrder.getOrderNum());
                // Index the change object by giving it the same
                // Try to retrieve the order from the service layer, which calls the get order in the DAO
                invalidSearch = false;
                // Avoid any potential for a endless loop
                view.displayEdit(foundOrder);
                // Display the found order to the user
                String newName = view.getCustName();
                // Get a new customer name
                if (!newName.isEmpty()) {
                    // If the user did not skip the customer name field
                    orderChanges.setCustName(newName);
                    changes++;
                    // Set the change object to the new value
                    // This value is checked again in the DAO
                } else {
                    view.displayError("Order Name unchanged.");
                    orderChanges.setCustName(foundOrder.getCustName());
                    // Otherwise set the change object to the old value
                    // This value is checked again in the DAO
                }
                String state = view.getState(service.returnStates());
                // Get a new state
                if (service.checkState(state)) {
                    // If the service verifies that the state entered is a valid one, then the user wants to change the value
                    orderChanges.setState(state);
                    changes++;
                    // Add it to the new object storing changes
                    // This value is checked again in the DAO
                } else {
                    view.displayError("Order State unchanged.");
                    orderChanges.setState(foundOrder.getState());}
                // If the service determines the input is not valid, the state will be copied over
                // This value is checked again in the DAO
                String product = view.getProduct(service.returnProducts());
                // Get a new product
                if (service.checkProductType(product)) {
                    // If the service verifies that the product entered is a valid one, then the user wants to change the value
                    orderChanges.setProductType(product);
                    changes++;
                    // Add it to the new object storing changes
                    // This value is checked again in the DAO
                } else {
                    view.displayError("Order Product unchanged.");
                    orderChanges.setProductType(foundOrder.getProductType());
                    // If the service determines the input is not valid, the state will be copied over
                    // This value is checked again in the DAO
                }
                BigDecimal area = view.getArea();
                // Get a new area value
                if (!area.equals(0)) {
                    if (service.checkArea(area)) {
                        // If the service verifies that the area entered is a valid one, then the user wants to change the value
                        orderChanges.setArea(area);
                        changes++;
                        // Add it to the new object storing changes
                        // This value is checked again in the DAO
                    } else {
                        view.displayError("Area not changed.");
                        orderChanges.setArea(foundOrder.getArea());
                        // If the service determines the input is not valid, the state will be copied over
                        // This value is checked again in the DAO
                    }
                }
                orderChanges.setDate(foundOrder.getDate());
                // Ensure that the double check in the DAO has the correct date
                if (changes > 0) {
                    service.populateFields(orderChanges);
                    view.displayView(orderChanges);
                    if (view.getConfirmation()) {
                        // Call the view method to verify changes
                        // This method returns true if the user confirms changes
                        foundOrder = service.editOrder(orderChanges);
                        // Call the service layer method to make the confirmed changes
                    }
                    else {
                        view.displayError("Changes abandoned.");
                    }
                }
                else {
                    view.displayError("Original values not changed.");
                }

                view.displayView(foundOrder);
                // Display the confirmed changes
            } catch (OrderNotFoundException e) {
                // if the service layer at any time cannot find the order, it will throw the exception
                invalidSearch = true;
                // Start the while loop
                view.displayError(e.getMessage());
                // Tell the user what has happened
            }
        } while (invalidSearch);
    }

    void removeOrder() {
        boolean invalidSearch = false;
        do {
            try {
                LocalDate date = view.getDate();
                // Get the date for the order from the user
                view.displayOrders(service.getOrders(date));
                // Display the orders on the date
                int orderNum = view.getOrderNum();
                // Get the order number for the order from the user
                view.displayView(service.getOrder(date, orderNum));
                // Display the order the user has chosen
                if (view.getConfirmation()) {
                    // View method call to confirm deletion
                    // This method returns true if the user confirms the action
                    service.removeOrder(date, orderNum);
                }
                else {
                    view.displayError("Order not removed.");
                    // When the If state checks false, the user rejects the action
                }
                invalidSearch = false;
            } catch (OrderNotFoundException e) {
                // The service layer will throw an exception if it cannot find the order
                view.displayError(e.getMessage());
                // Tell the user what has happened
                invalidSearch = true;
                // Start the while loop to find the appropriate order
                }
        } while (invalidSearch);
    }

    void exportData() throws IOException, CannotOpenFile {
        service.exportOrders();
        // Calls the service layer to print all orders to the back up folder
    }
}
