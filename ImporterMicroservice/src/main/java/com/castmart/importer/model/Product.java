package com.castmart.importer.model;

import java.io.Serializable;
import java.util.UUID;

public class Product implements Serializable {
    private String id;
    private String name;
    private String description;
    private String provider;
    private boolean available;
    private String measurementUnits;

    public Product() {}

    public Product(String id, String name, String description, String provider, boolean available, String mu) {
        this.id = UUID.fromString(id).toString(); // to validate
        this.name = name;
        this.description = description;
        this.provider = provider;
        this.available = available;
        this.measurementUnits = mu;
    }

    public Product(UUID id, String name, String description, String provider, boolean available, String mu) {
        if (id == null) throw new IllegalArgumentException("ID must not be null!!!");
        this.id = id.toString();
        this.name = name;
        this.description = description;
        this.provider = provider;
        this.available = available;
        this.measurementUnits = mu;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMeasurementUnits() {
        return measurementUnits;
    }

    public void setMeasurementUnits(String measurementUnits) {
        this.measurementUnits = measurementUnits;
    }
}
