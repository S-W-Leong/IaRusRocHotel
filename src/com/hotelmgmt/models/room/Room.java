package com.hotelmgmt.models.room;

import java.io.Serializable;
import java.math.BigDecimal;

public class Room implements Serializable {
    private String roomNumber;
    private RoomType type;
    private RoomStatus status;
    private BigDecimal basePrice;
    private int floor;
    private boolean needsCleaning;
    private String description;

    public Room(String roomNumber, RoomType type, BigDecimal basePrice, int floor) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = RoomStatus.AVAILABLE;
        this.basePrice = basePrice;
        this.floor = floor;
        this.needsCleaning = false;
        this.description = type != null ? type.getDescription() : ""; // Add null check
    }

    // Getters and Setters
    public String getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public int getFloor() { return floor; }
    public boolean isNeedsCleaning() { return needsCleaning; }
    public void setNeedsCleaning(boolean needsCleaning) { this.needsCleaning = needsCleaning; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE && !needsCleaning;
    }

    @Override
    public String toString() {
        return String.format("Room{number='%s', type=%s, status=%s, price=%s, floor=%d, needsCleaning=%b}",
                roomNumber, type, status, basePrice, floor, needsCleaning);
    }
}