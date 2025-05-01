package com.hotelmgmt.services;

import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomStatus;
import com.hotelmgmt.models.room.RoomType;
import com.hotelmgmt.utils.ConsoleUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

// RoomManager is a class that manages the rooms in the hotel. It allows the user to view all rooms, add new rooms, and update the status of a room.
public class RoomManager {
    private List<Room> rooms;
    private Scanner scanner;

    public RoomManager(List<Room> rooms) {
        this.rooms = rooms;
        this.scanner = new Scanner(System.in);
    }

    public void manageRooms() {
        ConsoleUtils.clearScreen();
        System.out.println("\n--- Room Management ---");
        System.out.println("1. View All Rooms");
        System.out.println("2. Add New Room");
        System.out.println("3. Update Room Status");
        System.out.println("4. Back to Main Menu");
        System.out.print("\nEnter your choice: ");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                viewAllRooms();
                break;
            case "2":
                addNewRoom();
                break;
            case "3":
                updateRoomStatus();
                break;
            case "4":
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
                ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void viewAllRooms() {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔════════════════════════════════════ Room List ══════════════════════════════════════════╗");
        System.out.println("║  Room No.  │      Type      │         Status        │  Floor  │ Price (RM) │  Cleaning  ║");
        System.out.println("╠════════════╪════════════════╪═══════════════════════╪═════════╪════════════╪════════════╣");
        
        for (Room room : rooms) {
            System.out.printf("║   %-8s │  %-12s  │   %-17s   │    %-3d  │   %-6.2f   │     %-3s    ║\n",
                room.getRoomNumber(),
                room.getType().toString().replace("_SUITE", ""),
                room.getStatus(),
                room.getFloor(),
                room.getBasePrice(),
                room.isNeedsCleaning() ? "YES" : "NO");
        }
        
        System.out.println("╚════════════╧════════════════╧═══════════════════════╧═════════╧════════════╧════════════╝");
        System.out.println("\nTotal Rooms: " + rooms.size());
        ConsoleUtils.waitForEnter(scanner);
    }

    private void addNewRoom() {
        ConsoleUtils.clearScreen();
        System.out.println("\n                             Add New Room        ");
        System.out.println("                    ═════════════════════════════");
        displayRoomRequirements();

        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        
        // Check if room number already exists
        boolean roomExists = rooms.stream()
            .anyMatch(room -> room.getRoomNumber().equals(roomNumber));
            
        if (roomExists) {
            System.out.println("\nError: Room number " + roomNumber + " is already in use. Please choose another.");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }
        
        System.out.print("Enter room type: ");
        String roomType = scanner.nextLine();
        
        System.out.print("Enter base price: ");
        String priceStr = scanner.nextLine();
        BigDecimal price = new BigDecimal(priceStr);
        
        System.out.print("Enter floor number: ");
        int floor = Integer.parseInt(scanner.nextLine());
        
        Room newRoom = new Room(roomNumber, RoomType.valueOf(roomType.toUpperCase()), price, floor);
        rooms.add(newRoom);
        
        System.out.println("\nRoom " + roomNumber + " has been added successfully!");
        ConsoleUtils.waitForEnter(scanner);
    }

    public static void displayRoomRequirements() {
        System.out.println("\n════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                           ROOM NUMBER FORMAT                              ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════");
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ Room Number Format:                                                          ║");
        System.out.println("║ - Format: TTFRR (TT=Type, F=Floor, RR=Room)                                  ║");
        System.out.println("║ - Example: SD101 (Type=SD, Floor=1, Room=01)                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Room Type Codes:                                                             ║");
        System.out.println("║ - STANDARD Room = SD                                                         ║");
        System.out.println("║ - DELUXE Room = DX                                                           ║");
        System.out.println("║ - SUITE Room = ST                                                            ║");
        System.out.println("║ - EXECUTIVE SUITE Room = ET                                                  ║");
        System.out.println("║ - PRESIDENTIAL SUITE Room = PE                                               ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Floor Numbers:                                                               ║");
        System.out.println("║ - Floors 1-5 available for all room types                                    ║");
        System.out.println("║ - Each floor can have multiple room types                                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
    }

    private void updateRoomStatus() {
        ConsoleUtils.clearScreen();
        System.out.println("\n                             Update Room Status        ");
        System.out.println("                       ═══════════════════════════════");
        
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        
        Room room = rooms.stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElse(null);
            
        if (room == null) {
            System.out.println("\nError: Room not found!");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }
        
        // Display only the searched room's information
        System.out.println("\n╔═════════════════════════════════ Room Information ══════════════════════════════════════╗");
        System.out.println("║  Room No.  │      Type      │         Status        │  Floor  │ Price (RM) │  Cleaning  ║");
        System.out.println("╠════════════╪════════════════╪═══════════════════════╪═════════╪════════════╪════════════╣");
        System.out.printf("║   %-8s │  %-12s  │   %-17s   │    %-3d  │   %-6.2f   │     %-3s    ║\n",
            room.getRoomNumber(),
            room.getType().toString().replace("_SUITE", ""),
            room.getStatus(),
            room.getFloor(),
            room.getBasePrice(),
            room.isNeedsCleaning() ? "YES" : "NO");
        System.out.println("╚════════════╧════════════════╧═══════════════════════╧═════════╧════════════╧════════════╝");
        
        System.out.println("\nCurrent Status: " + room.getStatus());
        System.out.println("\nAvailable Statuses:");
        for (RoomStatus status : RoomStatus.values()) {
            System.out.println("- " + status);
        }
        
        System.out.print("\nEnter new status: ");
        String newStatus = scanner.nextLine();
        
        try {
            room.setStatus(RoomStatus.valueOf(newStatus.toUpperCase()));
            System.out.println("\nRoom status updated successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: Invalid status!");
        }
        
        ConsoleUtils.waitForEnter(scanner);
    }

    public List<Room> getRooms() {
        return rooms;
    }
} 