import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Manages the in-memory list of transactions and persists them to
 * transactions.csv.
 */
public class LedgerManager {

    private static final String FILE_PATH = "transactions.csv";
    private static final String HEADER = "date|time|description|vendor|amount";
    private List<Transaction> transactions = new ArrayList<>();

    // ── File I/O ──────────────────────────────────────────────────────────────

    /** Load all transactions from the CSV file into memory. */
    public void loadTransactions() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            // Create file with header so future appends are clean.
            try (FileWriter writer = new FileWriter(FILE_PATH)) {
                writer.write(HEADER + "\n");
            } catch (IOException e) {
                System.out.println("Could not create transactions file.");
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Skip header row if present
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("date|")) continue;
                }

                try {
                    Transaction transaction = Transaction.fromCsvLine(line);
                    transactions.add(transaction);
                } catch (Exception ex) {
                    System.out.println("Skipping malformed line: " + line);
                }
            }

        } catch (IOException e) {
            System.out.println("Could not load transactions file.");
        }
    }

    /** Append a single transaction to the CSV file. */
    public void saveTransaction(Transaction t) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(t.toCsvLine() + "\n");

        } catch (IOException e) {
            System.out.println("Could not save transaction.");
        }
    }

    // ── Add entries ───────────────────────────────────────────────────────────

    /** Add a deposit (positive amount) and persist it. */
    public void addDeposit(Transaction deposit) {
        if (deposit.getAmount() <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }

        transactions.add(deposit);
        saveTransaction(deposit);
    }

    /** Add a payment (negative amount) and persist it. */
    public void addPayment(Transaction payment) {
        if (payment.getAmount() > 0) {
            payment.setAmount(payment.getAmount() * -1);
        }

        transactions.add(payment);
        saveTransaction(payment);
    }

    // ── Display helpers ───────────────────────────────────────────────────────

    /** Return all transactions sorted newest-first. */
    public List<Transaction> getAllSorted() {
        List<Transaction> sortedList = new ArrayList<>(transactions);

        sortedList.sort(
                Comparator.comparing(Transaction::getDate)
                        .thenComparing(Transaction::getTime)
                        .reversed()
        );

        return sortedList;
    }

    /** Return only deposits, newest-first. */
    public List<Transaction> getDeposits() {
        List<Transaction> deposits = new ArrayList<>();

        for (Transaction t : getAllSorted()) {
            if (t.isDeposit()) {
                deposits.add(t);
            }
        }

        return deposits;
    }

    /** Return only payments, newest-first. */
    public List<Transaction> getPayments() {
        List<Transaction> payments = new ArrayList<>();

        for (Transaction t : getAllSorted()) {
            if (t.isPayment()) {
                payments.add(t);
            }
        }

        return payments;
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    /** Transactions from the first day of the current month through today. */
    public List<Transaction> getMonthToDate() {
        List<Transaction> results = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        for (Transaction t : getAllSorted()) {
            if (!t.getDate().isBefore(firstDayOfMonth) && !t.getDate().isAfter(today)) {
                results.add(t);
            }
        }

        return results;
    }

    /** Transactions for the entire previous calendar month. */
    public List<Transaction> getPreviousMonth() {
        List<Transaction> results = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1);
        LocalDate lastDayOfPreviousMonth = firstDayOfCurrentMonth.minusDays(1);

        for (Transaction t : getAllSorted()) {
            LocalDate date = t.getDate();

            if (!date.isBefore(firstDayOfPreviousMonth) && !date.isAfter(lastDayOfPreviousMonth)) {
                results.add(t);
            }
        }

        return results;
    }

    /** Transactions from Jan 1 of the current year through today. */
    public List<Transaction> getYearToDate() {
        List<Transaction> results = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = LocalDate.of(today.getYear(), 1, 1);

        for (Transaction t : getAllSorted()) {
            LocalDate date = t.getDate();

            if (!date.isBefore(firstDayOfYear) && !date.isAfter(today)) {
                results.add(t);
            }
        }

        return results;
    }

    /** Transactions for the entire previous calendar year. */
    public List<Transaction> getPreviousYear() {
        List<Transaction> results = new ArrayList<>();

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        LocalDate firstDayOfPreviousYear = LocalDate.of(previousYear, 1, 1);
        LocalDate lastDayOfPreviousYear = LocalDate.of(previousYear, 12, 31);

        for (Transaction t : getAllSorted()) {
            LocalDate date = t.getDate();

            if (!date.isBefore(firstDayOfPreviousYear) && !date.isAfter(lastDayOfPreviousYear)) {
                results.add(t);
            }
        }

        return results;
    }

    /** Return all transactions whose vendor matches case-insensitive. */
    public List<Transaction> searchByVendor(String vendor) {
        List<Transaction> results = new ArrayList<>();

        String searchText = vendor.toLowerCase();

        for (Transaction t : getAllSorted()) {
            if (t.getVendor().toLowerCase().contains(searchText)) {
                results.add(t);
            }
        }

        return results;
    }

    // ── Internal helper ───────────────────────────────────────────────────────

    /** Print a formatted list of transactions to stdout. */
    public void printTransactions(List<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("  No transactions found.");
            return;
        }

        System.out.printf("%-12s %-10s %-30s %-20s %10s%n",
                "Date", "Time", "Description", "Vendor", "Amount");

        System.out.println("-".repeat(90));

        for (Transaction t : list) {
            System.out.println(t);
        }

        System.out.println("-".repeat(90));
    }
}
