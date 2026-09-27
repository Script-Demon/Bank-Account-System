import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BankSystem {
    private static final Path DATA_FILE = Paths.get("accounts.csv");
    private static final Path TRANSACTIONS_FILE = Paths.get("transactions.csv");

    private final Map<String, BankAccount> accounts = new LinkedHashMap<>();
    private final Scanner scanner = new Scanner(System.in);
    private int nextAccountNumber = 1001;

    public static void main(String[] args) {
        new BankSystem().run();
    }

    private void run() {
        loadAccounts();
        while (true) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> checkBalance();
                case 5 -> viewDetails();
                case 6 -> transfer();
                case 7 -> {
                    System.out.println("Thank you for banking with us. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please select 1-7.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== Bank Account System =====");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Check Balance");
        System.out.println("5. View Account Details");
        System.out.println("6. Transfer Money");
        System.out.println("7. Exit");
    }

    private void createAccount() {
        String name = readLine("Enter account holder name: ");
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        double initial = readDouble("Enter initial deposit: ");
        if (initial < 0) {
            System.out.println("Initial deposit cannot be negative.");
            return;
        }
        String accNo = String.valueOf(nextAccountNumber++);
        BankAccount account = new BankAccount(accNo, name, 0);
        if (initial > 0) {
            account.deposit(initial);
        }
        accounts.put(accNo, account);
        saveAccounts();
        System.out.println("Account created successfully! Your account number is " + accNo);
    }

    private void deposit() {
        BankAccount account = findAccount();
        if (account == null) return;
        double amount = readDouble("Enter amount to deposit: ");
        try {
            account.deposit(amount);
            saveAccounts();
            System.out.printf("Deposited %.2f. New balance: %.2f%n", amount, account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void withdraw() {
        BankAccount account = findAccount();
        if (account == null) return;
        double amount = readDouble("Enter amount to withdraw: ");
        try {
            account.withdraw(amount);
            saveAccounts();
            System.out.printf("Withdrew %.2f. New balance: %.2f%n", amount, account.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("From:");
        BankAccount source = findAccount();
        if (source == null) return;
        System.out.println("To:");
        BankAccount target = findAccount();
        if (target == null) return;
        double amount = readDouble("Enter amount to transfer: ");
        try {
            source.transferTo(target, amount);
            saveAccounts();
            System.out.printf("Transferred %.2f from %s to %s. Your new balance: %.2f%n",
                    amount, source.getAccountNumber(), target.getAccountNumber(), source.getBalance());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkBalance() {
        BankAccount account = findAccount();
        if (account == null) return;
        System.out.printf("Current balance: %.2f%n", account.getBalance());
    }

    private void viewDetails() {
        BankAccount account = findAccount();
        if (account == null) return;
        System.out.println("----- Account Details -----");
        System.out.println(account);
        System.out.println();
        System.out.println("----- Transaction History -----");
        List<Transaction> history = account.getTransactions();
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        System.out.printf("%-19s  %-20s  %12s  %12s%n", "Date/Time", "Description", "Amount", "Balance");
        for (Transaction t : history) {
            System.out.println(t);
        }
    }

    private BankAccount findAccount() {
        String accNo = readLine("Enter account number: ");
        BankAccount account = accounts.get(accNo);
        if (account == null) {
            System.out.println("Account not found.");
        }
        return account;
    }

    private void loadAccounts() {
        if (!Files.exists(DATA_FILE)) return;
        try {
            for (String line : Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                // Format: accountNumber,balance,holderName (name last so it may contain commas)
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                try {
                    String accNo = parts[0];
                    int number = Integer.parseInt(accNo);
                    double balance = Double.parseDouble(parts[1]);
                    accounts.put(accNo, new BankAccount(accNo, parts[2], balance));
                    nextAccountNumber = Math.max(nextAccountNumber, number + 1);
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid record: " + line);
                }
            }
            System.out.println("Loaded " + accounts.size() + " account(s).");
        } catch (IOException e) {
            System.out.println("Could not load accounts: " + e.getMessage());
        }
        loadTransactions();
    }

    private void loadTransactions() {
        if (!Files.exists(TRANSACTIONS_FILE)) return;
        try {
            for (String line : Files.readAllLines(TRANSACTIONS_FILE, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                // Format: accountNumber,type,amount,balanceAfter,timestamp[,counterparty]
                String[] parts = line.split(",", -1);
                if (parts.length != 5 && parts.length != 6) continue;
                BankAccount account = accounts.get(parts[0]);
                if (account == null) continue;
                String counterparty = parts.length == 6 && !parts[5].isEmpty() ? parts[5] : null;
                try {
                    account.addLoadedTransaction(new Transaction(
                            Transaction.Type.valueOf(parts[1]),
                            Double.parseDouble(parts[2]),
                            Double.parseDouble(parts[3]),
                            LocalDateTime.parse(parts[4]),
                            counterparty));
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    System.out.println("Skipping invalid transaction: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load transactions: " + e.getMessage());
        }
    }

    private void saveAccounts() {
        List<String> lines = new ArrayList<>();
        List<String> txLines = new ArrayList<>();
        for (BankAccount a : accounts.values()) {
            lines.add(a.getAccountNumber() + "," + a.getBalance() + "," + a.getHolderName());
            for (Transaction t : a.getTransactions()) {
                txLines.add(a.getAccountNumber() + "," + t.getType() + "," + t.getAmount() + ","
                        + t.getBalanceAfter() + "," + t.getTimestamp() + ","
                        + (t.getCounterparty() == null ? "" : t.getCounterparty()));
            }
        }
        try {
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8);
            Files.write(TRANSACTIONS_FILE, txLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Warning: could not save accounts: " + e.getMessage());
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Input closed. Goodbye!");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                double value = Double.parseDouble(readLine(prompt));
                if (Double.isFinite(value)) return value;
                System.out.println("Please enter a valid amount.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
            }
        }
    }
}
