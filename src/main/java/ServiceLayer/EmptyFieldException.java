package ServiceLayer;

public class EmptyFieldException extends Throwable {
    public EmptyFieldException(String message) {
        super(message);
    }

    public EmptyFieldException(String message, Exception e) {
        super(message, e);
    }
}
