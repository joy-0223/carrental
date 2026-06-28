package com.example.carrental.model;

public class Car {
    private int id;
    private String brand;
    private String model;
    private double pricePerDay;
    private String description;
    private String imageUrl;
    private boolean isAvailable;

    public Car() {}

    public Car(String brand, String model, double pricePerDay, String description, String imageUrl) {
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isAvailable = true;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}