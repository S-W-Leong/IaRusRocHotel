package com.hotelmgmt.ui;

import com.hotelmgmt.models.housekeeping.HousekeepingTask;
import com.hotelmgmt.models.housekeeping.TaskStatus;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.models.user.Staff;
import com.hotelmgmt.services.HousekeepingService;
import com.hotelmgmt.services.RoomManager;
import com.hotelmgmt.utils.ConsoleUtils;
import java.util.List;
import java.util.Scanner;

public class HousekeepingStaffMenu {
    private final Scanner scanner;
    private final HousekeepingService housekeepingService;
    private final RoomManager roomManager;
    private final Staff currentStaff;

    public HousekeepingStaffMenu(Scanner scanner, HousekeepingService housekeepingService, 
                                RoomManager roomManager, Staff currentStaff) {
        this.scanner = scanner;
        this.housekeepingService = housekeepingService;
        this.roomManager = roomManager;
        this.currentStaff = currentStaff;
    }

    public void viewTasks() {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     MY ASSIGNED TASKS                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");

        List<HousekeepingTask> myTasks = housekeepingService.getTasksByStaff(currentStaff);

        if (myTasks.isEmpty()) {
            System.out.println("║ No tasks currently assigned.                                    ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }

        for (int i = 0; i < myTasks.size(); i++) {
            HousekeepingTask task = myTasks.get(i);
            System.out.printf("║ Task %d:                                                        ║\n", i + 1);
            System.out.printf("║   Room: %-52s ║\n", task.getRoom().getRoomNumber());
            System.out.printf("║   Status: %-50s ║\n", task.getStatus());
            System.out.printf("║   Scheduled: %-47s ║\n", task.getScheduledTime());
            if (!task.getProgressNotes().isEmpty()) {
                System.out.printf("║   Notes: %-51s ║\n", task.getProgressNotes());
            }
            System.out.println("║                                                                ║");
        }

        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Update Task Status                                          ║");
        System.out.println("║ 0. Back                                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        System.out.print("\nEnter your choice (0-1): ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            updateTaskStatus(myTasks);
        }
    }

    private void updateTaskStatus(List<HousekeepingTask> myTasks) {
        System.out.print("\nEnter task number to update: ");
        try {
            int taskNum = Integer.parseInt(scanner.nextLine()) - 1;
            if (taskNum >= 0 && taskNum < myTasks.size()) {
                HousekeepingTask selectedTask = myTasks.get(taskNum);
                
                System.out.println("\nCurrent Status: " + selectedTask.getStatus());
                System.out.println("\nAvailable Statuses:");
                System.out.println("1. IN_PROGRESS  - Task is currently being worked on");
                System.out.println("2. COMPLETED    - Task has been finished");

                System.out.print("\nEnter new status (1-2): ");
                String statusChoice = scanner.nextLine();
                TaskStatus newStatus = null;
                
                switch (statusChoice) {
                    case "1":
                        newStatus = TaskStatus.IN_PROGRESS;
                        break;
                    case "2":
                        newStatus = TaskStatus.COMPLETED;
                        break;
                    default:
                        System.out.println("Invalid status choice.");
                        return;
                }

                System.out.print("Enter progress notes: ");
                String notes = scanner.nextLine();

                housekeepingService.updateTaskStatus(selectedTask.getTaskId(), newStatus, notes);
                System.out.println("\nTask status updated successfully!");
            } else {
                System.out.println("\nInvalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nInvalid input. Please enter a number.");
        }
        ConsoleUtils.waitForEnter(scanner);
    }

    public void viewRoomStatus() {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔═════════════════════════════════ Room Status ══════════════════════════════════════╗");
        System.out.println("║  Room No.  │      Type      │     Status     │  Floor  │ Needs Cleaning │  Notes   ║");
        System.out.println("╠════════════╪════════════════╪════════════════╪═════════╪═══════════════╪══════════╣");
        
        List<Room> rooms = roomManager.getRooms();
        for (Room room : rooms) {
            String notes = "";
            List<HousekeepingTask> roomTasks = housekeepingService.getTasksByRoom(room);
            if (!roomTasks.isEmpty()) {
                HousekeepingTask latestTask = roomTasks.get(roomTasks.size() - 1);
                if (latestTask.getAssignedStaff().equals(currentStaff)) {
                    notes = "Assigned";
                }
            }
            
            System.out.printf("║   %-8s │  %-12s  │   %-10s   │    %-3d  │      %-7s   │  %-7s  ║\n",
                room.getRoomNumber(),
                room.getType().toString().replace("_SUITE", ""),
                room.getStatus(),
                room.getFloor(),
                room.isNeedsCleaning() ? "YES" : "NO",
                notes);
        }
        
        System.out.println("╚════════════╧════════════════╧════════════════╧═════════╧═══════════════╧══════════╝");
        ConsoleUtils.waitForEnter(scanner);
    }
} 