package com.hotelmgmt.ui;

import com.hotelmgmt.models.roomService.MenuCategory;
import com.hotelmgmt.models.roomService.MenuItem;
import com.hotelmgmt.models.roomService.RoomServiceOrder;
import com.hotelmgmt.models.user.Guest;
import com.hotelmgmt.models.room.Room;
import com.hotelmgmt.services.RoomServiceManager;
import com.hotelmgmt.utils.ConsoleUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RoomServiceMenu {
    private final RoomServiceManager roomServiceManager;
    private final Scanner scanner;
    private final List<MenuItem> menuItems;

    public RoomServiceMenu(RoomServiceManager roomServiceManager, Scanner scanner) {
        this.roomServiceManager = roomServiceManager;
        this.scanner = scanner;
        this.menuItems = MenuItem.initializeMenuItems();
    }

    public void displayMenu(Guest guest, Room room) {
        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                     ROOM SERVICE MENU                           ║");
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            System.out.println("║ Please select a category:                                       ║");
            System.out.println("║                                                                ║");
            System.out.println("║ 1. Breakfast                                                   ║");
            System.out.println("║ 2. Lunch                                                       ║");
            System.out.println("║ 3. Dinner                                                      ║");
            System.out.println("║ 4. Snacks                                                      ║");
            System.out.println("║ 5. Beverages                                                   ║");
            System.out.println("║ 6. Desserts                                                    ║");
            System.out.println("║ 7. View Current Order                                          ║");
            System.out.println("║ 8. Complete Order                                              ║");
            System.out.println("║ 9. Back to Main Menu                                          ║");
            System.out.println("║                                                                ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            System.out.print("\nEnter your choice (1-9): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayCategoryItems(MenuCategory.BREAKFAST, guest, room);
                    break;
                case "2":
                    displayCategoryItems(MenuCategory.LUNCH, guest, room);
                    break;
                case "3":
                    displayCategoryItems(MenuCategory.DINNER, guest, room);
                    break;
                case "4":
                    displayCategoryItems(MenuCategory.SNACKS, guest, room);
                    break;
                case "5":
                    displayCategoryItems(MenuCategory.BEVERAGES, guest, room);
                    break;
                case "6":
                    displayCategoryItems(MenuCategory.DESSERTS, guest, room);
                    break;
                case "7":
                    viewCurrentOrder(guest, room);
                    break;
                case "8":
                    if (completeOrder(guest, room)) {
                        return;
                    }
                    break;
                case "9":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
            }
        }
    }

    private void displayCategoryItems(MenuCategory category, Guest guest, Room room) {
        List<MenuItem> categoryItems = MenuItem.getMenuItemsByCategory(menuItems, category);
        List<MenuItem> selectedItems = new ArrayList<>();

        while (true) {
            ConsoleUtils.clearScreen();
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.printf("║                      %-42s║\n", category.toString());
            System.out.println("╠════════════════════════════════════════════════════════════════╣");
            
            for (int i = 0; i < categoryItems.size(); i++) {
                MenuItem item = categoryItems.get(i);
                System.out.printf("║ %d. %-54s ║\n", (i + 1), item.getName());
                System.out.printf("║    %-54s ║\n", item.getDescription());
                System.out.printf("║    RM %.2f                                                    ║\n", item.getPrice());
                System.out.println("║                                                                ║");
            }
            
            System.out.println("║ 0. Back to Categories                                          ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            
            System.out.print("\nEnter item number to add to order (0 to go back): ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                break;
            }

            try {
                int index = Integer.parseInt(choice) - 1;
                if (index >= 0 && index < categoryItems.size()) {
                    MenuItem selectedItem = categoryItems.get(index);
                    selectedItems.add(selectedItem);
                    System.out.println("\nAdded " + selectedItem.getName() + " to your order.");
                    
                    System.out.print("\nWould you like to add another item from this category? (Y/N): ");
                    if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                        break;
                    }
                } else {
                    System.out.println("Invalid item number. Please try again.");
                    ConsoleUtils.waitForEnter(scanner);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                ConsoleUtils.waitForEnter(scanner);
            }
        }

        if (!selectedItems.isEmpty()) {
            System.out.print("\nEnter any special instructions (or press Enter to skip): ");
            String specialInstructions = scanner.nextLine();
            
            RoomServiceOrder order = roomServiceManager.placeOrder(guest, room, selectedItems, specialInstructions);
            System.out.println("\nItems added to your order successfully!");
            ConsoleUtils.waitForEnter(scanner);
        }
    }

    private void viewCurrentOrder(Guest guest, Room room) {
        ConsoleUtils.clearScreen();
        List<RoomServiceOrder> orders = roomServiceManager.getOrdersByGuest(guest);
        
        if (orders.isEmpty()) {
            System.out.println("\nYou have no current orders.");
            ConsoleUtils.waitForEnter(scanner);
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     CURRENT ORDERS                              ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");

        for (RoomServiceOrder order : orders) {
            System.out.println("║ Order ID: " + order.getId());
            System.out.println("║ Items:");
            for (MenuItem item : order.getItems()) {
                System.out.printf("║  - %-54s ║\n", item.getName());
                System.out.printf("║    RM %.2f                                                    ║\n", item.getPrice());
            }
            if (order.getSpecialInstructions() != null && !order.getSpecialInstructions().isEmpty()) {
                System.out.println("║ Special Instructions: " + order.getSpecialInstructions());
            }
            System.out.printf("║ Total Amount: RM %.2f                                          ║\n", order.getTotalAmount());
            System.out.println("║                                                                ║");
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        ConsoleUtils.waitForEnter(scanner);
    }

    private boolean completeOrder(Guest guest, Room room) {
        List<RoomServiceOrder> orders = roomServiceManager.getOrdersByGuest(guest);
        
        if (orders.isEmpty()) {
            System.out.println("\nYou have no orders to complete.");
            ConsoleUtils.waitForEnter(scanner);
            return false;
        }

        System.out.println("\nAre you sure you want to complete your order? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("\nYour order has been sent to the kitchen!");
            System.out.println("Thank you for using our room service.");
            ConsoleUtils.waitForEnter(scanner);
            return true;
        }
        return false;
    }
} 