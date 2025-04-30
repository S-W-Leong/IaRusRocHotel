package com.hotelmgmt.utils;

import java.util.Scanner;

public class ConsoleUtils {
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void waitForEnter(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static String readPassword(Scanner scanner) {
        return scanner.nextLine(); // In a real application, use Console.readPassword()
    }

    public static void printHeader(String title) {
        System.out.println("\n" + title);
        System.out.println("=".repeat(title.length()));
    }

    public static void printError(String message) {
        System.out.println("\nError: " + message);
    }

    public static void printSuccess(String message) {
        System.out.println("\nSuccess: " + message);
    }
} 