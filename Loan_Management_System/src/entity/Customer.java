package entity;

public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private int creditScore;

    // Default constructor
    public Customer() {}

    // Parameterized constructor
    public Customer(int customerId, String name, String email, String phone, String address, int creditScore) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.creditScore = creditScore;
    }

    // Getters and setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }

    @Override
    public String toString() {
        return "Customer ID: " + customerId + ", Name: " + name + ", Email: " + email +
               ", Phone: " + phone + ", Address: " + address + ", Credit Score: " + creditScore;
    }
}
