package com.hotelmgmt.models.roomService;

import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Guest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

public class RoomServiceOrder implements Serializable {
    private String id;
    private Guest guest;
    private Room room;
    private List<MenuItem> items;
    private String specialInstructions;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;

    public RoomServiceOrder(Guest guest, Room room, List<MenuItem> items, String specialInstructions) {
        this.id = UUID.randomUUID().toString();
        this.guest = guest;
        this.room = room;
        this.items = new ArrayList<>(items);
        this.specialInstructions = specialInstructions;
        this.orderTime = LocalDateTime.now();
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        this.totalAmount = items.stream()
            .map(MenuItem::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public String getId() { return id; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public List<MenuItem> getItems() { return new ArrayList<>(items); }
    public void addItem(MenuItem item) {
        items.add(item);
        calculateTotalAmount();
    }
    public void removeItem(MenuItem item) {
        items.remove(item);
        calculateTotalAmount();
    }
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { 
        this.specialInstructions = specialInstructions; 
    }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getOrderTime() { return orderTime; }

    @Override
    public String toString() {
        return String.format("RoomServiceOrder{id='%s', guest='%s', room='%s', items=%d, amount=%s}",
                id, guest.getUsername(), room.getRoomNumber(), items.size(), totalAmount);
    }
} 