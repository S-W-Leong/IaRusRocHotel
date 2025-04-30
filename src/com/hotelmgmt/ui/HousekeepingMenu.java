package com.hotelmgmt.ui;

import com.hotelmgmt.models.housekeeping.HousekeepingTask;
import com.hotelmgmt.models.housekeeping.TaskStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Staff;
import com.hotelmgmt.services.HousekeepingService;
import com.hotelmgmt.utils.ConsoleUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class HousekeepingMenu {
    private HousekeepingService housekeepingService;
    private Scanner scanner;

    public HousekeepingMenu(HousekeepingService housekeepingService) {
        this.housekeepingService = housekeepingService;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- Housekeeping Management ---");
            System.out.println("1. Assign Task");
            System.out.println("2. List Tasks");
            System.out.println("3. Update Task Status");
            System.out.println("4. Add Housekeeping Staff");
            System.out.println("5. List Housekeeping Staff");
            System.out.println("0. Exit");
            System.out.print("Select option: ");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1: assignTask(); break;
                case 2: listTasks(); break;
                case 3: updateTaskStatus(); break;
                case 4: addStaff(); break;
                case 5: listStaff(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private void assignTask() {
        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine();
        // For demo, create a dummy room and staff
        Room room = new Room("101", null, null, 1);
        Staff staff = new Staff("hkuser", "pass", "John", "Doe", "hk@hotel.com", "1234567890", null, "E001", java.time.LocalDate.now(), "Housekeeping");
        housekeepingService.addStaff(staff);
        System.out.print("Enter scheduled time (yyyy-MM-ddTHH:mm): ");
        LocalDateTime scheduledTime = LocalDateTime.parse(scanner.nextLine());
        housekeepingService.assignTask(taskId, room, staff, scheduledTime);
        System.out.println("Task assigned.");
    }

    private void listTasks() {
        List<HousekeepingTask> tasks = housekeepingService.getTasks();
        for (HousekeepingTask task : tasks) {
            System.out.println("Task ID: " + task.getTaskId() + ", Room: " + task.getRoom().getRoomNumber() + ", Staff: " + task.getAssignedStaff().getFirstName() + ", Status: " + task.getStatus() + ", Notes: " + task.getProgressNotes());
        }
    }

    private void updateTaskStatus() {
        System.out.print("Enter Task ID: ");
        String taskId = scanner.nextLine();
        System.out.print("Enter new status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED): ");
        TaskStatus status = TaskStatus.valueOf(scanner.nextLine());
        System.out.print("Enter progress notes: ");
        String notes = scanner.nextLine();
        housekeepingService.updateTaskStatus(taskId, status, notes);
        System.out.println("Task status updated.");
    }

    private void addStaff() {
        System.out.print("Enter staff username: ");
        String username = scanner.nextLine();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        Staff staff = new Staff(username, "pass", firstName, lastName, username+"@hotel.com", "1234567890", null, "E"+System.currentTimeMillis(), java.time.LocalDate.now(), "Housekeeping");
        housekeepingService.addStaff(staff);
        System.out.println("Staff added.");
    }

    private void listStaff() {
        List<Staff> staffList = housekeepingService.getHousekeepingStaff();
        for (Staff staff : staffList) {
            System.out.println("Staff: " + staff.getFirstName() + " " + staff.getLastName() + ", ID: " + staff.getEmployeeId());
        }
    }
} 