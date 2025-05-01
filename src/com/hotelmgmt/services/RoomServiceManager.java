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

    // Food Ordering Methods
    public void orderFood(Guest guest) {
        System.out.println("\n--- Food Menu ---");
        
        // Get all menu items
        List<MenuItem> allMenuItems = MenuItem.initializeMenuItems();
        
        // Display food categories
        System.out.println("\nFood Categories:");
        System.out.println("1. Breakfast");
        System.out.println("2. Lunch");
        System.out.println("3. Dinner");
        System.out.println("4. Snacks");
        System.out.println("5. Beverages");
        System.out.println("6. Desserts");
        System.out.println("0. Cancel");
        
        System.out.print("\nSelect category: ");
        String categoryChoice = scanner.nextLine();
        
        if (categoryChoice.equals("0")) {
            return;
        }
        
        // Get menu items for selected category
        MenuCategory selectedCategory;
        switch (categoryChoice) {
            case "1":
                selectedCategory = MenuCategory.BREAKFAST;
                break;
            case "2":
                selectedCategory = MenuCategory.LUNCH;
                break;
            case "3":
                selectedCategory = MenuCategory.DINNER;
                break;
            case "4":
                selectedCategory = MenuCategory.SNACKS;
                break;
            case "5":
                selectedCategory = MenuCategory.BEVERAGES;
                break;
            case "6":
                selectedCategory = MenuCategory.DESSERTS;
                break;
            default:
                System.out.println("Invalid category selection.");
                return;
        }
        
        List<MenuItem> categoryItems = MenuItem.getMenuItemsByCategory(allMenuItems, selectedCategory);
        
        // Display items
        System.out.println("\nAvailable Items:");
        for (MenuItem item : categoryItems) {
            System.out.printf("%s. %s - $%.2f\n", item.getId(), item.getName(), item.getPrice());
        }
        
        System.out.print("\nSelect item (0 to cancel): ");
        String itemChoice = scanner.nextLine();
        
        if (itemChoice.equals("0")) {
            return;
        }
        
        // Find selected item
        MenuItem selectedItem = categoryItems.stream()
            .filter(item -> item.getId().equals(itemChoice))
            .findFirst()
            .orElse(null);
            
        if (selectedItem == null) {
            System.out.println("Invalid item selection.");
            return;
        }
        
        // Get room number
        System.out.print("\nEnter your room number: ");
        String roomNumber = scanner.nextLine();
        
        // Find the room
        Room room = rooms.stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElse(null);
            
        if (room == null) {
            System.out.println("Invalid room number.");
            return;
        }
        
        // Get special instructions
        System.out.print("Special instructions (press Enter for none): ");
        String instructions = scanner.nextLine();
        
        // Create order
        List<MenuItem> orderItems = new ArrayList<>();
        orderItems.add(selectedItem);
        
        RoomServiceOrder order = new RoomServiceOrder(
            guest,
            room,
            orderItems,
            instructions
        );
        
        roomServiceOrders.add(order);
        System.out.println("\nOrder placed successfully!");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Status: " + order.getStatus());
    }

    public void requestCleaning(Guest guest) {
        System.out.println("\n--- Cleaning Service ---");
        
        // Get room number
        System.out.print("Enter your room number: ");
        String roomNumber = scanner.nextLine();
        
        // Find the room
        Room room = rooms.stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElse(null);
            
        if (room == null) {
            System.out.println("Invalid room number.");
            return;
        }
        
        // Get cleaning type
        System.out.println("\nCleaning Types:");
        System.out.println("1. Regular Cleaning");
        System.out.println("2. Deep Cleaning");
        System.out.println("3. Towel Change");
        System.out.println("4. Bed Sheet Change");
        System.out.println("5. Mini Bar Restock");
        System.out.println("6. Bathroom Refresh");
        System.out.println("7. Window Cleaning");
        System.out.println("8. Carpet Cleaning");
        System.out.print("\nSelect cleaning type: ");
        String cleaningType = scanner.nextLine();
        
        String cleaningDescription;
        switch (cleaningType) {
            case "1":
                cleaningDescription = "Regular Cleaning";
                break;
            case "2":
                cleaningDescription = "Deep Cleaning";
                break;
            case "3":
                cleaningDescription = "Towel Change";
                break;
            case "4":
                cleaningDescription = "Bed Sheet Change";
                break;
            case "5":
                cleaningDescription = "Mini Bar Restock";
                break;
            case "6":
                cleaningDescription = "Bathroom Refresh";
                break;
            case "7":
                cleaningDescription = "Window Cleaning";
                break;
            case "8":
                cleaningDescription = "Carpet Cleaning";
                break;
            default:
                System.out.println("Invalid cleaning type.");
                return;
        }
        
        // Get special instructions
        System.out.print("Special instructions (press Enter for none): ");
        String instructions = scanner.nextLine();
        
        // Create cleaning request
        List<MenuItem> cleaningItems = new ArrayList<>();
        cleaningItems.add(new MenuItem("CLEAN", cleaningDescription, "", new BigDecimal("0.00"), MenuCategory.SNACKS));
        
        RoomServiceOrder order = new RoomServiceOrder(
            guest,
            room,
            cleaningItems,
            instructions
        );
        
        roomServiceOrders.add(order);
        System.out.println("\nCleaning request submitted successfully!");
        System.out.println("Request ID: " + order.getId());
        System.out.println("Status: " + order.getStatus());
    }
}   