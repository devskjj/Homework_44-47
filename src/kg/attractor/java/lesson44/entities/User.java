package kg.attractor.java.lesson44.entities;

public class User {
    private String firstName;
    private String lastName;
    private String middleName;
    private final int id;
    private boolean emailConfirmed = false;
    private String email;
    private int password;

    public User(String firstName, int id) {
        this(firstName, null, null, id);
    }

    public User(String firstName, String lastName, int id) {
        this(firstName, lastName, null, id);
    }

    public User(String firstName, String lastName, String middleName, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = firstName + "@test.mail";
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }
}