package com.hotelmgmt.services;

public class RegisterRequirement {
    
    public static void displayRequirements() {
        System.out.println("\n════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                           REGISTRATION REQUIREMENTS                            ");
        System.out.println("════════════════════════════════════════════════════════════════════════════════");
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║ Username:                                                                    ║");
        System.out.println("║ • Only letters, numbers, and underscore allowed                              ║");
        System.out.println("║ • Cannot be empty                                                            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Password:                                                                    ║");
        System.out.println("║ • Minimum 8 characters                                                       ║");
        System.out.println("║ • At least 1 uppercase letter                                                ║");
        System.out.println("║ • At least 1 lowercase letter                                                ║");
        System.out.println("║ • At least 1 number                                                          ║");
        System.out.println("║ • At least 1 special character (@$!%*?&)                                     ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Email:                                                                       ║");
        System.out.println("║ • Must be in valid email format                                              ║");
        System.out.println("║ • Cannot be empty                                                            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Phone:                                                                       ║");
        System.out.println("║ • Must be in valid phone number format                                       ║");
        System.out.println("║ • Can include +, -, (), and spaces                                           ║");
        System.out.println("║ • Minimum 8 digits                                                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝\n\n\n\n\n\n\n\n");
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        // Only allows alphanumeric characters and underscore
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        // Password must be at least 8 characters long and contain:
        // At least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Basic email validation pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // Allows various phone number formats including international numbers
        String phoneRegex = "^[+]?[0-9\\s-()]{8,}$";
        return phone.matches(phoneRegex);
    }
} 