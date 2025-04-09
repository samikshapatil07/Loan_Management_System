package main;

import dao.ILoanRepository;
import dao.ILoanRepositoryImpl;
import entity.*;

import java.util.Scanner;

public class LoanManagement {

    public static void main(String[] args) {
        ILoanRepository repo = new ILoanRepositoryImpl();
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        System.out.println("=== Welcome to Loan Management System ===");

        while (choice != 7) {
            System.out.println("\n==== MENU ====");
            System.out.println("1. Apply Loan");
            System.out.println("2. View All Loans");
            System.out.println("3. Get Loan by ID");
            System.out.println("4. Calculate Interest");
            System.out.println("5. Check Loan Status");
            System.out.println("6. Repay Loan");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Enter Loan Type (H for HomeLoan, C for CarLoan): ");
                    String type = scanner.next();

                    System.out.println("Loan ID: ");
                    int loanId = scanner.nextInt();

                    System.out.println("Customer ID: ");
                    int customerId = scanner.nextInt();
                    Customer customer = new Customer(customerId, "", "", "", "", 0);

                    System.out.println("Principal Amount: ");
                    double principal = scanner.nextDouble();

                    System.out.println("Interest Rate: ");
                    double rate = scanner.nextDouble();

                    System.out.println("Loan Term (months): ");
                    int term = scanner.nextInt();
                    scanner.nextLine(); // clear buffer

                    if (type.equals("H")) {
                        System.out.println("Property Address: ");
                        String address = scanner.nextLine();
                        System.out.println("Property Value: ");
                        int value = scanner.nextInt();

                        HomeLoan HomeLoan = new HomeLoan(loanId, customer, principal, rate, term, "HomeLoan", "Pending", address, value);
                        try {
                            repo.applyLoan(HomeLoan);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                    } else if (type.equals("C")) {
                        System.out.println("Car Model: ");
                        String model = scanner.nextLine();
                        System.out.println("Car Value: ");
                        int value = scanner.nextInt();

                        CarLoan carLoan = new CarLoan(loanId, customer, principal, rate, term, "CarLoan", "Pending", model, value);
                        try {
                            repo.applyLoan(carLoan);
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                    } else {
                        System.out.println("Invalid loan type.");
                    }
                    break;

                case 2:
                    try {
                        repo.getAllLoan();
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("Enter Loan ID: ");
                    int id = scanner.nextInt();
                    try {
                        repo.getLoanById(id);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.println("Enter Loan ID: ");
                    int interestId = scanner.nextInt();
                    try {
                        repo.calculateInterest(interestId);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.println("Enter Loan ID: ");
                    int statusId = scanner.nextInt();
                    try {
                        repo.loanStatus(statusId);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("Enter Loan ID: ");
                    int repayId = scanner.nextInt();
                    System.out.println("Enter Repayment Amount: ");
                    double repayAmount = scanner.nextDouble();
                    try {
                        repo.loanRepayment(repayId, repayAmount);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 7:
                    System.out.println("Exiting Loan Management System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        scanner.close();
    }
}
