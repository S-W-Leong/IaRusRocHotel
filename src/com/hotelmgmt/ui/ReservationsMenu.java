package com.hotelmgmt.ui;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.billing.PaymentStatus;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.reservation.ReservationStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.room.RoomType;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.services.BookingManager;
import com.hotelmgmt.services.PaymentService;
import com.hotelmgmt.services.ReservationService;
import com.hotelmgmt.services.RoomService;
import com.hotelmgmt.services.RoomServiceManager;
import com.hotelmgmt.utils.ConsoleUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ReservationsMenu {
    private final Scanner scanner;
    private final BookingManager bookingManager;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final RoomService roomService;
    private final RoomServiceManager roomServiceManager;
    private final RoomServiceMenu roomServiceMenu;

    public ReservationsMenu(Scanner scanner, BookingManager bookingManager, ReservationService reservationService,
                          PaymentService paymentService, RoomService roomService, RoomServiceManager roomServiceManager) {
        this.scanner = scanner;
        this.bookingManager = bookingManager;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.roomService = roomService;
        this.roomServiceManager = roomServiceManager;
        this.roomServiceMenu = new RoomServiceMenu(roomServiceManager, scanner);
    }

    public void displayMenu(Guest guest) {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    RESERVATIONS MANAGEMENT                       ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Make New Reservation                                          ║");
            System.out.println("║ 2. View My Reservations                                          ║");
            System.out.println("║ 0. Back to Main Menu                                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════╝");

            System.out.print("\nEnter your choice (0-2): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    makeNewReservation(guest);
                    break;
                case "2":
                    viewMyReservations(guest);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void makeNewReservation(Guest guest) {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      NEW RESERVATION                              ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
        
        // Display available room types
        RoomType[] types = RoomType.values();
        List<Room> allRooms = roomService.getRooms();
        
        System.out.println("║                                                                   ║");
        System.out.println("║ Available Room Types:                                             ║");
        System.out.println("║                                                                   ║");
        for (int i = 0; i < types.length; i++) {
            RoomType type = types[i];
            List<Room> roomsOfType = allRooms.stream()
                .filter(r -> r.getType() == type)
                .toList();
            BigDecimal minPrice = roomsOfType.stream()
                .map(Room::getBasePrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            long availableCount = roomsOfType.stream()
                .filter(Room::isAvailable)
                .count();
            
            System.out.printf("║ %d. %-20s RM %-8.2f  Available: %-3d               ║\n", 
                i + 1, type, minPrice, availableCount);
            System.out.printf("║    %-62s ║\n", type.getDescription());
            System.out.println("║                                                                   ║");
        }
        
        System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
        
        // Get room type selection
        RoomType selectedType = null;
        while (selectedType == null) {
            System.out.print("\nSelect room type (1-" + types.length + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine()) - 1;
                if (choice >= 0 && choice < types.length) {
                    selectedType = types[choice];
                } else {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        // Get dates
        LocalDate checkIn = null;
        LocalDate checkOut = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (checkIn == null) {
            System.out.print("\nEnter check-in date (YYYY-MM-DD): ");
            try {
                checkIn = LocalDate.parse(scanner.nextLine(), formatter);
                if (checkIn.isBefore(LocalDate.now())) {
                    System.out.println("Check-in date cannot be in the past.");
                    checkIn = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        while (checkOut == null) {
            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            try {
                checkOut = LocalDate.parse(scanner.nextLine(), formatter);
                if (checkOut.isBefore(checkIn)) {
                    System.out.println("Check-out date must be after check-in date.");
                    checkOut = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        // Make reservation
        Reservation reservation = bookingManager.bookRoom(guest, selectedType.toString(), checkIn, checkOut);
        
        if (reservation != null) {
            processPayment(reservation);
        } else {
            System.out.println("\nSorry, no rooms available for the selected dates.");
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void processPayment(Reservation reservation) {
        Invoice invoice = paymentService.createInvoice(reservation);
        boolean paid = false;

        while (!paid) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                           INVOICE                              ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.printf("║ Reservation ID: %-46s ║\n", reservation.getId());
            System.out.printf("║ Room: %-15s Type: %-34s ║\n", 
                reservation.getRoom().getRoomNumber(), reservation.getRoom().getType());
            System.out.printf("║ Check-in:  %-51s ║\n", reservation.getCheckInDate().toLocalDate());
            System.out.printf("║ Check-out: %-51s ║\n", reservation.getCheckOutDate().toLocalDate());
            System.out.println("║                                                                ║");
            System.out.printf("║ Total Amount:  RM %-44.2f ║\n", invoice.getTotalAmount());
            System.out.printf("║ Amount Paid:   RM %-44.2f ║\n", invoice.getPaidAmount());
            System.out.printf("║ Outstanding:   RM %-44.2f ║\n", 
                invoice.getTotalAmount().subtract(invoice.getPaidAmount()));
            System.out.printf("║ Payment Status: %-46s ║\n", invoice.getPaymentStatus());
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Make Payment                                                ║");
            System.out.println("║ 0. Cancel Reservation                                          ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");

            System.out.print("\nEnter your choice (0-1): ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                bookingManager.cancelReservation(reservation);
                System.out.println("\nReservation cancelled.");
                ConsoleUtils.waitForEnter(scanner);
                return;
            } else if (choice.equals("1")) {
                System.out.print("\nEnter payment amount (RM): ");
                try {
                    BigDecimal amount = new BigDecimal(scanner.nextLine());
                    
                    // Show payment methods
                    System.out.println("\nPayment Methods:");
                    PaymentMethod[] methods = PaymentMethod.values();
                    for (int i = 0; i < methods.length; i++) {
                        System.out.println((i + 1) + ". " + methods[i]);
                    }

                    PaymentMethod selectedMethod = null;
                    while (selectedMethod == null) {
                        System.out.print("\nSelect payment method (1-" + methods.length + "): ");
                        try {
                            int methodChoice = Integer.parseInt(scanner.nextLine()) - 1;
                            if (methodChoice >= 0 && methodChoice < methods.length) {
                                selectedMethod = methods[methodChoice];
                            } else {
                                System.out.println("Invalid selection.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }

                    System.out.print("Enter transaction reference: ");
                    String transactionRef = scanner.nextLine();

                    System.out.print("Enter notes (optional): ");
                    String notes = scanner.nextLine();

                    paid = paymentService.processPayment(invoice, amount, selectedMethod, transactionRef, notes);
                    
                    if (paid) {
                        reservation.setStatus(ReservationStatus.CONFIRMED);
                        System.out.println("\nPayment successful! Reservation is CONFIRMED.");
                    } else {
                        System.out.println("\nPartial payment received. Full payment required to confirm reservation.");
                    }
                    ConsoleUtils.waitForEnter(scanner);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount. Please enter a valid number.");
                    ConsoleUtils.waitForEnter(scanner);
                }
            }
        }
    }

    private void viewMyReservations(Guest guest) {
        while (true) {
            ConsoleUtils.clearScreen();
            List<Reservation> myReservations = reservationService.getReservationsForGuest(guest);

            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                      MY RESERVATIONS                           ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");

            if (myReservations.isEmpty()) {
                System.out.println("║ No reservations found.                                         ║");
                System.out.println("╚════════════════════════════════════════════════════════════════╝");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }

            // Print reservations
            for (int i = 0; i < myReservations.size(); i++) {
                Reservation r = myReservations.get(i);
                System.out.printf("║ %d. Room %-10s %-15s                             ║\n", 
                    i + 1, r.getRoom().getRoomNumber(), r.getRoom().getType());
                System.out.printf("║    Status: %-51s ║\n", r.getStatus());
                System.out.printf("║    Check-in:  %-48s ║\n", r.getCheckInDate().toLocalDate());
                System.out.printf("║    Check-out: %-48s ║\n", r.getCheckOutDate().toLocalDate());
                System.out.printf("║    Amount: RM %-48.2f ║\n", r.getTotalAmount());
                System.out.println("║                                                                ║");
            }

            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Select a reservation                                        ║");
            System.out.println("║ 0. Back                                                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");

            System.out.print("\nEnter your choice (0-1): ");
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
            }
        }
    }

    private void handleReservationActions(Reservation reservation, List<Reservation> allReservations) {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println(  "║                  RESERVATION DETAILS                           ║");
            System.out.println(  "╠════════════════════════════════════════════════════════════════╣");
            System.out.printf("║ Room Number: %-50s║\n", reservation.getRoom().getRoomNumber());
            System.out.printf("║ Room Type: %-51s ║\n", reservation.getRoom().getType());
            System.out.printf("║ Status: %-54s ║\n", reservation.getStatus());
            System.out.printf("║ Check-in Date: %-47s ║\n", reservation.getCheckInDate().toLocalDate());
            System.out.printf("║ Check-out Date: %-46s ║\n", reservation.getCheckOutDate().toLocalDate());
            System.out.printf("║ Total Amount: RM %-45.2f ║\n", reservation.getTotalAmount());
            
            // Show invoice details
            Invoice invoice = paymentService.getInvoiceById(reservation.getId());
            if (invoice != null) {
                System.out.println("║                                                                ║");
                System.out.println("║ Invoice Details:                                               ║");
                System.out.printf("║ Payment Status: %-46s ║\n", invoice.getPaymentStatus());
                System.out.printf("║ Amount Paid: RM %-46.2f ║\n", invoice.getPaidAmount());
                System.out.printf("║ Outstanding: RM %-46.2f ║\n", 
                    invoice.getTotalAmount().subtract(invoice.getPaidAmount()));

                // Display room service orders if any
                List<RoomServiceOrder> orders = roomServiceManager.getOrdersForRoom(reservation.getRoom());
                boolean hasRoomService = false;
                for (RoomServiceOrder order : orders) {
                    if (order.getGuest().equals(reservation.getGuest())) {
                        if (!hasRoomService) {
                            System.out.println("║                                                                ║");
                            System.out.println("║ Room Service Orders:                                           ║");
                            hasRoomService = true;
                        }
                        System.out.printf("║ - Order ID: %-50s ║\n", order.getId());
                        for (var item : order.getItems()) {
                            System.out.printf("║   • %-58s ║\n", item.getName());
                        }
                    }
                }
            }

            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ Available Actions:                                             ║");
            System.out.println("║ 1. Check-in                                                    ║");
            System.out.println("║ 2. Check-out                                                   ║");
            System.out.println("║ 3. Cancel Reservation                                          ║");
            if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                System.out.println("║ 4. Order Room Service                                          ║");
            }
            System.out.println("║ 0. Back                                                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleCheckIn(reservation, allReservations);
                    break;
                case "2":
                    handleCheckOut(reservation);
                    break;
                case "3":
                    if (handleCancellation(reservation)) {
                        return;
                    }
                    break;
                case "4":
                    if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
                        roomServiceMenu.displayMenu((Guest)reservation.getGuest(), reservation.getRoom());
                    } else {
                        System.out.println("Room service is only available after check-in.");
                        ConsoleUtils.waitForEnter(scanner);
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void handleCheckIn(Reservation reservation, List<Reservation> allReservations) {
        boolean hasActiveCheckIn = allReservations.stream()
            .anyMatch(r -> r.getStatus() == ReservationStatus.CHECKED_IN);
        
        if (hasActiveCheckIn) {
            System.out.println("\nError: You already have an active check-in. Cannot check-in to multiple rooms.");
        } else if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            System.out.println("\nCannot check in. Reservation is not CONFIRMED or not fully paid.");
        } else {
            Invoice invoice = paymentService.getInvoiceById(reservation.getId());
            if (invoice != null && invoice.getPaymentStatus() != PaymentStatus.PAID) {
                System.out.println("\nCannot check in. Invoice is not fully paid.");
            } else if (bookingManager.checkIn(reservation)) {
                System.out.println("\nSuccessfully checked in!");
            } else {
                System.out.println("\nCheck-in failed. Please contact hotel staff.");
            }
        }
        ConsoleUtils.waitForEnter(scanner);
    }

    private void handleCheckOut(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            System.out.println("\nCannot check out. Reservation must be in CHECKED_IN status.");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }

        // Get or create invoice
        Invoice invoice = paymentService.getInvoiceById(reservation.getId());
        if (invoice == null) {
            invoice = paymentService.createInvoice(reservation);
        }

        // Add any existing room service orders to invoice
        List<RoomServiceOrder> orders = roomServiceManager.getOrdersForRoom(reservation.getRoom());
        for (RoomServiceOrder order : orders) {
            if (order.getGuest().equals(reservation.getGuest()) && 
                !invoice.getRoomServices().contains(order)) {
                invoice.addRoomService(order);
            }
        }

        // If invoice is already paid, proceed with checkout immediately
        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            if (bookingManager.checkOut(reservation, null, null, null)) {
                System.out.println("\nSuccessfully checked out!");
                ConsoleUtils.waitForEnter(scanner);
                return;
            } else {
                System.out.println("\nCheck-out failed. Please contact hotel staff.");
                ConsoleUtils.waitForEnter(scanner);
                return;
            }
        }

        // If not paid, process payment
        processCheckoutPayment(invoice, reservation);
    }

    private void processCheckoutPayment(Invoice invoice, Reservation reservation) {
        while (invoice.getPaymentStatus() != PaymentStatus.PAID) {
            displayCheckoutInvoice(invoice, reservation);
            
            System.out.println("\n1. Make Payment");
            System.out.println("2. View Room Service Details");
            System.out.println("3. Order Room Service (Optional)");
            System.out.println("0. Cancel Checkout");
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    processPayment(invoice, reservation);
                    break;
                case "2":
                    displayRoomServiceDetails(invoice);
                    break;
                case "3":
                    roomServiceMenu.displayMenu((Guest)reservation.getGuest(), reservation.getRoom());
                    // Refresh invoice after room service order
                    invoice = paymentService.getInvoiceById(reservation.getId());
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid choice.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void processPayment(Invoice invoice, Reservation reservation) {
        System.out.print("\nEnter payment amount (RM): ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            PaymentMethod selectedMethod = getPaymentMethod();
            
            System.out.print("Enter transaction reference: ");
            String transactionRef = scanner.nextLine();
            
            System.out.print("Enter notes (optional): ");
            String notes = scanner.nextLine();

            boolean paid = paymentService.processPayment(invoice, amount, selectedMethod, transactionRef, notes);
            
            if (paid) {
                if (bookingManager.checkOut(reservation, selectedMethod, transactionRef, notes)) {
                    System.out.println("\nSuccessfully checked out!");
                    ConsoleUtils.waitForEnter(scanner);
                    return;
                } else {
                    System.out.println("\nCheck-out failed. Please contact hotel staff.");
                }
            } else {
                System.out.println("\nPartial payment received. Full payment required to complete check-out.");
                System.out.printf("Remaining balance: RM %.2f\n", 
                    invoice.getTotalAmount().subtract(invoice.getPaidAmount()));
            }
            ConsoleUtils.waitForEnter(scanner);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a valid number.");
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void displayRoomServiceDetails(Invoice invoice) {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println(  "║                    ROOM SERVICE DETAILS                        ║");
        System.out.println(  "╠════════════════════════════════════════════════════════════════╣");
        
        List<RoomServiceOrder> orders = invoice.getRoomServices();
        if (orders.isEmpty()) {
            System.out.println("║ No room service orders found.                                  ║");
        } else {
            for (RoomServiceOrder order : orders) {
                System.out.printf("║ Order ID: %-52s ║\n", order.getId());
                System.out.printf("║ Date: %-57s ║\n", order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                System.out.println("║ Items:                                                         ║");
                for (var item : order.getItems()) {
                    System.out.printf("║ • %-60s ║\n", item.getName());
                    System.out.printf("║   RM %-57.2f ║\n", item.getPrice());
                }
                System.out.printf("║ Order Total: RM %-46.2f ║\n", order.getTotalAmount());
                System.out.println("║                                                                ║");
            }
        }
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        ConsoleUtils.waitForEnter(scanner);
    }

    private void displayCheckoutInvoice(Invoice invoice, Reservation reservation) {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      CHECKOUT INVOICE                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Room Charges: RM %-45.2f ║\n", reservation.getTotalAmount());
        
        // Display room service orders
        BigDecimal roomServiceTotal = BigDecimal.ZERO;
        boolean hasRoomService = false;
        for (RoomServiceOrder order : invoice.getRoomServices()) {
            if (!hasRoomService) {
                System.out.println("║                                                                ║");
                System.out.println("║ Room Service Orders:                                           ║");
                hasRoomService = true;
            }
            BigDecimal orderTotal = order.getTotalAmount();
            roomServiceTotal = roomServiceTotal.add(orderTotal);
            System.out.printf("║ Order ID: %-52s ║\n", order.getId());
            for (var item : order.getItems()) {
                System.out.printf("║ • %-60s ║\n", item.getName());
                System.out.printf("║   RM %-51.2f ║\n", item.getPrice());
            }
        }
        
        if (hasRoomService) {
            System.out.printf("║ Room Service Total: RM %-42.2f ║\n", roomServiceTotal);
        }
        
        System.out.println("║                                                                ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Amount:  RM %-44.2f ║\n", invoice.getTotalAmount());
        System.out.printf("║ Amount Paid:   RM %-44.2f ║\n", invoice.getPaidAmount());
        System.out.printf("║ Outstanding:   RM %-44.2f ║\n", 
            invoice.getTotalAmount().subtract(invoice.getPaidAmount()));
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private PaymentMethod getPaymentMethod() {
        PaymentMethod[] methods = PaymentMethod.values();
        System.out.println("\nPayment Methods:");
        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i]);
        }

        while (true) {
            System.out.print("\nSelect payment method (1-" + methods.length + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine()) - 1;
                if (choice >= 0 && choice < methods.length) {
                    return methods[choice];
                }
                System.out.println("Invalid selection.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private boolean handleCancellation(Reservation reservation) {
        if (bookingManager.cancelReservation(reservation)) {
            System.out.println("\nReservation cancelled successfully!");
            ConsoleUtils.waitForEnter(scanner);
            return true;
        } else {
            System.out.println("\nCould not cancel reservation. It may be already checked-in or completed.");
            ConsoleUtils.waitForEnter(scanner);
            return false;
        }
    }
} 