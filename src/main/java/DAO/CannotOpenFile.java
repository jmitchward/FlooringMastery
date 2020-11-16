package DAO;

public class CannotOpenFile extends Throwable {
    CannotOpenFile(String message) {
        super(message);
    }
    CannotOpenFile(String message, Exception e) {
        super(message, e);
    }

}
