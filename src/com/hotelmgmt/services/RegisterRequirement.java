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
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Passport Number:                                                             ║");
        System.out.println("║ • Must contain only digits                                                   ║");
        System.out.println("║ • Cannot be empty                                                            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Date of Birth:                                                               ║");
        System.out.println("║ • Must be in YYYY-MM-DD format                                               ║");
        System.out.println("║ • Example: 1990-01-01                                                        ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Nationality:                                                                 ║");
        System.out.println("║ • Must contain only letters and spaces                                       ║");
        System.out.println("║ • Cannot be empty                                                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
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

    public static boolean isValidPassportNumber(String passportNumber) {
        if (passportNumber == null || passportNumber.isEmpty()) {
            return false;
        }
        // Passport number should contain only digits
        return passportNumber.matches("^\\d+$");
    }

    public static boolean isValidDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return false;
        }
        // Date format must be YYYY-MM-DD with leading zeros
        return dateOfBirth.matches("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    }

    public static boolean isValidNationality(String nationality) {
        if (nationality == null || nationality.isEmpty()) {
            return false;
        }
        // Nationality should contain only letters and spaces
        return nationality.matches("^[a-zA-Z\\s]+$");
    }
}