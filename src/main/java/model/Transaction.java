package main.java.model;

public class Transaction {
    private String date;
    private String description;
    private double amount;
    private String category; // Optional attribute for categorizing transactions

    public Transaction(String date, String description, double amount, String category) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    // Getters for all attributes (date, description, amount, category)
    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    // Optional setters for description and category (date and amount might be set only during creation)
    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Optional method for displaying transaction details
    @Override
    public String toString() {
        return "Date: " + date + ", Description: " + description + ", Amount: $" + String.format("%.2f", amount) + (category != null ? ", Category: " + category : "");
    }
}