package main.java.com.example.finance;

import main.java.model.Account;
import main.java.model.Transaction;
import main.java.service.TransactionService;
import main.java.service.InsufficientFundsException;

public class FinanceTracker {

    public static void main(String[] args) {
        // Create some accounts with unique IDs and set initial balances and budgets
        Account checking = new Account(1, "Checking", "Checking", 1000, 1200);
        Account savings = new Account(2, "Savings", "Savings", 500, 800);

        // Sample transactions (modify as needed)
        Transaction incomeTransaction = new Transaction("2024-05-17", "Salary", 2000, "Food");
        Transaction expenseTransaction = new Transaction("2024-05-17", "Rent", 500, "Housing");

        // Add income and expense transactions (using TransactionService)
        try {
            TransactionService.addIncome(checking, incomeTransaction);
            TransactionService.addExpense(savings, expenseTransaction);
        } catch (InsufficientFundsException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }

        // (Optional) Display account details after transactions
        System.out.println("Checking Account Details:");
        System.out.println(checking.toString());
        System.out.println("Savings Account Details:");
        System.out.println(savings.toString());
    }
}