package com.example.sportbookingapp.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private int id;
    private int userId;
    private int courtId;
    private String courtName;
    private String courtImage;
    private String date;        // Format: dd/MM/yyyy
    private String startTime;   // Format: HH:mm
    private String endTime;     // Format: HH:mm
    private double totalPrice;
    private String status;      // "CONFIRMED", "CANCELLED", "PENDING"
    private String paymentMethod; // "CASH", "BANKING"
    private int isDiscounted;

    public Booking(int id, int userId, int courtId, String courtName, String courtImage,
                   String date, String startTime, String endTime,
                   double totalPrice, String status, String paymentMethod) {
        this.id = id;
        this.userId = userId;
        this.courtId = courtId;
        this.courtName = courtName;
        this.courtImage = courtImage;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    // Constructor rút gọn để tạo Booking mới (chưa có ID)
    public Booking(int userId, int courtId, String courtName, String courtImage,
                   String date, String startTime, String endTime,
                   double totalPrice, String status, String paymentMethod) {
        this.userId = userId;
        this.courtId = courtId;
        this.courtName = courtName;
        this.courtImage = courtImage;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCourtId() { return courtId; }
    public void setCourtId(int courtId) { this.courtId = courtId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getCourtImage() { return courtImage; }
    public void setCourtImage(String courtImage) { this.courtImage = courtImage; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public int getIsDiscounted() { return isDiscounted; }
    public void setIsDiscounted(int isDiscounted) { this.isDiscounted = isDiscounted; }
    private Court courtObject;

    // Getter & Setter
    public Court getCourtObject() {
        return courtObject;
    }

    public void setCourtObject(Court courtObject) {
        this.courtObject = courtObject;
    }

}