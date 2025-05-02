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
            System.out.println("║                        REPORT MENU                            ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Generate Financial Report                                  ║");
            System.out.println("║ 2. Analyze Room Popularity                                    ║");
            System.out.println("║ 3. Generate Comprehensive Report                              ║");
            System.out.println("║ 0. Back to Main Menu                                          ║");
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

    private LocalDate[] getDateRange() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SELECT DATE RANGE                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        LocalDate startDate = getDate("Enter start date (YYYY-MM-DD): ");
        if (startDate == null) return null;
        
        LocalDate endDate = getDate("Enter end date (YYYY-MM-DD): ");
        if (endDate == null) return null;
        
        if (endDate.isBefore(startDate)) {
            System.out.println("\nEnd date cannot be before start date.");
            ConsoleUtils.waitForEnter(scanner);
            return null;
        }
        
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
                return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
    }
} 