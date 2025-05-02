package com.hotelmgmt.services;

import com.hotelmgmt.models.billing.Invoice;
import com.hotelmgmt.models.billing.Payment;
import com.hotelmgmt.models.billing.PaymentMethod;
import com.hotelmgmt.models.billing.PaymentStatus;
import com.hotelmgmt.models.reservation.Reservation;
import com.hotelmgmt.models.room.Room;
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

    public Report(List<Reservation> reservations, List<Room> rooms, 
                        List<RoomServiceOrder> roomServiceOrders, List<Invoice> invoices) {
        this.reservations = reservations;
        this.rooms = rooms;
        this.roomServiceOrders = roomServiceOrders;
        this.invoices = invoices;
    }

    // Financial Report
    public void generateFinancialReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n=== Financial Report ===");
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=====================================");
        
        // Payment Analysis - Only include completed transactions
        final BigDecimal[] totalPaidAmount = {BigDecimal.ZERO};
        final BigDecimal[] totalPendingAmount = {BigDecimal.ZERO};
        final int[] paidCount = {0};
        final int[] pendingCount = {0};
        
        // Payment method analysis - Only include completed payments
        Map<PaymentMethod, BigDecimal> paymentsByMethod = invoices.stream()
            .filter(invoice -> isWithinDateRange(invoice.getGeneratedAt(), startDate, endDate))
            .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
            .flatMap(invoice -> invoice.getPayments().stream())
            .collect(Collectors.groupingBy(
                Payment::getMethod,
                Collectors.mapping(Payment::getAmount, 
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        // Calculate completed transactions
        for (Invoice invoice : invoices) {
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
            .filter(o -> invoices.stream()
                .filter(invoice -> invoice.getPaymentStatus() == PaymentStatus.PAID)
                .anyMatch(invoice -> invoice.getRoomServices().contains(o)))
            .filter(o -> !o.getOrderTime().toLocalDate().isBefore(startDate) && 
                        !o.getOrderTime().toLocalDate().isAfter(endDate))
            .map(RoomServiceOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Total Revenue
        BigDecimal totalRevenue = roomRevenue.add(roomServiceRevenue);
        
        // Print Payment Summary
        System.out.println("\nPayment Summary:");
        System.out.println("----------------------------------------");
        System.out.printf("Total Paid Amount: $%.2f%n", totalPaidAmount[0]);
        System.out.printf("Number of Paid Invoices: %d%n", paidCount[0]);
        System.out.printf("Total Pending Amount: $%.2f%n", totalPendingAmount[0]);
        System.out.printf("Number of Pending Invoices: %d%n", pendingCount[0]);
        if (paidCount[0] > 0) {
            System.out.printf("Average Payment Amount: $%.2f%n", 
                totalPaidAmount[0].divide(BigDecimal.valueOf(paidCount[0]), 2, BigDecimal.ROUND_HALF_UP));
        }

        // Print Payment Method Breakdown
        System.out.println("\nPayment Method Breakdown:");
        System.out.println("----------------------------------------");
        paymentsByMethod.forEach((method, amount) -> {
            double percentage = amount.doubleValue() / totalPaidAmount[0].doubleValue() * 100;
            System.out.printf("%-15s: $%.2f (%.1f%%)\n", method, amount, percentage);
        });
        
        // Print Revenue Breakdown
        System.out.println("\nRevenue Breakdown:");
        System.out.println("----------------------------------------");
        System.out.printf("Room Revenue: $%.2f\n", roomRevenue);
        System.out.printf("Room Service Revenue: $%.2f\n", roomServiceRevenue);
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
        
        // Revenue by Room Type - Only include completed reservations
        System.out.println("\nRevenue by Room Type:");
        System.out.println("----------------------------------------");
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
                System.out.printf("%-20s: $%.2f (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), percentage);
            });
    }

    // Room Popularity Analysis
    public void analyzeRoomPopularity(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n=== Room Popularity Analysis ===");
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=====================================");
        
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
        System.out.println("\nRoom Type Popularity:");
        System.out.println("----------------------------------------");
        bookingsByRoomType.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                double percentage = (double) entry.getValue() / totalBookings * 100;
                System.out.printf("%-20s: %d bookings (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), percentage);
            });
        
        // Most popular room type
        String mostPopularRoomType = bookingsByRoomType.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No bookings");
        
        System.out.println("\nMost Popular Room Type: " + mostPopularRoomType);
    }

    // Generate Comprehensive Report
    public void generateComprehensiveReport(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n=== Hotel Management System Report ===");
        System.out.println("Report Period: " + startDate + " to " + endDate);
        System.out.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=====================================");
        
        generateFinancialReport(startDate, endDate);
        analyzeRoomPopularity(startDate, endDate);
    }

    private boolean isWithinDateRange(LocalDateTime date, LocalDate startDate, LocalDate endDate) {
        LocalDate dateOnly = date.toLocalDate();
        return !dateOnly.isBefore(startDate) && !dateOnly.isAfter(endDate);
    }
}
