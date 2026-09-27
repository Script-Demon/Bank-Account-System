import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankAccount {
    private final String accountNumber;
    private final String holderName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();

    public BankAccount(String accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /** Restores a previously saved transaction without changing the balance. */
    public void addLoadedTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        transactions.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, LocalDateTime.now()));
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: " + String.format("%.2f", balance));
        }
        balance -= amount;
        transactions.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, LocalDateTime.now()));
    }

    public void transferTo(BankAccount target, double amount) {
        if (target == this) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds. Current balance: " + String.format("%.2f", balance));
        }
        LocalDateTime now = LocalDateTime.now();
        balance -= amount;
        target.balance += amount;
        transactions.add(new Transaction(Transaction.Type.TRANSFER_OUT, amount, balance, now, target.accountNumber));
        target.transactions.add(new Transaction(Transaction.Type.TRANSFER_IN, amount, target.balance, now, accountNumber));
    }

    @Override
    public String toString() {
        return "Account Number : " + accountNumber + "\n"
             + "Holder Name    : " + holderName + "\n"
             + "Balance        : " + String.format("%.2f", balance);
    }
}
