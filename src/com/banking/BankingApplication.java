package com.banking;

import java.sql.*;
import java.util.Scanner;

public class BankingApplication {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Welcome to the Banking Application!");
            boolean exit = false;

            while (!exit) {
                System.out.println("\n====Menu:====");
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Check Balance");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> deposit(connection, scanner);
                    case 2 -> withdraw(connection, scanner);
                    case 3 -> checkBalance(connection, scanner);
                    case 4 -> {
                        exit = true;
                        System.out.println("Thank you for using the Banking Application. Have a good day!");
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private static void deposit(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Deposit must be greater than 0.");
            return;
        }

        String query = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, amount);
            statement.setInt(2, accountId);
            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private static void withdraw(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Withdrawal must be greater than 0.");
            return;
        }

        String balanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement balanceStmt = connection.prepareStatement(balanceQuery)) {
            balanceStmt.setInt(1, accountId);
            ResultSet rs = balanceStmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (amount > balance) {
                    System.out.println("Insufficient balance.");
                } else {
                    String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setDouble(1, amount);
                        updateStmt.setInt(2, accountId);
                        updateStmt.executeUpdate();
                        System.out.println("Withdrawal successful.");
                    }
                }
            } else {
                System.out.println("Account not found.");
            }
        }
    }

    private static void checkBalance(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter account ID: ");
        int accountId = scanner.nextInt();

        String query = "SELECT account_holder, balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, accountId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String accountHolder = rs.getString("account_holder");
                double balance = rs.getDouble("balance");
                System.out.printf("Account Name: %s\nBalance: %.2f\n", accountHolder, balance);
            } else {
                System.out.println("Account not found.");
            }
        }
    }
}
