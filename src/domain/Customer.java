package domain;

public class Customer {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final String customerType;

    public Customer(long id, String firstName, String lastName, String customerType) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID muss größer als 0 sein.");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Vorname darf nicht leer sein.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nachname darf nicht leer sein.");
        }
        if (customerType == null) {
            throw new IllegalArgumentException("CustomerType darf nicht null sein.");
        }

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerType = customerType;
    }

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