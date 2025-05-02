package com.hotelmgmt.services;

import com.hotelmgmt.models.billing.Invoice;
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
        
        // Payment Analysis
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        int paidCount = 0;
        for (Invoice invoice : invoices) {
            if (isWithinDateRange(invoice.getGeneratedAt(), startDate, endDate)) {
                if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
                    totalPaidAmount = totalPaidAmount.add(invoice.getTotalAmount());
                    paidCount++;
                }
            }
        }

        // Room Revenue
        BigDecimal roomRevenue = reservations.stream()
            .filter(r -> !r.getStatus().equals(com.hotelmgmt.models.reservation.ReservationStatus.CANCELLED))
            .filter(r -> !r.getCheckInDate().toLocalDate().isAfter(endDate) && 
                        !r.getCheckOutDate().toLocalDate().isBefore(startDate))
            .map(Reservation::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Room Service Revenue
        BigDecimal roomServiceRevenue = roomServiceOrders.stream()
            .filter(o -> !o.getOrderTime().toLocalDate().isBefore(startDate) && 
                        !o.getOrderTime().toLocalDate().isAfter(endDate))
            .map(RoomServiceOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Total Revenue
        BigDecimal totalRevenue = roomRevenue.add(roomServiceRevenue);
        
        // Print Payment Summary
        System.out.println("\nPayment Summary:");
        System.out.println("----------------------------------------");
        System.out.printf("Total Paid Amount: $%.2f%n", totalPaidAmount);
        System.out.printf("Number of Paid Invoices: %d%n", paidCount);
        if (paidCount > 0) {
            System.out.printf("Average Payment Amount: $%.2f%n", 
                totalPaidAmount.divide(BigDecimal.valueOf(paidCount), 2, BigDecimal.ROUND_HALF_UP));
        }
        
        // Print Revenue Breakdown
        System.out.println("\nRevenue Breakdown:");
        System.out.printf("Room Revenue: $%.2f\n", roomRevenue);
        System.out.printf("Room Service Revenue: $%.2f\n", roomServiceRevenue);
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
        
        // Revenue by Room Type
        System.out.println("\nRevenue by Room Type:");
        Map<String, BigDecimal> revenueByRoomType = reservations.stream()
            .filter(r -> !r.getStatus().equals(com.hotelmgmt.models.reservation.ReservationStatus.CANCELLED))
            .filter(r -> !r.getCheckInDate().toLocalDate().isAfter(endDate) && 
                        !r.getCheckOutDate().toLocalDate().isBefore(startDate))
            .collect(Collectors.groupingBy(
                r -> r.getRoom().getType().toString(),
                Collectors.mapping(Reservation::getTotalAmount, 
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));
        
        revenueByRoomType.forEach((type, amount) -> 
            System.out.printf("%s: $%.2f\n", type, amount));
    }

    // Room Popularity Analysis
    public void analyzeRoomPopularity(LocalDate startDate, LocalDate endDate) {
        System.out.println("\n=== Room Popularity Analysis ===");
        System.out.println("Period: " + startDate + " to " + endDate);
        System.out.println("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("=====================================");
        
        // Count bookings by room type
        Map<String, Long> bookingsByRoomType = reservations.stream()
            .filter(r -> !r.getStatus().equals(com.hotelmgmt.models.reservation.ReservationStatus.CANCELLED))
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
        bookingsByRoomType.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                double percentage = (double) entry.getValue() / totalBookings * 100;
                System.out.printf("%s: %d bookings (%.1f%%)\n", 
                    entry.getKey(), entry.getValue(), percentage);
            });
        
        // Most popular room
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
