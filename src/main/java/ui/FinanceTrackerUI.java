package main.java.ui;

import main.java.model.Account;
import main.java.model.Transaction;
import main.java.service.InsufficientFundsException;
import main.java.service.TransactionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FinanceTrackerUI implements ActionListener {

    private JFrame frame;
    private JLabel accountNameLabel, balanceLabel, totalBalanceLabel;
    private JTextField amountTextField; // More descriptive name
    private JButton depositButton, withdrawButton, addAccountButton;
    private JComboBox<String> accountComboBox;

    private ArrayList<Account> accounts = new ArrayList<>();
    private final String accountsFile = "resources/accounts.txt";

    public FinanceTrackerUI() {
        // Load accounts from file (if applicable)
        loadAccountsFromFile();


        // Initialize UI elements
        frame = new JFrame("Finance Tracker");
        accountNameLabel = new JLabel("Account Name: ");
        balanceLabel = new JLabel("Balance: $");
        amountTextField = new JTextField(10);
        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");
        accountComboBox = new JComboBox<>(); // Initialize without model
        addAccountButton = new JButton("Add Account");
        totalBalanceLabel = new JLabel("Total Balance: ");

        // Add action listeners
        depositButton.addActionListener(this);
        withdrawButton.addActionListener(this);
        addAccountButton.addActionListener(this);
        accountComboBox.addActionListener(this); // Handle account selection

        // Layout components
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));  // Adjust spacing as needed

        frame.add(accountNameLabel);
        frame.add(accountComboBox);
        frame.add(balanceLabel);
        frame.add(amountTextField);
        amountTextField.setText("100");
        frame.add(depositButton);
        frame.add(withdrawButton);
        frame.add(addAccountButton);
        frame.add(totalBalanceLabel);
        updateAccountComboBox();
        // Set frame properties and make it visible
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double amount;
        try {
            amount = Double.parseDouble(amountTextField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount. Please enter a number.");
            amountTextField.setText("");
            return;
        }

        if (e.getSource() == depositButton) {
            try {
                TransactionService.addIncome(accounts.get(accountComboBox.getSelectedIndex()), new Transaction(null, "Manual Deposit", amount, null));
                updateAccountBalanceAndLabel(); // Use the refactored method
                saveAccountsToFile();
            } catch (InsufficientFundsException ex) {
                // Handle insufficient funds for deposits (unlikely but possible)
                JOptionPane.showMessageDialog(frame, "Error adding deposit: " + ex.getMessage());
            }
        } else if (e.getSource() == withdrawButton) {
            try {
                TransactionService.addExpense(accounts.get(accountComboBox.getSelectedIndex()), new Transaction(null, "Manual Withdrawal", amount, null));
                updateAccountBalanceAndLabel();
                saveAccountsToFile();
            } catch (InsufficientFundsException ex) {
                JOptionPane.showMessageDialog(frame, "Insufficient funds for withdrawal.");
            }
        } else if (e.getSource() == addAccountButton) {
            // 1. Prompt user for account details
            String name = JOptionPane.showInputDialog(frame, "Enter Account Name:");
            String type = (String) JOptionPane.showInputDialog(frame, "Select Account Type:",
                    "Account Type", JOptionPane.QUESTION_MESSAGE, null,
                    new String[]{"Savings", "Checking", "Other"}, "Savings");
            double initialBalance = 0.0;
            boolean validInput = false;
            while (!validInput) {
                String balanceStr = JOptionPane.showInputDialog(frame, "Enter Initial Balance:");
                try {
                    initialBalance = Double.parseDouble(balanceStr);
                    validInput = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid balance amount. Please enter a number.");
                }
            }
            double budget = 0.0; // Optional budget, set to 0.0 by default
            String budgetStr = JOptionPane.showInputDialog(frame, "Enter Budget (Optional):");
            if (budgetStr != null && !budgetStr.isEmpty()) {
                try {
                    budget = Double.parseDouble(budgetStr);
                } catch (NumberFormatException ex) {
                    // Handle potential invalid budget input (optional)
                    System.err.println("Invalid budget amount entered. Ignoring budget.");
                }
            }

            // 2. Create a new Account object
            int nextId = getNextAccountId(); // Implement method to find the next available ID
            Account newAccount = new Account(nextId, name, type, initialBalance, budget);

            // 3. Add the account to the accounts list
            accounts.add(newAccount);

            // 4. Update UI elements
            updateAccountComboBox(); // Update dropdown with the new account
            accountComboBox.setSelectedItem(name); // Select the newly added account

            // 5. Optionally save accounts to file
            saveAccountsToFile(); // Implement method to save updated accounts list to file
        } else if (e.getSource() == accountComboBox) {
            updateAccountBalanceAndLabel(); // Update balance for the selected account
        }
    }

    private void updateAccountBalanceAndLabel() {
        if (!accounts.isEmpty()) {
            Account selectedAccount = accounts.get(accountComboBox.getSelectedIndex());
            accountNameLabel.setText("Account Name: " + selectedAccount.getName());
            balanceLabel.setText("Balance: $" + String.format("%.2f", selectedAccount.getBalance()));

            double totalBalance = 0.0;
            for (Account account : accounts) {
                totalBalance += account.getBalance();
            }
            totalBalanceLabel.setText("Total Balance: $" + String.format("%.2f", totalBalance));
        } else {
            // Optionally display a message indicating no accounts yet
            accountNameLabel.setText("Account Name:");
            balanceLabel.setText("Balance: $");
        }
    }

    private void loadAccountsFromFile() {
        try {
            FileReader reader = new FileReader(accountsFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Parse the CSV line and create an Account object
                String[] data = line.split(",");
                int id = Integer.parseInt(data[0]);
                String name = data[1];
                String type = data[2];
                double balance = Double.parseDouble(data[3]);
                double budget = Double.parseDouble(data[4]);
                accounts.add(new Account(id, name, type, balance, budget));
            }
            bufferedReader.close();
            reader.close();
        } catch (IOException e) {
            // Handle potential file I/O exceptions (e.g., file not found)
            System.err.println("Error loading accounts from file: " + e.getMessage());
        }
    }

    private void saveAccountsToFile() {
        try {
            FileWriter writer = new FileWriter(accountsFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (Account account : accounts) {
                // Convert Account object to CSV format
                String accountData = String.format("%d,%s,%s,%.2f,%.2f\n", account.getId(), account.getName(), account.getType(), account.getBalance(), account.getBudget());
                bufferedWriter.write(accountData);
            }
            bufferedWriter.close();
            writer.close();
        } catch (IOException e) {
            // Handle potential file I/O exceptions (e.g., file write error)
            System.err.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    private void updateAccountComboBox() {
        List<String> accountNames = getAccountNames();
        accountComboBox.setModel(new DefaultComboBoxModel<>(accountNames.toArray(new String[0])));
        updateAccountBalanceAndLabel(); // Update displayed account info after updating dropdown
    }

    private List<String> getAccountNames() {
        List<String> names = new ArrayList<>();
        for (Account account : accounts) {
            names.add(account.getName());
        }
        return names;
    }

    private int getNextAccountId() {
        int maxId = 0;
        for (Account account : accounts) {
            maxId = Math.max(maxId, account.getId()); // Find the highest ID so far
        }
        return maxId + 1; // Return the next available ID (highest + 1)
    }

    public static void main(String[] args) {
        try {
            new FinanceTrackerUI();
        } catch (Exception e) {
            System.err.println("Error starting Finance Tracker UI: " + e.getMessage());
            // Optionally display an error message to the user here
        }
    }
}






