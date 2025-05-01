package com.hotelmgmt.models.reservation;

import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Guest;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation implements Serializable {
    private String id;
    private Guest guest;
    private Room room;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private ReservationStatus status;
    private BigDecimal totalAmount;
    private String specialRequests;

    public Reservation(Guest guest, Room room, LocalDateTime checkInDate, 
                      LocalDateTime checkOutDate, String specialRequests) {
        this.id = UUID.randomUUID().toString();
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.PENDING;
        this.specialRequests = specialRequests;
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        long days = java.time.Duration.between(checkInDate, checkOutDate).toDays();
        this.totalAmount = room.getBasePrice().multiply(BigDecimal.valueOf(days));
    }

    // Getters and Setters
    public String getId() { return id; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public LocalDateTime getCheckInDate() { return checkInDate; }
    public LocalDateTime getCheckOutDate() { return checkOutDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    @Override
    public String toString() {
        return String.format("Reservation{id='%s', guest='%s', room='%s', checkIn='%s', checkOut='%s', status=%s}",
                id, guest.getUsername(), room.getRoomNumber(), checkInDate, checkOutDate, status);
    }
} 