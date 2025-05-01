package com.hotelmgmt;

import com.hotelmgmt.ui.Logo;
import com.hotelmgmt.ui.MainMenu;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Logo.display();
        System.out.println("Welcome to Hotel Management System");
        System.out.println("================================");

        try (Scanner scanner = new Scanner(System.in)) {
            MainMenu mainMenu = new MainMenu(scanner);
            mainMenu.start();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 