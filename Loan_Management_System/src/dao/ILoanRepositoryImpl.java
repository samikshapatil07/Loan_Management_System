package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import entity.Loan;
import entity.HomeLoan;
import entity.CarLoan;
import entity.Customer;

import exception.InvalidLoanException;

import util.DBConnUtil;

public class ILoanRepositoryImpl implements ILoanRepository {

    private Connection getConnection() throws Exception {
        return DBConnUtil.getDBConn(); // Utility method to get DB connection
    }

    /*--------------------------applyLoan----------------------------------------------------------------*/
    @Override
    public void applyLoan(Loan loan) throws Exception {
        try (Connection conn = getConnection()) {

            // Insert into Loan table
            String loanSql = "INSERT INTO Loan (loanId, customerId, principalAmount, interestRate, loanTerm, loanType, loanStatus) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(loanSql)) {
                ps.setInt(1, loan.getLoanId());
                ps.setInt(2, loan.getCustomer().getCustomerId());
                ps.setDouble(3, loan.getPrincipalAmount());
                ps.setDouble(4, loan.getInterestRate());
                ps.setInt(5, loan.getLoanTerm());
                ps.setString(6, loan.getLoanType());
                ps.setString(7, "Pending");
                ps.executeUpdate();
            }

            // Handle HomeLoan
            if (loan instanceof HomeLoan) {
                HomeLoan hl = (HomeLoan) loan;
                String sql = "INSERT INTO HomeLoan (loanId, propertyAddress, propertyValue) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, hl.getLoanId());
                    ps.setString(2, hl.getPropertyAddress());
                    ps.setInt(3, hl.getPropertyValue());
                    ps.executeUpdate();
                }

            // Handle CarLoan
            } else if (loan instanceof CarLoan) {
                CarLoan cl = (CarLoan) loan;
                String sql = "INSERT INTO CarLoan (loanId, carModel, carValue) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, cl.getLoanId());
                    ps.setString(2, cl.getCarModel());
                    ps.setInt(3, cl.getCarValue());
                    ps.executeUpdate();
                }
            }

            System.out.println("Loan applied successfully and is in Pending status.");
        }
    }

    /*--------------------------calculateInterest----------------------------------------------------------------*/
    @Override
    public double calculateInterest(int loanId) throws Exception {
        double interest = 0;

        try (Connection conn = getConnection()) {
            String query = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, loanId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new InvalidLoanException("Loan not found for ID: " + loanId);
                }

                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate");
                int term = rs.getInt("loanTerm");

                interest = (principal * rate * term) / (12 * 100);
            }
        }

        System.out.println("Calculated Interest for Loan ID " + loanId + ": " + interest);
        return interest;
    }

    /*--------------------------loanStatus----------------------------------------------------------------*/
    @Override
    public String loanStatus(int loanId) throws Exception {
        String status;

        try (Connection conn = getConnection()) {
            String query = "SELECT L.loanId, C.creditScore FROM Loan L " +
                           "JOIN Customer C ON L.customerId = C.customerId " +
                           "WHERE L.loanId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, loanId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new InvalidLoanException("Loan ID not found: " + loanId);
                }

                int creditScore = rs.getInt("creditScore");

                if (creditScore > 650) {
                    status = "Approved";
                } else {
                    status = "Rejected";
                }

                // Update loan status in DB
                String updateQuery = "UPDATE Loan SET loanStatus = ? WHERE loanId = ?";
                try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
                    updatePs.setString(1, status);
                    updatePs.setInt(2, loanId);
                    updatePs.executeUpdate();
                }
            }
        }

        System.out.println("Loan ID " + loanId + " is now " + status);
        return status;
    }

    /*--------------------------calculateEMI----------------------------------------------------------------*/
    @Override
    public double calculateEMI(int loanId) throws Exception {
        double emi;

        try (Connection conn = getConnection()) {
            String query = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, loanId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new InvalidLoanException("Loan not found for ID: " + loanId);
                }

                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate") / 12 / 100;
                int tenure = rs.getInt("loanTerm");

                emi = (principal * rate * Math.pow(1 + rate, tenure)) /
                      (Math.pow(1 + rate, tenure) - 1);
            }
        }

        System.out.println("EMI for Loan ID " + loanId + ": " + emi);
        return emi;
    }

    @Override
    public double calculateEMI(double principal, double rate, int tenure) {
        double monthlyRate = rate / 12 / 100;
        return (principal * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
               (Math.pow(1 + monthlyRate, tenure) - 1);
    }

    /*--------------------------loanRepayment----------------------------------------------------------------*/
    @Override
    public void loanRepayment(int loanId, double amount) throws Exception {
        try (Connection conn = getConnection()) {
            String query = "SELECT principalAmount, interestRate, loanTerm FROM Loan WHERE loanId = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, loanId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new InvalidLoanException("Loan not found for ID: " + loanId);
                }

                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate");
                int tenure = rs.getInt("loanTerm");

                double emi = calculateEMI(principal, rate, tenure);

                if (amount < emi) {
                    System.out.println("Repayment failed: Amount is less than single EMI (" + emi + ")");
                } else {
                    int emiCount = (int) (amount / emi);
                    System.out.println("Repayment successful. You have paid " + emiCount + " EMI(s).");
                }
            }
        }
    }

    /*--------------------------getAllLoan----------------------------------------------------------------*/
    @Override
    public List<Loan> getAllLoan() throws Exception {
        List<Loan> loanList = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String loanQuery = "SELECT * FROM Loan";
            try (PreparedStatement ps = conn.prepareStatement(loanQuery)) {
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    int loanId = rs.getInt("loanId");
                    int customerId = rs.getInt("customerId");
                    double principal = rs.getDouble("principalAmount");
                    double rate = rs.getDouble("interestRate");
                    int term = rs.getInt("loanTerm");
                    String type = rs.getString("loanType");
                    String status = rs.getString("loanStatus");

                    Customer customer = getCustomerById(customerId, conn);
                    Loan loan;

                    if ("HomeLoan".equalsIgnoreCase(type)) {
                        String homeQuery = "SELECT * FROM HomeLoan WHERE loanId = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(homeQuery)) {
                            ps2.setInt(1, loanId);
                            ResultSet rs2 = ps2.executeQuery();
                            if (rs2.next()) {
                                String address = rs2.getString("propertyAddress");
                                int value = rs2.getInt("propertyValue");
                                loan = new HomeLoan(loanId, customer, principal, rate, term, type, status, address, value);
                            } else continue;
                        }
                    } else if ("CarLoan".equalsIgnoreCase(type)) {
                        String carQuery = "SELECT * FROM CarLoan WHERE loanId = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(carQuery)) {
                            ps2.setInt(1, loanId);
                            ResultSet rs2 = ps2.executeQuery();
                            if (rs2.next()) {
                                String model = rs2.getString("carModel");
                                int value = rs2.getInt("carValue");
                                loan = new CarLoan(loanId, customer, principal, rate, term, type, status, model, value);
                            } else continue;
                        }
                    } else {
                        loan = new Loan(loanId, customer, principal, rate, term, type, status);
                    }

                    loanList.add(loan);
                }
            }
        }

        for (Loan loan : loanList) {
            System.out.println(loan);
        }

        return loanList;
    }

    /*--------------------------getLoanById----------------------------------------------------------------*/
    @Override
    public Loan getLoanById(int loanId) throws Exception {
        Loan loan = null;

        try (Connection conn = getConnection()) {
            String loanQuery = "SELECT * FROM Loan WHERE loanId = ?";
            try (PreparedStatement ps = conn.prepareStatement(loanQuery)) {
                ps.setInt(1, loanId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    throw new InvalidLoanException("Loan not found for ID: " + loanId);
                }

                int customerId = rs.getInt("customerId");
                double principal = rs.getDouble("principalAmount");
                double rate = rs.getDouble("interestRate");
                int term = rs.getInt("loanTerm");
                String type = rs.getString("loanType");
                String status = rs.getString("loanStatus");

                Customer customer = getCustomerById(customerId, conn);

                if ("HomeLoan".equalsIgnoreCase(type)) {
                    String homeQuery = "SELECT * FROM HomeLoan WHERE loanId = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(homeQuery)) {
                        ps2.setInt(1, loanId);
                        ResultSet rs2 = ps2.executeQuery();
                        if (rs2.next()) {
                            String address = rs2.getString("propertyAddress");
                            int value = rs2.getInt("propertyValue");
                            loan = new HomeLoan(loanId, customer, principal, rate, term, type, status, address, value);
                        }
                    }
                } else if ("CarLoan".equalsIgnoreCase(type)) {
                    String carQuery = "SELECT * FROM CarLoan WHERE loanId = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(carQuery)) {
                        ps2.setInt(1, loanId);
                        ResultSet rs2 = ps2.executeQuery();
                        if (rs2.next()) {
                            String model = rs2.getString("carModel");
                            int value = rs2.getInt("carValue");
                            loan = new CarLoan(loanId, customer, principal, rate, term, type, status, model, value);
                        }
                    }
                } else {
                    loan = new Loan(loanId, customer, principal, rate, term, type, status);
                }
            }
        }

        System.out.println(loan);
        return loan;
    }

    private Customer getCustomerById(int customerId, Connection conn) throws SQLException {
        String query = "SELECT * FROM Customer WHERE customerId = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getInt("customerId"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getInt("creditScore")
                );
            }
        }
        return null;
    }
}
