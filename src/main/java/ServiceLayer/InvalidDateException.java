package ServiceLayer;

public class InvalidDateException extends Throwable {
    public InvalidDateException(String message) {
        super(message);
    }
    public InvalidDateException(String message, Exception e) {
        super(message, e);
    }
}
