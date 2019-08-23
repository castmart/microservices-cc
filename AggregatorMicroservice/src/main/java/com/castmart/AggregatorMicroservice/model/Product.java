package com.castmart.AggregatorMicroservice.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="product")
public class Product implements Serializable {

    @Id
    private String id;

    private String name;
    private String description;
    private String provider;
    @Column(nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean available;
    private String measurementUnits;

    @Column(name = "creationTimestamp", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTimestamp;

    @Column(name = "editionTimestamp", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date editionTimestamp;

    public Product() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = UUID.fromString(id).toString();
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

    public boolean getAvailable() {
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

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Date getEditionTimestamp() {
        return editionTimestamp;
    }

    public void setEditionTimestamp(Date editionTimestamp) {
        this.editionTimestamp = editionTimestamp;
    }
}
