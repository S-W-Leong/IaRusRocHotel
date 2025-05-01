package com.hotelmgmt.services;

import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.user.Staff;
import com.hotelmgmt.models.user.User;
import com.hotelmgmt.utils.ConsoleUtils;
import java.util.Scanner;

public class ViewProfile {
    private final Scanner scanner;

    public ViewProfile(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayProfile(User user) {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                                ║");
        System.out.println("║                             USER PROFILE INFORMATION                           ║");
        System.out.println("║                                                                                ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                                ║");
        System.out.println("║   Personal Information:                                                        ║");
        System.out.println("║                                                                                ║");
        System.out.println("║     - Full Name: " + String.format("%-60s", user.getFullName()) + "  ║");
        System.out.println("║     - Username: " + String.format("%-62s", user.getUsername()) + " ║");
        System.out.println("║     - Role: " + String.format("%-66s", user.getRole()) + " ║");

        if (user instanceof Guest) {
            Guest guest = (Guest) user;
            System.out.println("║     - Passport Number: " + String.format("%-56s", guest.getPassportNumber()) + "║");
            System.out.println("║     - Date of Birth: " + String.format("%-58s", guest.getDateOfBirth()) + "║");
            System.out.println("║     - Nationality: " + String.format("%-60s", guest.getNationality()) + "║");

        } else if (user instanceof Staff) {
            Staff staff = (Staff) user;
            System.out.println("║     - Employee ID: " + String.format("%-60s", staff.getEmployeeId()) + "║");
            System.out.println("║     - Department: " + String.format("%-61s", staff.getDepartment()) + "║");
            System.out.println("║     - Joining Date: " + String.format("%-59s", staff.getJoiningDate()) + "║");
            System.out.println("║     - Status: " + String.format("%-65s", staff.isActive() ? "Active" : "Inactive") + "║");
        }
        System.out.println("║                                                                                ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                                ║");
        System.out.println("║   Contact Information:                                                         ║");
        System.out.println("║                                                                                ║");
        System.out.println("║     - Email: " + String.format("%-66s", user.getEmail()) + "║");
        System.out.println("║     - Phone: " + String.format("%-66s", user.getPhoneNumber()) + "║");
        System.out.println("║                                                                                ║");
        System.out.println("║                                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        ConsoleUtils.waitForEnter(scanner);
    }
} 