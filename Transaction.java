import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT }

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private final String counterparty; // other account number for transfers, otherwise null

    public Transaction(Type type, double amount, double balanceAfter, LocalDateTime timestamp) {
        this(type, amount, balanceAfter, timestamp, null);
    }

    public Transaction(Type type, double amount, double balanceAfter, LocalDateTime timestamp, String counterparty) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
        this.counterparty = counterparty;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getDescription() {
        switch (type) {
            case DEPOSIT:      return "Deposit";
            case WITHDRAWAL:   return "Withdrawal";
            case TRANSFER_IN:  return "Transfer from " + counterparty;
            case TRANSFER_OUT: return "Transfer to " + counterparty;
            default:           return type.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("%-19s  %-20s  %12.2f  %12.2f",
                timestamp.format(DISPLAY_FORMAT), getDescription(), amount, balanceAfter);
    }
}
