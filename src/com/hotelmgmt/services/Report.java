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
            System.out.printf("║ %-15s: RM %-39.2f (%.1f%%) ║\n", method, amount, percentage);
        });
        
        // Print Revenue Breakdown
        System.out.println("║                                                                ║");
        System.out.println("║ Revenue Breakdown:                                             ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Room Revenue: RM %-45.2f ║\n", roomRevenue);
        System.out.printf("║ Room Service Revenue: RM %-37.2f ║\n", roomServiceRevenue);
        System.out.printf("║ Total Revenue: RM %-45.2f║\n", totalRevenue);
        
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
                System.out.printf("║ %-20s: %-30d bookings (%.1f%%) ║\n", 
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
        
        generateFinancialReport(startDate, endDate);
        analyzeRoomPopularity(startDate, endDate);
    }

    // Room Service Revenue Report
    public void generateRoomServiceRevenueReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println( " ║                  ROOM SERVICE REVENUE REPORT                   ║");
        System.out.println(  "╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Period: %-54s ║\n", startDate + " to " + endDate);
        System.out.printf("║ Generated on: %-48s ║\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("╠════════════════════════════════════════════════════════════════╣");

        // Get all invoices from PaymentService
        List<Invoice> allInvoices = paymentService.getAllInvoices();

        // Calculate total room service revenue
        BigDecimal totalRoomServiceRevenue = roomServiceOrders.stream()
            .filter(o -> allInvoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .anyMatch(invoice -> invoice.getRoomServices().contains(o)))
            .filter(o -> !o.getOrderTime().toLocalDate().isBefore(startDate) && 
                        !o.getOrderTime().toLocalDate().isAfter(endDate))
            .map(RoomServiceOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Revenue by category
        Map<MenuCategory, BigDecimal> revenueByCategory = roomServiceOrders.stream()
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

        // Most popular items
        Map<String, Long> itemPopularity = roomServiceOrders.stream()
            .filter(o -> allInvoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .anyMatch(invoice -> invoice.getRoomServices().contains(o)))
            .filter(o -> !o.getOrderTime().toLocalDate().isBefore(startDate) && 
                        !o.getOrderTime().toLocalDate().isAfter(endDate))
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                MenuItem::getName,
                Collectors.counting()
            ));

        // Print total revenue
        System.out.println("║                                                                ║");
        System.out.println("║ Total Room Service Revenue:                                    ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Revenue: RM %-44.2f ║\n", totalRoomServiceRevenue);

        // Print revenue by category
        System.out.println("║                                                                ║");
        System.out.println("║ Revenue by Category:                                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        revenueByCategory.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                double percentage = entry.getValue().doubleValue() / totalRoomServiceRevenue.doubleValue() * 100;
                System.out.printf("║ %-20s: RM %-31.2f (%.1f%%) ║\n", 
                    entry.getKey(), entry.getValue(), percentage);
            });

        // Print most popular items
        System.out.println("║                                                                ║");
        System.out.println("║ Most Popular Items:                                            ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        itemPopularity.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(5)
            .forEach(entry -> {
                System.out.printf("║ %-20s: %-30d orders ║\n", 
                    entry.getKey(), entry.getValue());
            });

        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private boolean isWithinDateRange(LocalDateTime date, LocalDate startDate, LocalDate endDate) {
        LocalDate dateOnly = date.toLocalDate();
        return !dateOnly.isBefore(startDate) && !dateOnly.isAfter(endDate);
    }
}
