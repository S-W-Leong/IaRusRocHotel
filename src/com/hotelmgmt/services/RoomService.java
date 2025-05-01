package com.hotelmgmt.services;   

import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomStatus;
import java.util.List;
import com.hotelmgmt.models.reservation.Reservation;
import java.time.LocalDateTime;

// RoomService serves to find available rooms for a guest during the reservation process
public class RoomService {
    private List<Room> rooms;

    public RoomService(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Room findAvailableRoom(String roomType, LocalDateTime checkIn, LocalDateTime checkOut, List<Reservation> reservations) {
        for (Room room : rooms) {
            if (room.getType().toString().equalsIgnoreCase(roomType) && room.getStatus() == RoomStatus.AVAILABLE) {
                boolean isAvailable = true;
                for (Reservation reservation : reservations) {
                    if (reservation.getRoom().equals(room) && reservation.getStatus() != com.hotelmgmt.models.reservation.ReservationStatus.CANCELLED) {
                        // Check for date overlap
                        if (!(checkOut.isBefore(reservation.getCheckInDate()) || checkIn.isAfter(reservation.getCheckOutDate()))) {
                            isAvailable = false;
                            break;
                        }
                    }
                }
                if (isAvailable) {
                    return room;
                }
            }
        }
        return null;
    }

    public boolean assignRoom(Room room) {
        if (room.getStatus() == RoomStatus.AVAILABLE) {
            room.setStatus(RoomStatus.OCCUPIED);
            return true;
        }
        return false;
    }

    public void updateRoomStatus(Room room, RoomStatus status) {
        room.setStatus(status);
    }

    public List<Room> getRooms() {
        return rooms;
    }
} 