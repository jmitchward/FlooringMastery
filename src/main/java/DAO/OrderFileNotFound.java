package DAO;

import java.io.FileNotFoundException;

public class OrderFileNotFound extends Throwable {

    public OrderFileNotFound(String message) {
        super(message);
    }

    public OrderFileNotFound(String message, Exception e) {
        super(message, e);
    }
}
