package com.hotelmgmt.ui;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.Payment;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.billing.PaymentStatus;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.reservation.ReservationStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomData;
import com.hotelmgmt.models.room.RoomType;
import com.hotelmgmt.models.roomService.MenuCategory;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.user.Staff;
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
    private final RoomManager roomManager;
    private final ReservationsMenu reservationsMenu;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
        this.authService = new AuthenticationService();
        seedRooms();
        this.roomService = new RoomService(rooms);
        this.reservationService = new ReservationService();
        this.roomServiceManager = new RoomServiceManager(rooms);
        this.paymentService = new PaymentService();
        this.bookingManager = new BookingManager(roomService, reservationService, paymentService, roomServiceManager);
        this.housekeepingService = new HousekeepingService();
        this.roomManager = new RoomManager(rooms);
        this.reservationsMenu = new ReservationsMenu(scanner, bookingManager, reservationService, paymentService, roomService, roomServiceManager);
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
        ConsoleUtils.clearScreen();
        Logo.display();
        System.out.println("\n1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        System.out.print("\nEnter your choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "0":
                System.out.println("\nThank you for using our system!");
                System.exit(0);
                break;
            default:
                System.out.println("\nInvalid choice. Please try again.");
                ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void showMainMenu() {
        while (currentUser != null) {
            ConsoleUtils.clearScreen();
            Logo.display();
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.printf("║ Welcome, %-52s ║\n", currentUser.getFullName());
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. View Profile                                                ║");
            
            if (currentUser.getRole() == UserRole.GUEST) {
                System.out.println("║ 2. Room Booking & Reservations                                ║");
            } else if (currentUser.getRole() == UserRole.MANAGER) {
                System.out.println("║ 2. Room Management                                           ║");
                System.out.println("║ 3. Housekeeping Management                                   ║");
                System.out.println("║ 4. Reports                                                   ║");
            } else {
                System.out.println("║ 2. View Tasks                                                ║");
                System.out.println("║ 3. View Room Status                                          ║");
            }
            
            System.out.println("║ 0. Logout                                                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();
            handleMainMenuChoice(choice);
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
                    reservationsMenu.displayMenu((Guest) currentUser);
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
                    Report report = new Report(reservations, rooms, roomServices, invoices);
                    new ReportMenu(report).displayMenu();
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        } else {
            HousekeepingStaffMenu staffMenu = new HousekeepingStaffMenu(scanner, housekeepingService, roomManager, (Staff)currentUser);
            switch (choice) {
                case "0":
                    logout();
                    break;
                case "1":
                    viewProfile();
                    break;
                case "2":
                    staffMenu.viewTasks();
                    break;
                case "3":
                    staffMenu.viewRoomStatus();
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
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

    private void logout() {
        currentUser = null;
        System.out.println("\nLogout successful!");
        ConsoleUtils.waitForEnter(scanner);
    }

    private void viewProfile() {
        new ViewProfile(scanner).displayProfile(currentUser);
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
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        } while (!RegisterRequirement.isValidUsername(username));

        String password;
        do {
            System.out.print("Enter password: ");
            password = scanner.nextLine();
            if (!RegisterRequirement.isValidPassword(password)) {
                System.out.println("Invalid password! Password must be at least 8 characters long and contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character.");
                ConsoleUtils.waitForEnter(scanner);
                return;
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
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        } while (!RegisterRequirement.isValidEmail(email));

        String phone;
        do {
            System.out.print("Enter phone number: ");
            phone = scanner.nextLine();
            if (!RegisterRequirement.isValidPhone(phone)) {
                System.out.println("Invalid phone number format! Please enter a valid phone number.");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        } while (!RegisterRequirement.isValidPhone(phone));

        String passportNumber;
        do {
            System.out.print("Enter passport number (digits only): ");
            passportNumber = scanner.nextLine();
            if (!RegisterRequirement.isValidPassportNumber(passportNumber)) {
                System.out.println("Invalid passport number! Please enter digits only.");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        } while (!RegisterRequirement.isValidPassportNumber(passportNumber));

        String dateOfBirth;
        do {
            System.out.print("Enter date of birth (YYYY-MM-DD): ");
            dateOfBirth = scanner.nextLine();
            if (!RegisterRequirement.isValidDateOfBirth(dateOfBirth)) {
                System.out.println("Invalid date format! Please use YYYY-MM-DD format with leading zeros (e.g., 2000-01-01).");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        } while (!RegisterRequirement.isValidDateOfBirth(dateOfBirth));

        String nationality;
        do {
            System.out.print("Enter nationality: ");
            nationality = scanner.nextLine();
            if (!RegisterRequirement.isValidNationality(nationality)) {
                System.out.println("Invalid nationality! Please enter letters only.");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        } while (!RegisterRequirement.isValidNationality(nationality));

        try {
            currentUser = authService.registerGuest(username, password, firstName, lastName, email, phone, 
                passportNumber, LocalDate.parse(dateOfBirth), nationality);
            System.out.println("\nRegistration successful!");
            ConsoleUtils.waitForEnter(scanner);
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    // Seed some rooms
    private void seedRooms() {
        // Standard Rooms (20 rooms)
        for (int i = 1; i <= 20; i++) {
            String roomNumber = String.format("SD%03d", i);
            rooms.add(new Room(roomNumber, RoomType.STANDARD, new BigDecimal("200"), 1));
        }

        // Deluxe Rooms (15 rooms)
        for (int i = 1; i <= 15; i++) {
            String roomNumber = String.format("DX%03d", i);
            rooms.add(new Room(roomNumber, RoomType.DELUXE, new BigDecimal("350"), 2));
        }

        // Suite Rooms (10 rooms)
        for (int i = 1; i <= 10; i++) {
            String roomNumber = String.format("ST%03d", i);
            rooms.add(new Room(roomNumber, RoomType.SUITE, new BigDecimal("400"), 3));
        }

        // Executive Rooms (5 rooms)
        for (int i = 1; i <= 5; i++) {
            String roomNumber = String.format("ET%03d", i);
            rooms.add(new Room(roomNumber, RoomType.EXECUTIVE_SUITE, new BigDecimal("500"), 4));
        }

        // Presidential Room (1 room)
        rooms.add(new Room("PE501", RoomType.PRESIDENTIAL_SUITE, new BigDecimal("800"), 5));
    }

    
}