package com.example.sportbookingapp.model;

public class Court {
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

    public void setId(int id) {
        this.id = id;
    }
}
