package com.hotelmgmt.services;

import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomStatus;
import com.hotelmgmt.models.roomService.MenuCategory;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.roomService.RoomServiceStatus;
import com.hotelmgmt.models.user.Guest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// RoomServiceManager is a class that manages the room service orders in the hotel. It allows the user to place orders, update the status of an order, and get all orders for a guest or room.
public class RoomServiceManager {
    private List<Room> rooms;
    private List<RoomServiceOrder> roomServiceOrders;
    private Scanner scanner;

    public RoomServiceManager(List<Room> rooms) {
        this.rooms = rooms;
        this.roomServiceOrders = new ArrayList<>();
        this.scanner = new Scanner(System.in);
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

    // Room Service Management Methods
    public RoomServiceOrder placeOrder(Guest guest, Room room, List<MenuItem> items, String specialInstructions) {
        RoomServiceOrder order = new RoomServiceOrder(guest, room, items, specialInstructions);
        roomServiceOrders.add(order);
        return order;
    }

    public List<RoomServiceOrder> getOrdersByRoom(String roomNumber) {
        return roomServiceOrders.stream()
            .filter(order -> order.getRoom().getRoomNumber().equals(roomNumber))
            .toList();
    }

    public List<RoomServiceOrder> getOrdersByStatus(RoomServiceStatus status) {
        return roomServiceOrders.stream()
            .filter(order -> order.getStatus() == status)
            .toList();
    }

    public boolean updateOrderStatus(String orderId, RoomServiceStatus newStatus) {
        RoomServiceOrder order = roomServiceOrders.stream()
            .filter(o -> o.getId().equals(orderId))
            .findFirst()
            .orElse(null);

        if (order != null) {
            order.setStatus(newStatus);
            return true;
        }
        return false;
    }

    public List<RoomServiceOrder> getAllOrders() {
        return new ArrayList<>(roomServiceOrders);
    }

    public RoomServiceOrder getOrderById(String orderId) {
        return roomServiceOrders.stream()
            .filter(order -> order.getId().equals(orderId))
            .findFirst()
            .orElse(null);
    }

    public List<RoomServiceOrder> getOrdersByGuest(Guest guest) {
        return roomServiceOrders.stream()
            .filter(order -> order.getGuest().equals(guest))
            .toList();
    }

    public List<RoomServiceOrder> getOrdersForRoom(Room room) {
        List<RoomServiceOrder> result = new ArrayList<>();
        for (RoomServiceOrder order : roomServiceOrders) {
            if (order.getRoom().equals(room)) {
                result.add(order);
            }
        }
        return result;
    }
}   