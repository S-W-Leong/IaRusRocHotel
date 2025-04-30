package com.hotelmgmt.models.user;

import java.time.LocalDate;

public class Staff extends User {
    private String employeeId;
    private LocalDate joiningDate;
    private String department;
    private boolean isActive;

    public Staff(String username, String password, String firstName, String lastName,
                String email, String phoneNumber, UserRole role, String employeeId,
                LocalDate joiningDate, String department) {
        super(username, password, firstName, lastName, email, phoneNumber, role);
        this.employeeId = employeeId;
        this.joiningDate = joiningDate;
        this.department = department;
        this.isActive = true;
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
} 