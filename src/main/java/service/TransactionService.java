package main.java.service;

import main.java.model.Account;
import main.java.model.Transaction;

public class TransactionService {

    public static void addIncome(Account account, Transaction transaction) throws InsufficientFundsException {
        // Validate if the transaction amount is positive (optional)
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Income transaction amount must be positive.");
        }
        account.deposit(transaction.getAmount());
    }

    public static void addExpense(Account account, Transaction transaction) throws InsufficientFundsException {
        // Check if account balance is sufficient for the expense
        if (account.getBalance() < transaction.getAmount()) {
            throw new InsufficientFundsException("Insufficient funds for expense.");
        }
        account.withdraw(transaction.getAmount());
    }

    public static void transferFunds(Account fromAccount, Account toAccount, double amount) throws InsufficientFundsException {
        // Check if fromAccount has sufficient balance
        if (fromAccount.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient funds in source account.");
        }
        // Debit the source account
        fromAccount.withdraw(amount);
        // Credit the destination account
        toAccount.deposit(amount);
    }
}