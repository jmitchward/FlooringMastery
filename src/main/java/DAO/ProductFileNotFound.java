package DAO;

import java.io.FileNotFoundException;

public class ProductFileNotFound extends Throwable {

    public ProductFileNotFound(String message) {
        super(message);
    }
    public ProductFileNotFound(String message, Exception e) {
        super(message, e);
    }
}
