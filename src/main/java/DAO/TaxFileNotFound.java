package DAO;

import java.io.FileNotFoundException;

public class TaxFileNotFound extends Throwable {
    public TaxFileNotFound(String message) {
        super(message);
    }
    public TaxFileNotFound(String message, Exception e) {
        super(message, e);
    }
}
