package ServiceLayer;

public class OrderNotFoundException extends Throwable {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
