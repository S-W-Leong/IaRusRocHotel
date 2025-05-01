package com.hotelmgmt.ui;

import com.hotelmgmt.models.housekeeping.HousekeepingTask;
import com.hotelmgmt.models.housekeeping.TaskStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Staff;
import com.hotelmgmt.services.HousekeepingService;
import com.hotelmgmt.services.RoomManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class HousekeepingMenu {
    private HousekeepingService housekeepingService;
    private Scanner scanner;
    private RoomManager roomManager;

    public HousekeepingMenu(HousekeepingService housekeepingService, RoomManager roomManager) {
        this.housekeepingService = housekeepingService;
        this.scanner = new Scanner(System.in);
        this.roomManager = roomManager;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- Housekeeping Management ---");
            System.out.println("1. Assign Task");
            System.out.println("2. List Tasks");
            System.out.println("3. Update Task Status");
            System.out.println("4. Add Housekeeping Staff");
            System.out.println("5. List Housekeeping Staff");
            System.out.println("6. Remove Housekeeping Staff");
            System.out.println("0. Exit");
            System.out.print("Select option: ");
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    assignTask();
                    break;
                case 2:
                    listTasks();
                    break;
                case 3:
                    updateTaskStatus();
                    break;
                case 4:
                    addStaff();
                    break;
                case 5:
                    listStaff();
                    break;
                case 6:
                    removeStaff();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void assignTask() {
        // Get valid task ID
        String taskId;
        while (true) {
            System.out.print("Enter Task ID: ");
            taskId = scanner.nextLine();
            if (taskId != null && !taskId.trim().isEmpty()) {
                break;
            }
            System.out.println("Task ID cannot be empty. Please try again.");
        }

        // Get room from RoomManager
        System.out.print("Enter room number : ");
        final String inputRoomNumber = scanner.nextLine().toUpperCase();
        
        // Format the room number if needed
        final String formattedRoomNumber;
        if (inputRoomNumber.length() == 3) {
            // If user entered just the number (e.g., "101"), format it based on floor
            int floor = Integer.parseInt(inputRoomNumber.substring(0, 1));
            String prefix;
            switch (floor) {
                case 1: prefix = "SD"; break;
                case 2: prefix = "DX"; break;
                case 3: prefix = "ST"; break;
                case 4: prefix = "ET"; break;
                case 5: prefix = "PE"; break;
                default: prefix = "";
            }
            formattedRoomNumber = prefix + inputRoomNumber;
        } else {
            formattedRoomNumber = inputRoomNumber;
        }
        
        Room room = roomManager.getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(formattedRoomNumber))
            .findFirst()
            .orElse(null);
            
        if (room == null) {
            System.out.println("Room not found. Please try again.");
            return;
        }

        // Get staff selection
        List<Staff> availableStaff = housekeepingService.getHousekeepingStaff();
        if (availableStaff.isEmpty()) {
            System.out.println("No housekeeping staff available. Please add staff first.");
            return;
        }

        System.out.println("\nAvailable Housekeeping Staff:");
        for (int i = 0; i < availableStaff.size(); i++) {
            Staff s = availableStaff.get(i);
            System.out.printf("%d. %s (ID: %s)\n",
                    i + 1,
                    s.getFirstName(),
                    s.getEmployeeId());
        }

        Staff selectedStaff = null;
        while (selectedStaff == null) {
            System.out.print("\nSelect staff member (1-" + availableStaff.size() + "): ");
            try {
                int selection = Integer.parseInt(scanner.nextLine());
                if (selection > 0 && selection <= availableStaff.size()) {
                    selectedStaff = availableStaff.get(selection - 1);
                } else {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Get valid scheduled time
        LocalDateTime scheduledTime;
        while (true) {
            System.out.print("Enter scheduled time (yyyy-MM-ddTHH:mm): ");
            String timeInput = scanner.nextLine();
            try {
                scheduledTime = LocalDateTime.parse(timeInput);
                if (!scheduledTime.isBefore(LocalDateTime.now())) {
                    break;
                }
                System.out.println("Scheduled time must be in the future. Please try again.");
            } catch (Exception e) {
                System.out.println("Invalid date/time format. Please use yyyy-MM-ddTHH:mm");
            }
        }

        housekeepingService.assignTask(taskId, room, selectedStaff, scheduledTime);
        System.out.println("Task assigned to " + selectedStaff.getFirstName() +
                " for Room " + room.getRoomNumber());
    }

    private void listTasks() {
        List<HousekeepingTask> tasks = housekeepingService.getTasks();

        System.out.println("\n=== Housekeeping Tasks ===");

        if (tasks.isEmpty()) {
            System.out.println("No tasks currently assigned.");
        } else {
            for (HousekeepingTask task : tasks) {
                System.out.println("\nTask ID: " + task.getTaskId());
                System.out.println("Room Number: " + task.getRoom().getRoomNumber());
                System.out.println("Assigned Staff: " + task.getAssignedStaff().getFirstName());
                System.out.println("Status: " + task.getStatus());
                System.out.println("Scheduled Time: " + task.getScheduledTime());
                System.out.println("Notes: " + task.getProgressNotes());
                System.out.println("-------------------");
            }
        }
    }

    private void updateTaskStatus() {
        List<HousekeepingTask> tasks = housekeepingService.getTasks();

        if (tasks.isEmpty()) {
            System.out.println("No tasks currently assigned.");
            return;
        }

        // Display available tasks
        System.out.println("\nAvailable Tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            HousekeepingTask task = tasks.get(i);
            System.out.printf("%d. Task ID: %s - Room %s - Staff: %s - Status: %s%n",
                    i + 1,
                    task.getTaskId(),
                    task.getRoom().getRoomNumber(),
                    task.getAssignedStaff().getFirstName(),
                    task.getStatus());
        }

        // Get valid task selection
        HousekeepingTask selectedTask = null;
        while (selectedTask == null) {
            System.out.print("\nSelect task number to update (1-" + tasks.size() + "): ");
            try {
                int selection = Integer.parseInt(scanner.nextLine());
                if (selection > 0 && selection <= tasks.size()) {
                    selectedTask = tasks.get(selection - 1);
                } else {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Get valid task status
        TaskStatus status;
        while (true) {
            System.out.println("\nAvailable Task Status:");
            System.out.println("1. PENDING      - Task is waiting to be started");
            System.out.println("2. IN_PROGRESS  - Task is currently being worked on");
            System.out.println("3. COMPLETED    - Task has been finished");
            System.out.println("4. CANCELLED    - Task has been cancelled");
            System.out.print("\nEnter status number (1-4): ");

            String input = scanner.nextLine();
            try {
                switch (input) {
                    case "1":
                        status = TaskStatus.PENDING;
                        break;
                    case "2":
                        status = TaskStatus.IN_PROGRESS;
                        break;
                    case "3":
                        status = TaskStatus.COMPLETED;
                        break;
                    case "4":
                        status = TaskStatus.CANCELLED;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("\nInvalid selection. Please enter a number between 1-4.");
            }
        }

        // Get notes
        System.out.print("Enter progress notes (optional): ");
        String notes = scanner.nextLine();

        housekeepingService.updateTaskStatus(selectedTask.getTaskId(), status, notes);
        System.out.println("Task status updated successfully.");
    }

    private void addStaff() {
        System.out.print("Enter staff username: ");
        String username = scanner.nextLine();
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        Staff staff = new Staff(username, "pass", firstName, lastName, username + "@hotel.com", "1234567890", null,
                "E" + System.currentTimeMillis(), java.time.LocalDate.now(), "Housekeeping");
        housekeepingService.addStaff(staff);
        System.out.println("Staff added.");
    }

    private void listStaff() {
        List<Staff> staffList = housekeepingService.getHousekeepingStaff();
        for (Staff staff : staffList) {
            System.out.println(
                    "Staff: " + staff.getFirstName() + " " + staff.getLastName() + ", ID: " + staff.getEmployeeId());
        }
    }

    private void removeStaff() {
        System.out.print("Enter employee ID of staff to remove: ");
        String employeeId = scanner.nextLine();

        if (housekeepingService.removeStaff(employeeId)) {
            System.out.println("Staff member successfully removed.");
        } else {
            System.out.println("Staff member not found or could not be removed.");
        }
    }
}