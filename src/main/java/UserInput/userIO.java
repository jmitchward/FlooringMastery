package UserInput;


import java.math.BigDecimal;
import java.time.LocalDate;

public interface userIO {
    void print(String msg);

    double readDouble(String prompt);

    double readDouble(String prompt, double min, double max);

    float readFloat(String prompt);

    float readFloat(String prompt, float min, float max);

    int readInt(String prompt);

    int readInt(String prompt, int min, int max);

    long readLong(String prompt);

    long readLong(String prompt, long min, long max);

    BigDecimal readBig(String prompt);

    String readString(String prompt);

    LocalDate readDate(String prompt);
}
