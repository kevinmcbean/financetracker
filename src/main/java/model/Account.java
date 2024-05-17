package main.java.model;

import main.java.service.InsufficientFundsException;

public class Account {
    private int id;  // Unique identifier for the account
    private String name;
    private String type; // Account type (e.g., Checking, Savings)
    private double balance;
    private double budget;

    public Account(int id, String name, String type, double balance, double budget) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.budget = budget;
    }

    // Getters for all attributes (id, name, type, balance)
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getBalance() {
        return balance;
    }

    public double getBudget() {
        return budget;
    }

    // Setters for name and type (optional, you can keep these private if mutations are only done through constructors)
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Methods for deposit, withdraw (implement logic as discussed earlier)
    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (this.balance < amount) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        this.balance -= amount;
    }

    // Method to set the budget for the account
    public void setBudget(double budget) {
        this.budget = budget;
    }

    // Optional method for displaying account details
    @Override
    public String toString() {
        return "Account ID: " + id + ", Name: " + name + ", Type: " + type + ", Balance: $" + String.format("%.2f", balance) + ", Budget: $" + String.format("%.2f", budget);
    }
}
