package com.hotelmgmt.ui;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.reservation.ReservationStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomData;
import com.hotelmgmt.models.room.RoomType;
import com.hotelmgmt.models.roomService.MenuCategory;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.user.User;
import com.hotelmgmt.models.user.UserRole;
import com.hotelmgmt.services.AuthenticationService;
import com.hotelmgmt.services.BookingManager;
import com.hotelmgmt.services.HousekeepingService;
import com.hotelmgmt.services.PaymentService;
import com.hotelmgmt.services.RegisterRequirement;
import com.hotelmgmt.services.Report;
import com.hotelmgmt.services.ReservationService;
import com.hotelmgmt.services.RoomManager;
import com.hotelmgmt.services.RoomService;
import com.hotelmgmt.services.RoomServiceManager;
import com.hotelmgmt.services.ViewProfile;
import com.hotelmgmt.utils.ConsoleUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private User currentUser;
    private final List<Room> rooms;
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
    private final RoomManager roomManager;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
        this.authService = new AuthenticationService();
        this.rooms = RoomData.getRooms();
        seedMenuItems();
        this.roomService = new RoomService(rooms);
        this.reservationService = new ReservationService();
        this.roomServiceManager = new RoomServiceManager(rooms);
        this.paymentService = new PaymentService();
        this.bookingManager = new BookingManager(roomService, reservationService, paymentService, roomServiceManager);
        this.housekeepingService = new HousekeepingService();
        this.roomManager = new RoomManager(rooms);
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
            Logo.display();
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
            Logo.display();
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
        ConsoleUtils.clearScreen();
        RegisterRequirement.displayRequirements();
        System.out.println("\nPress Enter to continue with registration...");
        scanner.nextLine();
        
        System.out.println("\nGuest Registration");
        
        String username;
        do {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (!RegisterRequirement.isValidUsername(username)) {
                System.out.println("Invalid username! Only letters, numbers, and underscore are allowed.");
            }
        } while (!RegisterRequirement.isValidUsername(username));

        String password;
        do {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (!RegisterRequirement.isValidPassword(password)) {
                System.out.println("Invalid password! Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.");
            }
        } while (!RegisterRequirement.isValidPassword(password));

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        String email;
        do {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
            if (!RegisterRequirement.isValidEmail(email)) {
                System.out.println("Invalid email format! Please enter a valid email address.");
            }
        } while (!RegisterRequirement.isValidEmail(email));

        String phone;
        do {
            System.out.print("Enter phone number: ");
            phone = scanner.nextLine();
            if (!RegisterRequirement.isValidPhone(phone)) {
                System.out.println("Invalid phone number format! Please enter a valid phone number.");
            }
        } while (!RegisterRequirement.isValidPhone(phone));

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
                    roomManager.manageRooms();
                    break;
                case "3":
                    new HousekeepingMenu(housekeepingService, roomManager).showMenu();
                    break;
                case "4":
                    showReportMenu();
                    break;
                case "5":
                    // System Settings
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        } else {
            switch (choice) {
                case "0":
                    logout();
                    break;
                case "1":
                    viewProfile();
                    break;
                case "2":
                    // Room Status
                    break;
                case "3":
                    // Tasks
                    break;
                case "4":
                    showReportMenu();
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("\nLogout successful!");
        ConsoleUtils.waitForEnter(scanner);
    }

    private void seedMenuItems() {
        menuItems.add(new MenuItem("1", "Club Sandwich", "Grilled chicken sandwich", new BigDecimal("12.50"), MenuCategory.LUNCH));
        menuItems.add(new MenuItem("2", "Pancakes", "Stack of pancakes with syrup", new BigDecimal("8.00"), MenuCategory.BREAKFAST));
        menuItems.add(new MenuItem("3", "Coffee", "Freshly brewed coffee", new BigDecimal("3.00"), MenuCategory.BEVERAGES));
    }

    private void viewProfile() {
        new ViewProfile(scanner).displayProfile(currentUser);
    }

    private void bookRoom() {
        // Gather room type info
        RoomType[] types = RoomType.values();
        List<Room> allRooms = roomService.getRooms();
        System.out.println("\n--- Available Room Types ---");
        System.out.printf("%-3s %-20s %-60s %-10s %-15s\n", "No", "Type", "Description", "Price (RM)", "Available");
        for (int i = 0; i < types.length; i++) {
            RoomType type = types[i];
            // Find all rooms of this type
            List<Room> roomsOfType = new ArrayList<>();
            for (Room r : allRooms) {
                if (r.getType() == type) roomsOfType.add(r);
            }
            // Find minimum price for this type
            BigDecimal minPrice = roomsOfType.stream().map(Room::getBasePrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
            // Count available rooms (status AVAILABLE and not needsCleaning)
            long availableCount = roomsOfType.stream().filter(Room::isAvailable).count();
            System.out.printf("%-3d %-20s %-60s %-10s %-15d\n", i+1, type, type.getDescription(), minPrice, availableCount);
        }
        int selectedIdx = -1;
        while (selectedIdx < 0 || selectedIdx >= types.length) {
            System.out.print("\nSelect room type (1-" + types.length + "): ");
            String input = scanner.nextLine();
            try {
                selectedIdx = Integer.parseInt(input) - 1;
                if (selectedIdx < 0 || selectedIdx >= types.length) throw new Exception();
            } catch (Exception e) {
                System.out.println("Invalid selection. Please enter a number between 1 and " + types.length + ".");
            }
        }
        RoomType selectedType = types[selectedIdx];
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        String checkInStr = scanner.nextLine();
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        String checkOutStr = scanner.nextLine();
        try {
            java.time.LocalDate checkIn = java.time.LocalDate.parse(checkInStr);
            java.time.LocalDate checkOut = java.time.LocalDate.parse(checkOutStr);
            Reservation reservation = bookingManager.bookRoom((Guest) currentUser, selectedType.toString(), checkIn, checkOut);
            if (reservation != null) {
                // Generate and display invoice
                Invoice invoice = paymentService.createInvoice(reservation);
                boolean paid = false;
                do {
                    System.out.println("\n--- Invoice ---");
                    System.out.println("Reservation ID: " + reservation.getId());
                    System.out.println("Room: " + reservation.getRoom().getRoomNumber() + " (" + reservation.getRoom().getType() + ")");
                    System.out.println("Check-in: " + reservation.getCheckInDate().toLocalDate());
                    System.out.println("Check-out: " + reservation.getCheckOutDate().toLocalDate());
                    System.out.println("Total Amount: RM" + String.format("%.2f", invoice.getTotalAmount()));
                    System.out.println("Amount Paid: RM" + String.format("%.2f", invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add)));
                    System.out.println("Outstanding: RM" + String.format("%.2f", invoice.getTotalAmount().subtract(invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add))));
                    System.out.println("Payment Status: " + invoice.getPaymentStatus());
                    // Prompt for payment
                    System.out.print("\nEnter payment amount (RM): ");
                    String amountStr = scanner.nextLine();
                    BigDecimal amount = new BigDecimal(amountStr);
                    // Show payment methods as numbered list
                    PaymentMethod[] methods = PaymentMethod.values();
                    System.out.println("Select payment method:");
                    for (int i = 0; i < methods.length; i++) {
                        System.out.println((i+1) + ". " + methods[i]);
                    }
                    int methodIdx = -1;
                    while (methodIdx < 0 || methodIdx >= methods.length) {
                        System.out.print("Enter payment method (1-" + methods.length + "): ");
                        String methodInput = scanner.nextLine();
                        try {
                            methodIdx = Integer.parseInt(methodInput) - 1;
                            if (methodIdx < 0 || methodIdx >= methods.length) throw new Exception();
                        } catch (Exception e) {
                            System.out.println("Invalid selection. Please enter a number between 1 and " + methods.length + ".");
                        }
                    }
                    PaymentMethod paymentMethod = methods[methodIdx];
                    System.out.print("Enter transaction reference: ");
                    String transactionRef = scanner.nextLine();
                    System.out.print("Enter notes (optional): ");
                    String notes = scanner.nextLine();
                    paid = paymentService.processPayment(invoice, amount, paymentMethod, transactionRef, notes);
                    if (paid) {
                        reservation.setStatus(ReservationStatus.CONFIRMED);
                        System.out.println("\nPayment successful! Reservation is CONFIRMED.");
                    } else {
                        reservation.setStatus(ReservationStatus.PENDING);
                        System.out.println("\nPartial payment received. Reservation is PENDING until full payment is made.");
                    }
                    System.out.println("Invoice Payment Status: " + invoice.getPaymentStatus());
                } while (!paid);
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
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔════════════════════════════════ My Reservations ═══════════════════════════════╗");
            List<Reservation> myReservations = reservationService.getReservationsForGuest((Guest) currentUser);
            
            if (myReservations.isEmpty()) {
                System.out.println("║ No reservations found.                                                           ║");
                System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }

            // Print header
            System.out.println("║                                                                               ║");
            System.out.printf("║ %-3s %-10s %-15s %-12s %-19s %-19s %-10s ║\n", 
                "No.", "Room", "Type", "Status", "Check-in", "Check-out", "Amount");
            System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");

            // Print reservations
            int index = 1;
            for (Reservation r : myReservations) {
                String checkIn = r.getCheckInDate().toLocalDate().toString();
                String checkOut = r.getCheckOutDate().toLocalDate().toString();
                System.out.printf("║ %-3d %-10s %-15s %-12s %-19s %-19s RM%-8.2f ║\n",
                    index++,
                    r.getRoom().getRoomNumber(),
                    r.getRoom().getType(),
                    r.getStatus(),
                    checkIn,
                    checkOut,
                    r.getTotalAmount());
            }
            
            System.out.println("╠═══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ Options:                                                                       ║");
            System.out.println("║ 1. Select a reservation                                                       ║");
            System.out.println("║ 0. Back to main menu                                                         ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
            
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();
            
            if (choice.equals("0")) {
                return;
            } else if (choice.equals("1")) {
                System.out.print("Enter reservation number: ");
                try {
                    int selected = Integer.parseInt(scanner.nextLine()) - 1;
                    if (selected >= 0 && selected < myReservations.size()) {
                        handleReservationActions(myReservations.get(selected), myReservations);
                    } else {
                        System.out.println("Invalid reservation number.");
                        ConsoleUtils.waitForEnter(scanner);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    ConsoleUtils.waitForEnter(scanner);
                }
            } else {
                System.out.println("Invalid choice.");
                ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void handleReservationActions(Reservation reservation, List<Reservation> allReservations) {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n=== Reservation Details ===");
            System.out.println("Room Number: " + reservation.getRoom().getRoomNumber());
            System.out.println("Room Type: " + reservation.getRoom().getType());
            System.out.println("Status: " + reservation.getStatus());
            System.out.println("Check-in Date: " + reservation.getCheckInDate().toLocalDate());
            System.out.println("Check-out Date: " + reservation.getCheckOutDate().toLocalDate());
            System.out.println("Total Amount: RM" + String.format("%.2f", reservation.getTotalAmount()));
            // Show invoice payment status
            Invoice invoice = paymentService.getInvoiceById(reservation.getId());
            if (invoice != null) {
                System.out.println("\n--- Invoice ---");
                System.out.println("Invoice Payment Status: " + invoice.getPaymentStatus());
                System.out.println("Amount Paid: RM" + String.format("%.2f", invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add)));
                System.out.println("Outstanding: RM" + String.format("%.2f", invoice.getTotalAmount().subtract(invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add))));
                // Display room service orders
                List<RoomServiceOrder> orders = roomServiceManager.getOrdersForRoom(reservation.getRoom());
                boolean hasRoomService = false;
                for (RoomServiceOrder order : orders) {
                    if (order.getGuest().equals(reservation.getGuest())) {
                        if (!hasRoomService) {
                            System.out.println("Room Service Orders:");
                            hasRoomService = true;
                        }
                        System.out.println("- Order ID: " + order.getId() + ", Items: " + order.getItems());
                    }
                }
                if (!hasRoomService) {
                    System.out.println("No room service orders for this stay.");
                }
            }
            System.out.println("\nOptions:");
            System.out.println("1. Check-in");
            System.out.println("2. Check-out");
            System.out.println("3. Cancel Reservation");
            System.out.println("0. Back");
            
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    // Check if user already has an active check-in
                    boolean hasActiveCheckIn = allReservations.stream()
                        .anyMatch(r -> r.getStatus() == ReservationStatus.CHECKED_IN);
                    if (hasActiveCheckIn) {
                        System.out.println("Error: You already have an active check-in. Cannot check-in to multiple rooms.");
                    } else if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                        System.out.println("Cannot check in. Reservation is not CONFIRMED or not fully paid.");
                    } else if (invoice != null && !"PAID".equals(invoice.getPaymentStatus().toString())) {
                        System.out.println("Cannot check in. Invoice is not fully paid.");
                    } else if (bookingManager.checkIn(reservation)) {
                        System.out.println("Successfully checked in!");
                    } else {
                        System.out.println("Check-in failed. Reservation must be in CONFIRMED status.");
                    }
                    ConsoleUtils.waitForEnter(scanner);
                    break;
                case "2":
                    if (invoice != null) {
                        // Always display invoice and room service orders
                        while (true) {
                            ConsoleUtils.clearScreen();
                            System.out.println("\n--- Invoice ---");
                            System.out.println("Invoice Payment Status: " + invoice.getPaymentStatus());
                            System.out.println("Amount Paid: RM" + String.format("%.2f", invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add)));
                            System.out.println("Outstanding: RM" + String.format("%.2f", invoice.getTotalAmount().subtract(invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add))));
                            // Display room service orders
                            List<RoomServiceOrder> orders = roomServiceManager.getOrdersForRoom(reservation.getRoom());
                            boolean hasRoomService = false;
                            for (RoomServiceOrder order : orders) {
                                if (order.getGuest().equals(reservation.getGuest())) {
                                    if (!hasRoomService) {
                                        System.out.println("Room Service Orders:");
                                        hasRoomService = true;
                                    }
                                    System.out.println("- Order ID: " + order.getId() + ", Items: " + order.getItems());
                                }
                            }
                            if (!hasRoomService) {
                                System.out.println("No room service orders for this stay.");
                            }
                            BigDecimal outstanding = invoice.getTotalAmount().subtract(invoice.getPayments().stream().map(p -> p.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add));
                            if (outstanding.compareTo(BigDecimal.ZERO) > 0) {
                                System.out.println("\nOutstanding balance detected. Please pay the remaining amount to check out.");
                                System.out.print("Enter payment amount (RM): ");
                                String payAmountStr = scanner.nextLine();
                                BigDecimal payAmount = new BigDecimal(payAmountStr);
                                // Show payment methods as numbered list
                                PaymentMethod[] methods = PaymentMethod.values();
                                System.out.println("Select payment method:");
                                for (int i = 0; i < methods.length; i++) {
                                    System.out.println((i+1) + ". " + methods[i]);
                                }
                                int methodIdx = -1;
                                while (methodIdx < 0 || methodIdx >= methods.length) {
                                    System.out.print("Enter payment method (1-" + methods.length + "): ");
                                    String methodInput = scanner.nextLine();
                                    try {
                                        methodIdx = Integer.parseInt(methodInput) - 1;
                                        if (methodIdx < 0 || methodIdx >= methods.length) throw new Exception();
                                    } catch (Exception e) {
                                        System.out.println("Invalid selection. Please enter a number between 1 and " + methods.length + ".");
                                    }
                                }
                                PaymentMethod payMethod = methods[methodIdx];
                                System.out.print("Enter transaction reference: ");
                                String payTransRef = scanner.nextLine();
                                System.out.print("Enter notes (optional): ");
                                String payNotes = scanner.nextLine();
                                boolean paidNow = paymentService.processPayment(invoice, payAmount, payMethod, payTransRef, payNotes);
                                if (paidNow) {
                                    System.out.println("Payment successful! Outstanding balance cleared.");
                                } else {
                                    System.out.println("Partial payment received. Please pay the full outstanding amount to check out.");
                                }
                                System.out.println("Invoice Payment Status: " + invoice.getPaymentStatus());
                                ConsoleUtils.waitForEnter(scanner);
                                // Loop again if still not paid
                                continue;
                            }
                            // If fully paid, break loop to proceed with check-out
                            break;
                        }
                        // After payment loop, check invoice status again
                        if ("PAID".equals(invoice.getPaymentStatus().toString())) {
                            System.out.println("Proceeding with check-out...");
                            if (bookingManager.checkOut(reservation, PaymentMethod.CASH, "", "")) {
                                System.out.println("Successfully checked out!");
                            } else {
                                System.out.println("Check-out failed. Reservation must be in CHECKED_IN status.");
                            }
                        } else {
                            System.out.println("Cannot check out. Invoice is not fully paid.");
                        }
                        ConsoleUtils.waitForEnter(scanner);
                    }
                    break;
                case "3":
                    if (bookingManager.cancelReservation(reservation)) {
                        System.out.println("Reservation cancelled successfully!");
                    } else {
                        System.out.println("Could not cancel reservation. It may be already checked-in or completed.");
                    }
                    ConsoleUtils.waitForEnter(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void orderRoomService() {
        // Check if guest is checked in
        Reservation checkedInReservation = null;
        for (Reservation reservation : reservations) {
            if (reservation.getGuest().equals(currentUser) && 
                reservation.getStatus() == com.hotelmgmt.models.reservation.ReservationStatus.CHECKED_IN) {
                checkedInReservation = reservation;
                break;
            }
        }

        if (checkedInReservation == null) {
            System.out.println("\nYou must be checked in to order room service.");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }

        // Get all menu items
        List<MenuItem> menuItems = MenuItem.initializeMenuItems();
        
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n--- Room Service Menu ---");
            
            // Display all menu items
            System.out.println("\nAvailable Items:");
            for (MenuItem item : menuItems) {
                System.out.printf("%s. %s - $%.2f\n", item.getId(), item.getName(), item.getPrice());
            }
            
            System.out.println("\n0. Back to Main Menu");
            System.out.print("\nSelect item: ");
            String itemChoice = scanner.nextLine();
            
            if (itemChoice.equals("0")) {
                return;
            }
            
            // Find selected item
            MenuItem selectedItem = menuItems.stream()
                .filter(item -> item.getId().equals(itemChoice))
                .findFirst()
                .orElse(null);
                
            if (selectedItem == null) {
                System.out.println("Invalid item selection.");
                ConsoleUtils.waitForEnter(scanner);
                continue;
            }
            
            // Get special instructions
            System.out.print("Special instructions (press Enter for none): ");
            String instructions = scanner.nextLine();
            
            // Create order
            List<MenuItem> orderItems = new ArrayList<>();
            orderItems.add(selectedItem);
            
            RoomServiceOrder order = roomServiceManager.placeOrder(
                (Guest) currentUser,
                checkedInReservation.getRoom(),
                orderItems,
                instructions
            );
            
            System.out.println("\nOrder placed successfully!");
            System.out.println("Order ID: " + order.getId());
            System.out.println("Status: " + order.getStatus());
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void viewBills() {
        // Implementation of viewBills method
    }

    private void showReportMenu() {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n=== Report Menu ===");
            System.out.println("1. Room Occupancy Analysis");
            System.out.println("2. Revenue Analysis");
            System.out.println("3. Guest Analysis");
            System.out.println("4. Room Service Analysis");
            System.out.println("5. Comprehensive Report");
            System.out.println("0. Back to Main Menu");
            
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();
            
            if (choice.equals("0")) {
                return;
            }
            
            System.out.print("\nEnter start date (YYYY-MM-DD): ");
            String startDateStr = scanner.nextLine();
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endDateStr = scanner.nextLine();
            
            try {
                LocalDate startDate = LocalDate.parse(startDateStr);
                LocalDate endDate = LocalDate.parse(endDateStr);
                
                Report reportAnalyse = new Report(reservations, rooms, roomServices, invoices);
                
                switch (choice) {
                    case "1":
                        reportAnalyse.analyzeRoomPopularity(startDate, endDate);
                        break;
                    case "2":
                        reportAnalyse.generateFinancialReport(startDate, endDate);
                        break;
                    case "3":
                        // Guest analysis is not implemented yet
                        System.out.println("Guest analysis feature is not available yet.");
                        break;
                    case "4":
                        // Room service analysis is not implemented yet
                        System.out.println("Room service analysis feature is not available yet.");
                        break;
                    case "5":
                        reportAnalyse.generateComprehensiveReport(startDate, endDate);
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
                
                ConsoleUtils.waitForEnter(scanner);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                ConsoleUtils.waitForEnter(scanner);
            }
        }
    }
}