package com.hotelmgmt.models.room;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RoomData {
    private static final List<Room> rooms = new ArrayList<>();

    static {
        // Standard Rooms (20 rooms)
        for (int i = 101; i <= 120; i++) {
            String roomNumber = String.format("SD%03d", i);
            Room room = new Room(roomNumber, RoomType.STANDARD, new BigDecimal("200"), 1);
            // Set some standard rooms as occupied and under maintenance
            if (i <= 5) {
                room.setStatus(RoomStatus.OCCUPIED);
            } else if (i <= 7) {
                room.setStatus(RoomStatus.UNDER_MAINTENANCE);
            } else if (i <= 10) {
                room.setStatus(RoomStatus.RESERVED);
            }
            rooms.add(room);
        }

        // Deluxe Rooms (15 rooms)
        for (int i = 201; i <= 215; i++) {
            String roomNumber = String.format("DX%03d", i);
            Room room = new Room(roomNumber, RoomType.DELUXE, new BigDecimal("350"), 2);
            // Set some deluxe rooms as occupied and reserved
            if (i <= 3) {
                room.setStatus(RoomStatus.OCCUPIED);
            } else if (i <= 6) {
                room.setStatus(RoomStatus.RESERVED);
            }
            rooms.add(room);
        }

        // Suite Rooms (10 rooms)
        for (int i = 301; i <= 310; i++) {
            String roomNumber = String.format("ST%03d", i);
            Room room = new Room(roomNumber, RoomType.SUITE, new BigDecimal("400"), 3);
            // Set some suite rooms as occupied and under maintenance
            if (i <= 2) {
                room.setStatus(RoomStatus.OCCUPIED);
            } else if (i <= 4) {
                room.setStatus(RoomStatus.UNDER_MAINTENANCE);
            }
            rooms.add(room);
        }

        // Executive Rooms (5 rooms)
        for (int i = 401; i <= 405; i++) {
            String roomNumber = String.format("ET%03d", i);
            Room room = new Room(roomNumber, RoomType.EXECUTIVE_SUITE, new BigDecimal("500"), 4);
            // Set some executive rooms as occupied
            if (i <= 2) {
                room.setStatus(RoomStatus.OCCUPIED);
            }
            rooms.add(room);
        }

        // Presidential Room (1 room)
        Room presidentialRoom = new Room("PE501", RoomType.PRESIDENTIAL_SUITE, new BigDecimal("800"), 5);
        presidentialRoom.setStatus(RoomStatus.AVAILABLE);
        rooms.add(presidentialRoom);
    }

    public static List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }
} 