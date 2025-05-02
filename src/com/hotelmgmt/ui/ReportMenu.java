package com.hotelmgmt.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import com.hotelmgmt.services.Report;

public class ReportMenu {
    private final Report reportAnalyse;
    private final Scanner scanner;

    public ReportMenu(Report reportAnalyse) {
        this.reportAnalyse = reportAnalyse;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Hotel Management System Reports ===");
            System.out.println("1. Financial Report");
            System.out.println("2. Room Popularity Report");
            System.out.println("3. Comprehensive Report");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 0) {
                break;
            }

            LocalDate[] dates = getDateRange();
            if (dates == null) {
                continue;
            }

            switch (choice) {
                case 1:
                    reportAnalyse.generateFinancialReport(dates[0], dates[1]);
                    break;
                case 2:
                    reportAnalyse.analyzeRoomPopularity(dates[0], dates[1]);
                    break;
                case 3:
                    reportAnalyse.generateComprehensiveReport(dates[0], dates[1]);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private LocalDate[] getDateRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        try {
            System.out.print("Enter start date (YYYY-MM-DD): ");
            String startDateStr = scanner.nextLine();
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);

            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endDateStr = scanner.nextLine();
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            if (endDate.isBefore(startDate)) {
                System.out.println("End date cannot be before start date.");
                return null;
            }

            return new LocalDate[]{startDate, endDate};
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            return null;
        }
    }
} 