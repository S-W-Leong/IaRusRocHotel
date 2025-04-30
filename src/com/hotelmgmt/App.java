package com.hotelmgmt;

import java.util.Scanner;
import com.hotelmgmt.ui.MainMenu;

public class App {
    public static void main(String[] args) {
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