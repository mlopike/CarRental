package by.bsac.carrental.model;

import java.math.BigDecimal;

/**
 * Модель автомобиля.
 */
public class Car {
    private int id;
    private String brand;
    private String model;
    private int year;
    private String licensePlate;
    private String color;
    private String transmission;     // AUTO, MANUAL
    private String fuelType;         // PETROL, DIESEL, ELECTRIC
    private BigDecimal pricePerDay;
    private String imageUrl;
    private String description;
    private String status;           // AVAILABLE, RENTED, REPAIR

    public Car() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFullName() {
        return brand + " " + model + " (" + year + ")";
    }
}
