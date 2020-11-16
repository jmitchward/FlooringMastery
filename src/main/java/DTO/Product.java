package DTO;

import java.math.BigDecimal;

public class Product {

    String type;
    BigDecimal costPerSqFt;
    BigDecimal laborPerSqFt;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getCostPerSqFt() {
        return costPerSqFt;
    }

    public void setCostPerSqFt(BigDecimal costPerSqFt) {
        this.costPerSqFt = costPerSqFt;
    }

    public BigDecimal getLaborPerSqFt() {
        return laborPerSqFt;
    }

    public void setLaborPerSqFt(BigDecimal laborPerSqFt) {
        this.laborPerSqFt = laborPerSqFt;
    }

}
