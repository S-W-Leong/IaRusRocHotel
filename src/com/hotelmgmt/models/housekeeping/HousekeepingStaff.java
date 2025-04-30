package com.hotelmgmt.models.housekeeping;

import com.hotelmgmt.models.user.Staff;
import java.time.LocalDate;

public class HousekeepingStaff extends Staff {
    public HousekeepingStaff(String username, String password, String firstName, String lastName, String email, String phoneNumber, com.hotelmgmt.models.user.UserRole role, String employeeId, LocalDate joiningDate, String department) {
        super(username, password, firstName, lastName, email, phoneNumber, role, employeeId, joiningDate, department);
    }
} 