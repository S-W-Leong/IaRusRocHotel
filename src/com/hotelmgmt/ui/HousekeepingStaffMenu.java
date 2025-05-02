package com.hotelmgmt.ui;

import com.hotelmgmt.models.housekeeping.HousekeepingTask;
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
        HousekeepingMenu housekeepingMenu = new HousekeepingMenu(housekeepingService, roomManager);
        housekeepingMenu.listTasks();
        ConsoleUtils.waitForEnter(scanner);
    }

    public void viewRoomStatus() {
        ConsoleUtils.clearScreen();
        System.out.println("\n╔═════════════════════════════════ Room Status ══════════════════════════════════════╗");
        System.out.println("║  Room No.  │      Type      │     Status     │  Floor  │  Notes   ║");
        System.out.println("╠════════════╪════════════════╪════════════════╪═════════╪══════════╣");
        
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
            
            System.out.printf("║   %-8s │  %-12s  │   %-10s   │    %-3d  │  %-7s  ║\n",
                room.getRoomNumber(),
                room.getType().toString().replace("_SUITE", ""),
                room.getStatus(),
                room.getFloor(),
                notes);
        }
        
        System.out.println("╚════════════╧════════════════╧════════════════╧═════════╧══════════╝");
        ConsoleUtils.waitForEnter(scanner);
    }
} 