import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Entry point and screen controller for the Accounting Ledger CLI application.
 *
 * Screen flow:
 *   HomeScreen  ──►  LedgerScreen  ──►  ReportsScreen
 */
public class AccountingLedgerApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final LedgerManager ledger = new LedgerManager();

    // ── Entry Point ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        ledger.loadTransactions();
        showHomeScreen();
        clearScreen();
        System.out.println("Goodbye!");
        scanner.close();
    }

    // ── Screen Helper ─────────────────────────────────────────────────────────

    /** Print 50 blank lines to simulate clearing the terminal screen. */
    private static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /** Pause until the user presses Enter, then clear the screen. */
    private static void pause() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    // ── Home Screen ───────────────────────────────────────────────────────────

    private static void showHomeScreen() {
        boolean running = true;
        while (running) {
            clearScreen();
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║     ACCOUNTING LEDGER        ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║  D) Add Deposit              ║");
            System.out.println("║  P) Make Payment             ║");
            System.out.println("║  L) Ledger                   ║");
            System.out.println("║  X) Exit                     ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "D" -> promptDeposit();
                case "P" -> promptPayment();
                case "L" -> showLedgerScreen();
                case "X" -> running = false;
                default  -> {
                    System.out.println("Invalid option. Try again.");
                    pause();
                }
            }
        }
    }

    // ── Add Deposit ───────────────────────────────────────────────────────────

    private static void promptDeposit() {
        clearScreen();
        System.out.println("\n--- Add Deposit ---");

        String description = readString("Description: ");
        String vendor      = readString("Vendor: ");
        double amount      = readPositiveDouble("Amount: $");

        Transaction deposit = new Transaction(
                LocalDate.now(),
                LocalTime.now().withNano(0),
                description,
                vendor,
                amount
        );

        ledger.addDeposit(deposit);
        System.out.println("✔ Deposit saved.");
        pause();
    }

    // ── Make Payment ──────────────────────────────────────────────────────────

    private static void promptPayment() {
        clearScreen();
        System.out.println("\n--- Make Payment ---");

        String description = readString("Description: ");
        String vendor      = readString("Vendor: ");
        double amount      = readPositiveDouble("Amount: $");

        // Store as negative for payments
        Transaction payment = new Transaction(
                LocalDate.now(),
                LocalTime.now().withNano(0),
                description,
                vendor,
                -amount
        );

        ledger.addPayment(payment);
        System.out.println("✔ Payment saved.");
        pause();
    }

    // ── Ledger Screen ─────────────────────────────────────────────────────────

    private static void showLedgerScreen() {
        boolean inLedger = true;
        while (inLedger) {
            clearScreen();
            System.out.println("\n--- LEDGER ---");
            System.out.println("  A) All Transactions");
            System.out.println("  D) Deposits");
            System.out.println("  P) Payments");
            System.out.println("  R) Reports");
            System.out.println("  H) Home");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "A" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getAllSorted());
                    pause();
                }
                case "D" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getDeposits());
                    pause();
                }
                case "P" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getPayments());
                    pause();
                }
                case "R" -> showReportsScreen();
                case "H" -> inLedger = false;
                default  -> {
                    System.out.println("Invalid option. Try again.");
                    pause();
                }
            }
        }
    }

    // ── Reports Screen ────────────────────────────────────────────────────────

    private static void showReportsScreen() {
        boolean inReports = true;
        while (inReports) {
            clearScreen();
            System.out.println("\n--- REPORTS ---");
            System.out.println("  1) Month To Date");
            System.out.println("  2) Previous Month");
            System.out.println("  3) Year To Date");
            System.out.println("  4) Previous Year");
            System.out.println("  5) Search by Vendor");
            System.out.println("  0) Back");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getMonthToDate());
                    pause();
                }
                case "2" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getPreviousMonth());
                    pause();
                }
                case "3" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getYearToDate());
                    pause();
                }
                case "4" -> {
                    clearScreen();
                    ledger.printTransactions(ledger.getPreviousYear());
                    pause();
                }
                case "5" -> promptVendorSearch();
                case "0" -> inReports = false;
                default  -> {
                    System.out.println("Invalid option. Try again.");
                    pause();
                }
            }
        }
    }

    // ── Vendor Search ─────────────────────────────────────────────────────────

    private static void promptVendorSearch() {
        clearScreen();
        System.out.print("Enter vendor name: ");
        String vendor = scanner.nextLine().trim();
        clearScreen();
        ledger.printTransactions(ledger.searchByVendor(vendor));
        pause();
    }

    // ── Input Helpers ─────────────────────────────────────────────────────────

    /** Prompt the user for a non-blank string. */
    private static String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) System.out.println("  Value cannot be empty.");
        } while (input.isEmpty());
        return input;
    }

    /** Prompt the user for a positive double value. */
    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(raw);
                if (val > 0) return val;
                System.out.println("  Amount must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Try again.");
            }
        }
    }
}
