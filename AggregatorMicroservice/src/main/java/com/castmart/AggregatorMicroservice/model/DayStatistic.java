package com.castmart.AggregatorMicroservice.model;

import java.io.Serializable;
import java.util.Objects;

public class DayStatistic implements Serializable {
    private long productsCreated;
    private long productsUpdated;
    private String day;

    public long getProductsCreated() {
        return productsCreated;
    }

    public void setProductsCreated(long productsCreated) {
        this.productsCreated = productsCreated;
    }

    public long getProductsUpdated() {
        return productsUpdated;
    }

    public void setProductsUpdated(long productsUpdated) {
        this.productsUpdated = productsUpdated;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int)productsCreated;
        result = 31 * result + (int)productsUpdated;
        result = 31 * result + day.length();
        return result;
    }
}
