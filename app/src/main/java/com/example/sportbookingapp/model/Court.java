package com.example.sportbookingapp.model;

import java.io.Serializable;

public class Court implements Serializable {
    private int id;
    private String name;
    private String address;
    private String type;
    private double price;       // Ví dụ: 150000
    private String imageName;   // Tên ảnh trong drawable
    private double rating;      // Ví dụ: 4.5
    private String facilities;  // Ví dụ: "Wifi,Canteen,Parking"
    private double lat;         // Dùng cho Google Map
    private double lng;         // Dùng cho Google Map

    private String description; // "Mô tả & Tiện ích..."
    private String openTime;    // "06:00"
    private String closeTime;   // "23:00"

    // Constructor cập nhật đầy đủ
    public Court(int id, String name, String address, String type, double price, String imageName,
                 double rating, String facilities, double lat, double lng,
                 String description, String openTime, String closeTime) {
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
        this.description = description;
        this.openTime = openTime;
        this.closeTime = closeTime;
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

    public String getDescription() { return description; }
    public String getOpenTime() { return openTime; }
    public String getCloseTime() { return closeTime; }

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

    public void setDescription(String description) { this.description = description; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
}