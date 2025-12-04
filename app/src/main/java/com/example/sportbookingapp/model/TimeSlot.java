package com.example.sportbookingapp.model;

public class TimeSlot {
    private String timeLabel;   // Tên hiển thị: "08:00 - 09:00"
    private String startTime;   // "08:00" (Lưu DB)
    private String endTime;     // "09:00" (Lưu DB)
    private boolean isBooked;   // Đã có người đặt chưa?
    private boolean isSelected; // Người dùng đang chọn ô này?

    public TimeSlot(String startTime, String endTime, boolean isBooked) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeLabel = startTime + " - " + endTime;
        this.isBooked = isBooked;
        this.isSelected = false;
    }

    // Getter cần thiết để sửa lỗi Cannot resolve method 'getTimeLabel'
    public String getTimeLabel() { return timeLabel; }

    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}