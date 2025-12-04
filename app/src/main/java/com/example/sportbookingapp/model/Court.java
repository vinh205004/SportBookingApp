package com.example.sportbookingapp.model;

import java.io.Serializable;

public class Court implements Serializable {
    private int id;
    private String name;
    private String address;
    private String type;
    private double price;
    private String imageName;
    private double rating;
    private String facilities;
    private double lat;
    private double lng;

    public Court(int id, String name, String address, String type, double price, String imageName, double rating, String facilities, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.price = price;
        this.imageName = imageName;
        this.rating = rating;
        this.facilities = facilities;
        this.lat = lat;
        this.lng = lng;
    }

    // --- GETTER ---
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getImageName() { return imageName; }
    public double getRating() { return rating; }
    public String getFacilities() { return facilities; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setType(String type) { this.type = type; }
    public void setPrice(double price) { this.price = price; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public void setRating(double rating) { this.rating = rating; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    public void setLat(double lat) { this.lat = lat; }
    public void setLng(double lng) { this.lng = lng; }
}