package com.hotelmgmt.ui;

import com.hotelmgmt.services.RoomManager;
import com.hotelmgmt.utils.ConsoleUtils;
import java.util.Scanner;

public class RoomManageMenu {
    private final Scanner scanner;
    private final RoomManager roomManager;

    public RoomManageMenu(Scanner scanner, RoomManager roomManager) {
        this.scanner = scanner;
        this.roomManager = roomManager;
    }

    public void showMenu() {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔═════════════════════════════════════════════════════════════════╗");
            System.out.println("║                     ROOM MANAGEMENT MENU                        ║");
            System.out.println("╠═════════════════════════════════════════════════════════════════╣");
            System.out.println("║                                                                 ║");
            System.out.println("║  1. View All Rooms                                              ║");
            System.out.println("║  2. Add New Room                                                ║");
            System.out.println("║  3. Update Room Status                                          ║");
            System.out.println("║  4. Back to Main Menu                                           ║");
            System.out.println("║                                                                 ║");
            System.out.println("╚═════════════════════════════════════════════════════════════════╝");
            System.out.print("\nEnter your choice: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    roomManager.viewAllRooms();
                    break;
                case "2":
                    roomManager.addNewRoom();
                    break;
                case "3":
                    roomManager.updateRoomStatus();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
                    System.out.println("║                    Invalid choice! Try again.                    ║");
                    System.out.println("╚══════════════════════════════════════════════════════════════════╝");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }
} 