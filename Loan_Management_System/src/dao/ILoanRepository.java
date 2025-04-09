package dao;

import entity.Loan;
import java.util.List;

public interface ILoanRepository {
    // a. Apply for a loan
    void applyLoan(Loan loan) throws Exception;

    // b. Calculate interest using loanId
    double calculateInterest(int loanId) throws Exception;

    // b. Overloaded: Calculate interest using parameters
    public default double calculateInterest(double principal, double rate, int term) {
        return (principal * rate * term) / (12 * 100);
    }
    // c. Update and return loan status based on credit score
    String loanStatus(int loanId) throws Exception;

    // d. Calculate EMI using loanId
    double calculateEMI(int loanId) throws Exception;

    // d. Overloaded: Calculate EMI using parameters
    double calculateEMI(double principal, double rate, int tenure);

    // e. Loan repayment
    void loanRepayment(int loanId, double amount) throws Exception;

    // f. Get all loans
    List<Loan> getAllLoan() throws Exception;

    // g. Get loan by ID
    Loan getLoanById(int loanId) throws Exception;
}

