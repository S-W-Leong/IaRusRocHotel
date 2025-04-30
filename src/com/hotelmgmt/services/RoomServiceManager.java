package com.hotelmgmt.services;

import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceStatus;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.room.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomServiceManager {
    private List<RoomServiceOrder> orders;

    public RoomServiceManager() {
        this.orders = new ArrayList<>();
    }

    public RoomServiceOrder placeOrder(Guest guest, Room room, List<MenuItem> items, String specialInstructions) {
        RoomServiceOrder order = new RoomServiceOrder(guest, room, items, specialInstructions);
        orders.add(order);
        return order;
    }

    public boolean updateOrderStatus(RoomServiceOrder order, RoomServiceStatus status) {
        if (orders.contains(order)) {
            order.setStatus(status);
            return true;
        }
        return false;
    }

    public List<RoomServiceOrder> getOrdersForGuest(Guest guest) {
        List<RoomServiceOrder> result = new ArrayList<>();
        for (RoomServiceOrder order : orders) {
            if (order.getGuest().equals(guest)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<RoomServiceOrder> getOrdersForRoom(Room room) {
        List<RoomServiceOrder> result = new ArrayList<>();
        for (RoomServiceOrder order : orders) {
            if (order.getRoom().equals(room)) {
                result.add(order);
            }
        }
        return result;
    }

    public List<RoomServiceOrder> getAllOrders() {
        return new ArrayList<>(orders);
    }
} 