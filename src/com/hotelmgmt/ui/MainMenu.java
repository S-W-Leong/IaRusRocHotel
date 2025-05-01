package com.hotelmgmt.ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.hotelmgmt.utils.ConsoleUtils;
import com.hotelmgmt.models.user.User;
import com.hotelmgmt.models.user.UserRole;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomType;
import com.hotelmgmt.models.roomService.MenuCategory;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.roomService.RoomServiceStatus;
import com.hotelmgmt.models.room.RoomStatus;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.reservation.ReservationStatus;
import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.PaymentStatus;
import com.hotelmgmt.services.AuthenticationService;
import com.hotelmgmt.services.BookingManager;
import com.hotelmgmt.services.RoomServiceManager;
import com.hotelmgmt.services.PaymentService;
import com.hotelmgmt.services.ReservationService;
import com.hotelmgmt.services.RoomService;
import com.hotelmgmt.services.HousekeepingService;

public class MainMenu {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private User currentUser;
    private final List<Room> rooms = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<RoomServiceOrder> roomServices = new ArrayList<>();
    private final List<MenuItem> menuItems = new ArrayList<>();
    private final List<Invoice> invoices = new ArrayList<>();
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final RoomServiceManager roomServiceManager;
    private final PaymentService paymentService;
    private final BookingManager bookingManager;
    private final HousekeepingService housekeepingService;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
        this.authService = new AuthenticationService();
        seedRooms();
        seedMenuItems();
        this.roomService = new RoomService(rooms);
        this.reservationService = new ReservationService();
        this.roomServiceManager = new RoomServiceManager();
        this.paymentService = new PaymentService();
        this.bookingManager = new BookingManager(roomService, reservationService, paymentService, roomServiceManager);
        this.housekeepingService = new HousekeepingService();
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        while (currentUser == null) {
            ConsoleUtils.clearScreen();
            System.out.println("\nLogin Menu");
            System.out.println("1. Login");
            System.out.println("2. Register as Guest");
            System.out.println("3. Exit");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.out.println("\nThank you for using Hotel Management System!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void showMainMenu() {
        while (currentUser != null) {
            ConsoleUtils.clearScreen();
            System.out.println("\nMain Menu - Welcome " + currentUser.getFullName());
            System.out.println("1. View Profile");
            
            if (currentUser.getRole() == UserRole.GUEST) {
                System.out.println("2. Room Booking");
                System.out.println("3. My Reservations");
                System.out.println("4. Room Service");
                System.out.println("5. View Bills");
            } else if (currentUser.getRole() == UserRole.MANAGER) {
                System.out.println("2. Room Management");
                System.out.println("3. Housekeeping Management");
                System.out.println("4. Reports");
                System.out.println("5. System Settings");
            } else {
                System.out.println("2. Room Status");
                System.out.println("3. Tasks");
                System.out.println("4. Reports");
            }

            System.out.println("0. Logout");
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();
            handleMainMenuChoice(choice);
        }
    }

    private void login() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine(); 
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            currentUser = authService.authenticate(username, password);
            System.out.println("\nLogin successful!");
            ConsoleUtils.waitForEnter(scanner);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void register() {
        System.out.println("\nGuest Registration");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        try {
            currentUser = authService.registerGuest(username, password, firstName, lastName, email, phone);
            System.out.println("\nRegistration successful!");
            ConsoleUtils.waitForEnter(scanner);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void handleMainMenuChoice(String choice) {
        if (currentUser.getRole() == UserRole.GUEST) {
            switch (choice) {
                case "0":
                    logout();
                    break;
                case "1":
                    viewProfile();
                    break;
                case "2":
                    bookRoom();
                    break;
                case "3":
                    viewMyReservations();
                    break;
                case "4":
                    orderRoomService();
                    break;
                case "5":
                    viewBills();
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        } else if (currentUser.getRole() == UserRole.MANAGER) {
            switch (choice) {
                case "0":
                    logout();
                    break;
                case "1":
                    viewProfile();
                    break;
                case "2":
                //zihao
                    break;
                    
                case "3":
                new HousekeepingMenu(housekeepingService).showMenu();
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        } else {
            System.out.println("\nAccess denied. Invalid role.");
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("\nLogout successful!");
        ConsoleUtils.waitForEnter(scanner);
    }

    // Seed some rooms
    private void seedRooms() {
        rooms.add(new Room("101", RoomType.STANDARD, new BigDecimal("100"), 1));
        rooms.add(new Room("102", RoomType.DELUXE, new BigDecimal("150"), 1));
        rooms.add(new Room("201", RoomType.SUITE, new BigDecimal("250"), 2));
    }

    // Seed some menu items
    private void seedMenuItems() {
        menuItems.add(new MenuItem("1", "Club Sandwich", "Grilled chicken sandwich", new BigDecimal("12.50"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("2", "Pancakes", "Stack of pancakes with syrup", new BigDecimal("8.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("3", "Coffee", "Freshly brewed coffee", new BigDecimal("3.00"), MenuCategory.BEVERAGES));
    }

    private void viewProfile() {
        System.out.println("\n--- Profile ---");
        System.out.println(currentUser);
        ConsoleUtils.waitForEnter(scanner);
    }

    private void bookRoom() {
        System.out.println("\n--- Available Room Types ---");
        for (RoomType type : RoomType.values()) {
            System.out.println("- " + type);
        }
        System.out.print("Enter room type: ");
        String roomType = scanner.nextLine();
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        String checkInStr = scanner.nextLine();
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        String checkOutStr = scanner.nextLine();
        try {
            java.time.LocalDate checkIn = java.time.LocalDate.parse(checkInStr);
            java.time.LocalDate checkOut = java.time.LocalDate.parse(checkOutStr);
            Reservation reservation = bookingManager.bookRoom((Guest) currentUser, roomType, checkIn, checkOut);
            if (reservation != null) {
                System.out.println("Room booked! Reservation ID: " + reservation.getId());
            } else {
                System.out.println("No available room for the selected type and dates.");
            }
        } catch (Exception e) {
            System.out.println("Invalid dates or error booking room.");
        }
        ConsoleUtils.waitForEnter(scanner);
    }

    private void viewMyReservations() {
        System.out.println("\n--- My Reservations ---");
        boolean found = false;
        for (Reservation r : reservations) {
            if (r.getGuest().getUsername().equals(currentUser.getUsername())) {
                System.out.println(r);
                found = true;
            }
        }
        if (!found) System.out.println("No reservations found.");
        ConsoleUtils.waitForEnter(scanner);
    }

    private void orderRoomService() {
        System.out.println("\n--- Room Service Menu ---");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.println((i+1) + ". " + menuItems.get(i));
        }
        System.out.print("Select menu item (number): ");
        String input = scanner.nextLine();
        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= menuItems.size()) throw new Exception();
        } catch (Exception e) {
            System.out.println("Invalid selection.");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }
        MenuItem selectedItem = menuItems.get(idx);
        // Find guest's latest reservation
        Reservation latest = null;
        for (Reservation r : reservationService.getAllReservations()) {
            if (r.getGuest().getUsername().equals(currentUser.getUsername())) {
                latest = r;
            }
        }
        if (latest == null) {
            System.out.println("You must have a reservation to order room service.");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }
        List<MenuItem> orderItems = new ArrayList<>();
        orderItems.add(selectedItem);
        RoomServiceOrder order = roomServiceManager.placeOrder((Guest)currentUser, latest.getRoom(), orderItems, "");
        System.out.println("Room service order placed! Order ID: " + order.getId());
        ConsoleUtils.waitForEnter(scanner);
    }

    private void viewBills() {
        System.out.println("\n--- My Bills ---");
        boolean found = false;
        for (Invoice inv : paymentService.getAllInvoices()) {
            if (inv.getReservation().getGuest().getUsername().equals(currentUser.getUsername())) {
                System.out.println(inv);
                found = true;
            }
        }
        if (!found) System.out.println("No bills found.");
        ConsoleUtils.waitForEnter(scanner);
    }
}