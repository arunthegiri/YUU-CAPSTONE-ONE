import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a single financial transaction.
 * CSV format: date|time|description|vendor|amount
 */
public class Transaction {

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Transaction(LocalDate date, LocalTime time,
                       String description, String vendor, double amount) {
        this.date        = date;
        this.time        = time;
        this.description = description;
        this.vendor      = vendor;
        this.amount      = amount;
    }

    /** Parse a single CSV line: date|time|description|vendor|amount */
    public static Transaction fromCsvLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid CSV line: " + line);
        }

        LocalDate date     = LocalDate.parse(parts[0].trim());
        LocalTime time     = LocalTime.parse(parts[1].trim());
        String description = parts[2].trim();
        String vendor      = parts[3].trim();
        double amount      = Double.parseDouble(parts[4].trim());

        return new Transaction(date, time, description, vendor, amount);
    }

    /** Serialize this transaction back to a CSV line. */
    public String toCsvLine() {
        return String.format("%s|%s|%s|%s|%.2f",
                date, time, description, vendor, amount);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns true when this is a deposit (positive amount). */
    public boolean isDeposit() {
        return amount > 0;
    }

    /** Returns true when this is a payment (negative amount). */
    public boolean isPayment() {
        return amount < 0;
    }

    @Override
    public String toString() {
        return String.format("%-12s %-10s %-30s %-20s %10.2f",
                date, time, description, vendor, amount);
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public LocalDate getDate()              { return date; }
    public void setDate(LocalDate date)     { this.date = date; }

    public LocalTime getTime()              { return time; }
    public void setTime(LocalTime time)     { this.time = time; }

    public String getDescription()                  { return description; }
    public void setDescription(String description)  { this.description = description; }

    public String getVendor()               { return vendor; }
    public void setVendor(String vendor)    { this.vendor = vendor; }

    public double getAmount()               { return amount; }
    public void setAmount(double amount)    { this.amount = amount; }
}
