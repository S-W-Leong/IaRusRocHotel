package com.hotelmgmt.services;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.Payment;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.billing.PaymentStatus;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.roomService.MenuCategory;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Report {
    private final List<Reservation> reservations;
    private final List<Room> rooms;
    private final List<RoomServiceOrder> roomServiceOrders;
    private final List<Invoice> invoices;
    private final PaymentService paymentService;

    public Report(List<Reservation> reservations, List<Room> rooms, 
                        List<RoomServiceOrder> roomServiceOrders, List<Invoice> invoices) {
        this.reservations = reservations;
        this.rooms = rooms;
        this.roomServiceOrders = roomServiceOrders;
        this.invoices = invoices;
        this.paymentService = new PaymentService();
    }

    // Financial Report
    public void generateFinancialReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      FINANCIAL REPORT                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Period: %-54s ║\n", startDate + " to " + endDate);
        System.out.printf("║ Generated on: %-48s ║\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        // Get all invoices from PaymentService
        List<Invoice> allInvoices = paymentService.getAllInvoices();
        
        // Payment Analysis - Only include completed transactions
        final BigDecimal[] totalPaidAmount = {BigDecimal.ZERO};
        final BigDecimal[] totalPendingAmount = {BigDecimal.ZERO};
        final int[] paidCount = {0};
        final int[] pendingCount = {0};
        
        // Payment method analysis - Only include completed payments
        Map<PaymentMethod, BigDecimal> paymentsByMethod = allInvoices.stream()
            .filter(invoice -> isWithinDateRange(invoice.getGeneratedAt(), startDate, endDate))
            .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
            .flatMap(invoice -> invoice.getPayments().stream())
            .collect(Collectors.groupingBy(
                Payment::getMethod,
                Collectors.mapping(Payment::getAmount, 
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        // Calculate completed transactions
        for (Invoice invoice : allInvoices) {
            if (isWithinDateRange(invoice.getGeneratedAt(), startDate, endDate)) {
                if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
                    totalPaidAmount[0] = totalPaidAmount[0].add(invoice.getTotalAmount());
                    paidCount[0]++;
                } else {
                    totalPendingAmount[0] = totalPendingAmount[0].add(invoice.getTotalAmount().subtract(invoice.getPaidAmount()));
                    pendingCount[0]++;
                }
            }
        }

        // Room Revenue - Only include completed reservations
        BigDecimal roomRevenue = reservations.stream()
            .filter(r -> r.getStatus().equals(com.hotelmgmt.models.reservation.ReservationStatus.CHECKED_OUT))
            .filter(r -> !r.getCheckInDate().toLocalDate().isAfter(endDate) && 
                        !r.getCheckOutDate().toLocalDate().isBefore(startDate))
            .map(Reservation::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Room Service Revenue - Only include completed orders
        BigDecimal roomServiceRevenue = roomServiceOrders.stream()
            .filter(o -> allInvoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .anyMatch(invoice -> invoice.getRoomServices().contains(o)))
            .filter(o -> !o.getOrderTime().toLocalDate().isBefore(startDate) && 
                        !o.getOrderTime().toLocalDate().isAfter(endDate))
            .map(RoomServiceOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Room Service Revenue by Category
        Map<MenuCategory, BigDecimal> roomServiceRevenueByCategory = roomServiceOrders.stream()
            .filter(o -> allInvoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .anyMatch(invoice -> invoice.getRoomServices().contains(o)))
            .filter(o -> !o.getOrderTime().toLocalDate().isBefore(startDate) && 
                        !o.getOrderTime().toLocalDate().isAfter(endDate))
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                MenuItem::getCategory,
                Collectors.mapping(MenuItem::getPrice, 
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        // Total Revenue
        BigDecimal totalRevenue = roomRevenue.add(roomServiceRevenue);
        
        // Print Payment Summary
        System.out.println("║                                                                ║");
        System.out.println("║ Payment Summary:                                               ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Paid Amount: RM %-41.2f║\n", totalPaidAmount[0]);
        System.out.printf("║ Number of Paid Invoices: %-37d ║\n", paidCount[0]);
        System.out.printf("║ Total Pending Amount: RM %-37.2f ║\n", totalPendingAmount[0]);
        System.out.printf("║ Number of Pending Invoices: %-34d ║\n", pendingCount[0]);
        if (paidCount[0] > 0) {
            System.out.printf("║ Average Payment Amount: RM %-39.2f ║\n", 
                totalPaidAmount[0].divide(BigDecimal.valueOf(paidCount[0]), 2, BigDecimal.ROUND_HALF_UP));
        }

        // Print Payment Method Breakdown
        System.out.println("║                                                                ║");
        System.out.println("║ Payment Method Breakdown:                                      ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        paymentsByMethod.forEach((method, amount) -> {
            double percentage = amount.doubleValue() / totalPaidAmount[0].doubleValue() * 100;
            System.out.printf("║ %-15s: RM %-31.2f (%.1f%%) ║\n", method, amount, percentage);
        });
        
        // Print Revenue Breakdown
        System.out.println("║                                                                ║");
        System.out.println("║ Revenue Breakdown:                                             ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Room Revenue: RM %-45.2f ║\n", roomRevenue);
        System.out.printf("║ Room Service Revenue: RM %-37.2f ║\n", roomServiceRevenue);
        System.out.printf("║ Total Revenue: RM %-45.2f║\n", totalRevenue);
        
        // Print Room Service Revenue by Category
        if (roomServiceRevenue.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("║                                                                ║");
            System.out.println("║ Room Service Revenue by Category:                             ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            roomServiceRevenueByCategory.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    double percentage = entry.getValue().doubleValue() / roomServiceRevenue.doubleValue() * 100;
                    System.out.printf("║ %-20s: RM %-31.2f (%.1f%%) ║\n", 
                        entry.getKey(), entry.getValue(), percentage);
                });
        }
        
        // Revenue by Room Type - Only include completed reservations
        System.out.println("║                                                                ║");
        System.out.println("║ Revenue by Room Type:                                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        Map<String, BigDecimal> revenueByRoomType = reservations.stream()
            .filter(r -> r.getStatus().equals(com.hotelmgmt.models.reservation.ReservationStatus.CHECKED_OUT))
            .filter(r -> !r.getCheckInDate().toLocalDate().isAfter(endDate) && 
                        !r.getCheckOutDate().toLocalDate().isBefore(startDate))
            .collect(Collectors.groupingBy(
                r -> r.getRoom().getType().toString(),
                Collectors.mapping(Reservation::getTotalAmount, 
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
        
        revenueByRoomType.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                double percentage = entry.getValue().doubleValue() / totalRevenue.doubleValue() * 100;
                System.out.printf("║ %-20s: RM %-31.2f (%.1f%%) ║\n", 
                    entry.getKey(), entry.getValue(), percentage);
            });
        
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    // Room Popularity Analysis
    public void analyzeRoomPopularity(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ROOM POPULARITY ANALYSIS                    ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Period: %-54s ║\n", startDate + " to " + endDate);
        System.out.printf("║ Generated on: %-48s ║\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        // Count bookings by room type - Only include completed reservations
        Map<String, Long> bookingsByRoomType = reservations.stream()
            .filter(r -> r.getStatus().equals(com.hotelmgmt.models.reservation.ReservationStatus.CHECKED_OUT))
            .filter(r -> !r.getCheckInDate().toLocalDate().isAfter(endDate) && 
                        !r.getCheckOutDate().toLocalDate().isBefore(startDate))
            .collect(Collectors.groupingBy(
                r -> r.getRoom().getType().toString(),
                Collectors.counting()
            ));
        
        // Calculate total bookings
        long totalBookings = bookingsByRoomType.values().stream()
            .mapToLong(Long::longValue)
            .sum();
        
        // Print room type popularity
        System.out.println("║                                                                ║");
        System.out.println("║ Room Type Popularity:                                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        bookingsByRoomType.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                double percentage = (double) entry.getValue() / totalBookings * 100;
                System.out.printf("║ %-20s: %-21d bookings (%.1f%%) ║\n", 
                    entry.getKey(), entry.getValue(), percentage);
            });
        
        // Most popular room type
        String mostPopularRoomType = bookingsByRoomType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No bookings");
        
        System.out.println("║                                                                ║");
        System.out.printf("║ Most Popular Room Type: %-38s ║\n", mostPopularRoomType);
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    // Generate Comprehensive Report
    public void generateComprehensiveReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                HOTEL MANAGEMENT SYSTEM REPORT                  ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Report Period: %-45s ║\n", startDate + " to " + endDate);
        System.out.printf("║ Generated on: %-45s ║\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        // Generate Financial Report
        generateFinancialReport(startDate, endDate);
        
        // Generate Room Service Revenue Report
        generateRoomServiceRevenueReport(startDate, endDate);
        
        // Generate Room Popularity Analysis
        analyzeRoomPopularity(startDate, endDate);
    }

    // Room Service Revenue Report
    public void generateRoomServiceRevenueReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println(  "║                  ROOM SERVICE REVENUE REPORT                   ║");
        System.out.println(  "╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Period: %-54s ║\n", startDate + " to " + endDate);
        System.out.printf("║ Generated on: %-48s ║\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");

        // Calculate total room service revenue and statistics
        final BigDecimal[] totalRoomServiceRevenue = {BigDecimal.ZERO};
        final int[] totalOrders = {0};
        final Map<String, Integer> ordersByRoom = new HashMap<>();
        final Map<LocalDate, BigDecimal> dailyRevenue = new HashMap<>();
        final Map<MenuCategory, BigDecimal> revenueByCategory = new HashMap<>();
        final Map<String, Long> itemPopularity = new HashMap<>();
        final Map<String, BigDecimal> itemRevenue = new HashMap<>();

        // Process all room service orders
        for (RoomServiceOrder order : roomServiceOrders) {
            LocalDate orderDate = order.getOrderTime().toLocalDate();
            if (!orderDate.isBefore(startDate) && !orderDate.isAfter(endDate)) {
                // Check if the order is paid (exists in a paid invoice)
                boolean isPaid = invoices.stream()
                    .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                    .anyMatch(invoice -> invoice.getRoomServices().contains(order));

                if (isPaid) {
                    // Update total revenue and order count
                    totalRoomServiceRevenue[0] = totalRoomServiceRevenue[0].add(order.getTotalAmount());
                    totalOrders[0]++;

                    // Track orders by room
                    String roomNumber = order.getRoom().getRoomNumber();
                    ordersByRoom.merge(roomNumber, 1, Integer::sum);

                    // Track daily revenue
                    dailyRevenue.merge(orderDate, order.getTotalAmount(), BigDecimal::add);

                    // Process items in the order
                    for (MenuItem item : order.getItems()) {
                        // Update category revenue
                        revenueByCategory.merge(
                            item.getCategory(),
                            item.getPrice(),
                            BigDecimal::add
                        );
                        
                        // Update item popularity and revenue
                        itemPopularity.merge(
                            item.getName(),
                            1L,
                            Long::sum
                        );
                        itemRevenue.merge(
                            item.getName(),
                            item.getPrice(),
                            BigDecimal::add
                        );
                    }
                }
            }
        }

        // Print summary statistics
        System.out.println("║                                                                ║");
        System.out.println("║ Summary Statistics:                                            ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Revenue: RM %-44.2f ║\n", totalRoomServiceRevenue[0]);
        System.out.printf("║ Total Orders: %-45d    ║\n", totalOrders[0]);
        if (totalOrders[0] > 0) {
            System.out.printf("║ Average Order Value: RM %-38.2f ║\n", 
                totalRoomServiceRevenue[0].divide(BigDecimal.valueOf(totalOrders[0]), 2, BigDecimal.ROUND_HALF_UP));
        }

        // Print daily revenue breakdown
        if (!dailyRevenue.isEmpty()) {
            System.out.println("║                                                                ║");
            System.out.println("║ Daily Revenue Breakdown:                                       ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            dailyRevenue.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.printf("║ %-10s: RM %-41.2f       ║\n", 
                        entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        entry.getValue());
                });
        }

        // Print revenue by category
        if (!revenueByCategory.isEmpty()) {
            System.out.println("║                                                                ║");
            System.out.println("║ Revenue by Category:                                           ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            revenueByCategory.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    double percentage = entry.getValue().doubleValue() / totalRoomServiceRevenue[0].doubleValue() * 100;
                    System.out.printf("║ %-20s: RM %-27.2f (%.1f%%)  ║\n", 
                        entry.getKey(), entry.getValue(), percentage);
                });
        }

        // Print most popular items
        if (!itemPopularity.isEmpty()) {
            System.out.println("║                                                                ║");
            System.out.println("║ Most Popular Items:                                            ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            itemPopularity.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    System.out.printf("║ %-20s: %-5d orders                             ║\n", 
                        entry.getKey(), entry.getValue());
                });
        }

        // Print highest revenue items
        if (!itemRevenue.isEmpty()) {
            System.out.println("║                                                                ║");
            System.out.println("║ Highest Revenue Items:                                         ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            itemRevenue.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    System.out.printf("║ %-20s: RM %-37.2f ║\n", 
                        entry.getKey(), entry.getValue());
                });
        }

        // Print orders by room
        if (!ordersByRoom.isEmpty()) {
            System.out.println("║                                                                ║");
            System.out.println("║ Orders by Room:                                                ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            ordersByRoom.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    System.out.printf("║ Room %-15s: %-21d orders             ║\n", 
                        entry.getKey(), entry.getValue());
                });
        }

        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private boolean isWithinDateRange(LocalDateTime date, LocalDate startDate, LocalDate endDate) {
        LocalDate dateOnly = date.toLocalDate();
        return !dateOnly.isBefore(startDate) && !dateOnly.isAfter(endDate);
    }
}
