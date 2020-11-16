package View;

import DTO.Order;
import DTO.Product;
import DTO.State;
import UserInput.userIO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrderView {
    userIO userInput;

    OrderView(userIO userInput) {
        this.userInput = userInput;
    }

    public void displayMenu() {
        userInput.print("==========Trajan's Wholesale Flooring=========");
        userInput.print("**Flooring backed by the might of the Empire!**");
        userInput.print("1. Display Orders");
        userInput.print("2. Add an Order.");
        userInput.print("3. Edit an Order.");
        userInput.print("4. Remove an Order.");
        userInput.print("5. Export All Data.");
        userInput.print("6. Exit");
    }

    public int menuSelection() {
        return userInput.readInt("Selection: ");
    }

    public void displayOrders(List<Order> orders) {
        if (orders.size() == 0) {
            // If the received list is empty, then there are no orders
            userInput.print("No orders on this date.");
        }
        else {
            // Otherwise, iterate over the orders and display their order number and the customer name
            for (Order order : orders) {
                userInput.print("====Order==== ");
                userInput.print("Order #: " + order.getOrderNum() + " | Name: " + order.getCustName());
                userInput.print("Material Cost: $" + order.getMaterialCost());
                userInput.print("Labor Cost: $" + order.getLaborCost());
                userInput.print("Tax : $" + order.getTax() + " | Total: $" + order.getTotal());
            }
        }
    }

    public void displayEdit(Order order) {
        userInput.print("====Order==== ");
        userInput.print("Invalid changes will not be made.");
        userInput.print("Order #: " + order.getOrderNum() + " | Name: " + order.getCustName());
        userInput.print("State: " + order.getState() + " | Product Type: " + order.getProductType());
        userInput.print("Area: " + order.getArea());
        userInput.print("Leave blank if no changes needed.");
        // Displays the order information for edit
    }

    public void displayView(Order order) {
        userInput.print("====Order==== ");
        userInput.print("Order #: " + order.getOrderNum() + " | Name: " + order.getCustName());
        userInput.print("State: " + order.getState() + " | Product Type: " + order.getProductType());
        userInput.print("Area: " + order.getArea());
        userInput.print("Material Cost: $" + order.getMaterialCost());
        userInput.print("Labor Cost: $" + order.getLaborCost());
        userInput.print("Tax : $" + order.getTax() + " | Total: $" + order.getTotal());
        // Displays the order information
    }

    public String getCustName() {
        return userInput.readString("Customer Name: ");
        // Returns a string for the customers name
    }

    public String getState(List<State> states) {
        userInput.print("States:");
        for (State state : states) {
            userInput.print(state.getState());
        }
        return userInput.readString("Selection: ");
        // Returns a string for the state
    }

    public String getProduct(List<Product> products) {
        userInput.print("Products:");
        for (Product product : products) {
            userInput.print(product.getType());
        }
        return userInput.readString("Selection: ");
        // Returns a string for the proudct type
    }

    public BigDecimal getArea() {
        return userInput.readBig("Area: ");
        // Returns a Big Decimal value for the area, userInput has a formatting catch to ensure this is always a big decimal
    }

    public LocalDate getDate() {
        return userInput.readDate("Date: ");
        // Returns a Local Date value for the date, userInput has a formatting catch to ensure this is always a date
    }

    public int getOrderNum() {
        return userInput.readInt("Order #: ");
        // Returns an int value for the order number, userInput has a formatting catch to ensure this is always an int
    }

    public void displayExit() {
        userInput.print("Gratias Tibi");
        // Good bye in latin, I think.
    }

    public void displayInvalidSelection() {
        userInput.print("Invalid Selection");
        // Menu display
    }

    public void displayError(String message){
        userInput.print(message);
        // Display error messages or any sent message
    }

    public boolean getConfirmation() {
        String confirmation = userInput.readString("Confirm? ");
        if (confirmation.equalsIgnoreCase("y") || confirmation.equalsIgnoreCase("yes")) {
            // Match to any case a Y or a Yes
            return true;
            // Returns true, confirming a change
        }
        else if (confirmation.equalsIgnoreCase("n") || confirmation.equalsIgnoreCase("no")) {
            // Match to any case a N or a No
            return false;
            // Returns false, rejecting change
        }
        else {
            userInput.print("Please enter Y/N to confirm changes.");
            // Catch for any value not N, Y, No, or Yes. Also provides indication for appropriate input
            return getConfirmation();
            // Return a recursive function call for new input.
        }

    }



}
