package domain;

public class Customer {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String customerType; // Student, Rentern, VIP etc.

    public Customer(Long id, String firstName, String lastName, String customerType){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerType = customerType;
    }

    // Getter
    public long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getCustomerType() {
        return customerType;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
