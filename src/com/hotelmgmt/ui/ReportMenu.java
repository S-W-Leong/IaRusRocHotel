package com.hotelmgmt.ui;

import com.hotelmgmt.services.Report;
import com.hotelmgmt.utils.ConsoleUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ReportMenu {
    private final Scanner scanner;
    private final Report report;

    public ReportMenu(Report report) {
        this.scanner = new Scanner(System.in);
        this.report = report;
    }

    public void displayMenu() {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                        REPORT MENU                             ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Generate Financial Report                                   ║");
            System.out.println("║ 2. Analyze Room Popularity                                     ║");
            System.out.println("║ 3. Generate Comprehensive Report                               ║");
            System.out.println("║ 4. Generate Room Service Revenue Report                        ║");
            System.out.println("║ 0. Back to Main Menu                                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    generateFinancialReport();
                    break;
                case "2":
                    analyzeRoomPopularity();
                    break;
                case "3":
                    generateComprehensiveReport();
                    break;
                case "4":
                    generateRoomServiceRevenueReport();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void generateFinancialReport() {
        LocalDate[] dates = getDateRange();
        if (dates != null) {
            report.generateFinancialReport(dates[0], dates[1]);
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void analyzeRoomPopularity() {
        LocalDate[] dates = getDateRange();
        if (dates != null) {
            report.analyzeRoomPopularity(dates[0], dates[1]);
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void generateComprehensiveReport() {
        LocalDate[] dates = getDateRange();
        if (dates != null) {
            report.generateComprehensiveReport(dates[0], dates[1]);
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void generateRoomServiceRevenueReport() {
        LocalDate[] dates = getDateRange();
        if (dates != null) {
            report.generateRoomServiceRevenueReport(dates[0], dates[1]);
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private LocalDate[] getDateRange() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SELECT DATE RANGE                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Enter dates in YYYY-MM-DD format (e.g., 2024-05-01)            ║");
        System.out.println("║ Type 'exit' to return to the previous menu                     ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        LocalDate startDate = getDate("║ Start Date: ");
        if (startDate == null) return null;
        
        LocalDate endDate = getDate("║ End Date: ");
        if (endDate == null) return null;
        
        if (endDate.isBefore(startDate)) {
            System.out.println("╠═════════════════════════════════════════════════════════════════╣");
            System.out.println("║ Error: End date cannot be before start date                     ║");
            System.out.println("╚═════════════════════════════════════════════════════════════════╝");
            ConsoleUtils.waitForEnter(scanner);
            return null;
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        return new LocalDate[]{startDate, endDate};
    }

    private LocalDate getDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateStr = scanner.nextLine();
            
            if (dateStr.equalsIgnoreCase("exit")) {
                return null;
            }
            
            try {
                LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                System.out.println("╠════════════════════════════════════════════════════════════════╣");
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("╠════════════════════════════════════════════════════════════════╣");
                System.out.println("║ Error: Invalid date format. Please use YYYY-MM-DD format      ║");
                System.out.println("╠════════════════════════════════════════════════════════════════╣");
            }
        }
    }
} 