package main.java.service;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message); // Call the super constructor to pass the message
    }
}