package com.hotelmgmt.services;

import com.hotelmgmt.models.user.User;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.utils.ConsoleUtils;
import com.hotelmgmt.ui.Logo;
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
        System.out.println("║                                                                                ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                                ║");
        System.out.println("║   Contact Information:                                                         ║");
        System.out.println("║                                                                                ║");
        if (user instanceof Guest) {
            Guest guest = (Guest) user;
            System.out.println("║     - Email: " + String.format("%-66s", guest.getEmail()) + "║");
            System.out.println("║     - Phone: " + String.format("%-66s", guest.getPhoneNumber()) + "║");
        }
        System.out.println("║                                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        ConsoleUtils.waitForEnter(scanner);
    }
} 