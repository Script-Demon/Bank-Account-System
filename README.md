# Bank Account System (Java)

A console-based bank account manager.

## Features

1. Create a bank account (account numbers are assigned automatically, starting at 1001)
2. Deposit money
3. Withdraw money (cannot withdraw more than the balance)
4. Check balance
5. View account details, including full transaction history (date/time, description, amount, balance after)
6. Transfer money between accounts (recorded in both accounts' histories)
7. Exit

Accounts and transactions are saved to `accounts.csv` and `transactions.csv` after every change and loaded again on startup.

## Requirements

- JDK 14 or newer (for example, Eclipse Temurin from https://adoptium.net)

## Running

Double-click `run.bat`, or from this folder:

```
javac Transaction.java BankAccount.java BankSystem.java
java BankSystem
```

## Files

| File | Purpose |
|------|---------|
| `BankAccount.java` | A single account: number, holder name, balance, deposit/withdraw/transfer rules |
| `Transaction.java` | One deposit, withdrawal or transfer with its timestamp and resulting balance |
| `BankSystem.java` | Menu, user input, and saving/loading accounts |
| `run.bat` | Compiles and runs the program on Windows |
| `accounts.csv` | Created automatically; stores saved accounts |
| `transactions.csv` | Created automatically; stores transaction history |
