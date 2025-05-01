package com.hotelmgmt.services;

import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.user.Staff;
import com.hotelmgmt.models.user.User;
import com.hotelmgmt.models.user.UserRole;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    private final Map<String, User> users;

    public AuthenticationService() {
        this.users = new HashMap<>();
        // Add some default users for testing
        createDefaultUsers();
    }

    private void createDefaultUsers() {
        // Create a manager
        Staff manager = new Staff("admin", "admin123", "System", "Admin", 
            "system.admin@hotel.com", "9876543210", UserRole.MANAGER, "EMP001",
            LocalDate.now(), "Management");
        users.put(manager.getUsername(), manager);

        // Create a housekeeping staff
        Staff staff = new Staff("staff", "staff123", "House", "Keeper",
            "staff@hotel.com", "1234567891", UserRole.HOUSEKEEPING_STAFF, "EMP002",
            LocalDate.now(), "Housekeeping");
        users.put(staff.getUsername(), staff);

        // Create a test guest
        Guest guest = new Guest("guest", "guest123", "Test", "Guest",
            "guest@test.com", "1234567892", "P123456",
            LocalDate.of(1990, 1, 1), "USA");
        users.put(guest.getUsername(), guest);
    }

    public User authenticate(String username, String password) throws Exception {
        User user = users.get(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        
        // In a real application, use proper password hashing
        if (!user.getPassword().equals(password)) {
            throw new Exception("Invalid password");
        }
        
        return user;
    }

    public Guest registerGuest(String username, String password, String firstName,
                             String lastName, String email, String phone) throws Exception {
        if (users.containsKey(username)) {
            throw new Exception("Username already exists");
        }

        Guest guest = new Guest(username, password, firstName, lastName, email, phone,
            "TBD", LocalDate.now(), "TBD");
        users.put(username, guest);
        
        return guest;
    }

    public void changePassword(String username, String oldPassword, String newPassword) throws Exception {
        User user = authenticate(username, oldPassword);
        // In a real application, hash the password before storing
        user.setPassword(newPassword);
    }
} 