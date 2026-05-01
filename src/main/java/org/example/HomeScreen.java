package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class HomeScreen {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "transactions.csv";

    public static void main(String[] args) {
        showHome();
    }

    public static void showHome() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Financial Tracker Home ---");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "D":
                    handleTransaction(true); // Deposit (Positive)
                    break;
                case "P":
                    handleTransaction(false); // Payment (Negative)
                    break;
                case "L":
                    // This will call your Ledger screen later
                    System.out.println("Redirecting to Ledger...");
                    // LedgerScreen.display();
                    break;
                case "X":
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleTransaction(boolean isDeposit) {
        System.out.println(isDeposit ? "\n--- Add Deposit ---" : "\n--- Make Payment ---");

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter vendor: ");
        String vendor = scanner.nextLine();

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(scanner.nextLine());

        // Adjust amount to be negative if it's a payment
        if (!isDeposit && amount > 0) {
            amount *= -1;
        }

        // Get current Date and Time
        String date = LocalDate.now().toString();
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // Format: date|time|description|vendor|amount
        String entry = String.format("%s|%s|%s|%s|%.2f", date, time, description, vendor, amount);

        saveToFile(entry);
    }

    private static void saveToFile(String entry) {
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(entry + System.lineSeparator());
            fw.flush(); // Force the data out of the buffer and into the file
            System.out.println("Successfully wrote: " + entry);
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Could not write to file!");
            e.printStackTrace();
        }
    }
}
