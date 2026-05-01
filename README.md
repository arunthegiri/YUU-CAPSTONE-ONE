# Accounting Ledger

A command-line Java application for tracking financial transactions — deposits, payments, and everything in between. Built as a Capstone 1 project for the Pluralsight Java Academy.

---

## What It Does

- Add deposits and payments from the home screen
- View your full transaction history (newest first)
- Filter by deposits only or payments only
- Run pre-defined date reports: Month-to-Date, Previous Month, Year-to-Date, Previous Year
- Search all transactions by vendor name
- Everything auto-saves to a local `transactions.csv` file — no database needed

---

## Project Structure

```
AccountingLedger/
├── src/
│   ├── AccountingLedgerApp.java   ← entry point, all CLI screens and menus
│   ├── LedgerManager.java         ← business logic, file I/O, filtering, reports
│   └── Transaction.java           ← data model, CSV parsing, formatting
├── transactions.csv               ← auto-created on first save; stores all data
└── README.md
```

### What each file does

**`Transaction.java`**
The data model. Every line in `transactions.csv` becomes one of these. Handles parsing from CSV, serializing back to CSV, and knows whether it's a deposit or a payment.

**`LedgerManager.java`**
The engine. Owns the in-memory list of transactions, reads and writes the CSV file, runs all filtering and date-based reports, and handles the formatted output to the console.

**`AccountingLedgerApp.java`**
The user interface. Controls all the menus and screen navigation. Reads user input, validates it, and calls into `LedgerManager` to get things done. The `clearScreen()` method prints 30 blank lines before each new menu to simulate a clean terminal.

---

## How to Run

### 1. Clone or download the project

```bash
git clone https://github.com/YOUR_USERNAME/AccountingLedger.git
cd AccountingLedger
```

### 2. Compile

Run this from the **project root** (not inside `src/`):

```bash
javac -d out src/*.java
```

This compiles all three files and drops the `.class` files into an `out/` folder.

> **Note:** You need Java 14 or higher. The app uses switch expressions which were added in Java 14. Run `java -version` to check yours.

### 3. Run

```bash
java -cp out AccountingLedgerApp
```

Keep the `transactions.csv` in the same folder you run the command from — that's where the app reads and writes it.

---

## How to Run in IntelliJ IDEA

### 1. Open the project

Open IntelliJ and choose **File → Open**, then select the `AccountingLedger` folder (the root, not `src/`). IntelliJ will open it as a plain Java project.

### 2. Mark the src folder as source root

Right-click the `src/` folder in the Project panel on the left → **Mark Directory as → Sources Root**. It'll turn blue. This tells IntelliJ where your `.java` files live.

### 3. Set the Java SDK

Go to **File → Project Structure → Project** and make sure the SDK is set to Java 14 or higher. If you don't have one set up, click **Add SDK → Download JDK** and grab any version 17 or 21 (both are fine).

### 4. Fix the working directory so the CSV loads correctly

This is the one step people miss. The app looks for `transactions.csv` in whatever folder it's run from. By default IntelliJ runs from the project root which is correct, but double-check:

- Click the dropdown next to the green Run button at the top → **Edit Configurations**
- Select `AccountingLedgerApp` under Application
- Make sure **Working directory** is set to the project root (the `AccountingLedger/` folder, not `src/`)
- Hit OK

### 5. Run it

Click the green **Run** button or hit `Shift + F10`. The terminal output will appear in the **Run** panel at the bottom of IntelliJ.

> **Heads up:** IntelliJ's built-in Run panel doesn't always handle interactive console input well. If typing feels laggy or broken, go to **Help → Find Action → search "Registry"** → enable `editor.runs.in.terminal`. That makes it use a proper terminal instead.

---

## CSV Format

Every transaction is stored as a single pipe-delimited line:

```
date|time|description|vendor|amount
```

Example:
```
2024-01-15|11:30:00|Invoice 1001 paid|Acme Corp|1500.00
2024-01-10|14:22:10|Office Supplies|Staples|-45.99
```

- **Deposits** have a positive amount
- **Payments** have a negative amount — the app negates it for you if you enter a positive number

The file is appended to (never overwritten), so your data is always safe.

---

## Navigation

```
Home Screen
  ├── D  Add Deposit          ← prompts for description, vendor, amount
  ├── P  Make Payment         ← same as deposit, amount saved as negative
  ├── L  View Ledger
  │     ├── A  All Transactions    (newest first)
  │     ├── D  Deposits Only       (newest first)
  │     ├── P  Payments Only       (newest first)
  │     ├── R  Reports
  │     │     ├── 1  Month To Date
  │     │     ├── 2  Previous Month
  │     │     ├── 3  Year To Date
  │     │     ├── 4  Previous Year
  │     │     ├── 5  Search by Vendor
  │     │     └── 0  Back to Ledger
  │     └── H  Back to Home
  └── X  Exit
```

---

## Sample Data

The included `transactions.csv` has 50 sample transactions from January–May 2024. It covers:

- Recurring expenses (rent, phone, electric, internet, gas)
- Income (invoices, consulting fees, freelance payments)
- One-off purchases (Amazon, Best Buy, Staples, IKEA)
- Meals and misc

This gives you real data to test every report and filter right away.

---

## Known Limitations / Future Ideas

- No edit or delete — once a transaction is saved, it's in the file. You can edit the CSV manually.
- Amounts are stored as plain `double` — fine for a capstone but a real app would use `BigDecimal`
- Custom search (bonus feature from the spec) is not implemented yet
- The "clear screen" is 30 blank lines, not a true terminal clear — works fine for demos

---

## Requirements

- Java 14+
- No external libraries or dependencies

---

