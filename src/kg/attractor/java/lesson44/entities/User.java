package kg.attractor.java.lesson44.entities;

public class User {
    private String firstName;
    private String lastName;
    private String middleName;
    private int id;
    private boolean emailConfirmed = false;
    private String email;
    private String password;

    public User(String firstName, int id) {
        this(firstName, null, null, null, id);
    }

    public User(String firstName, String lastName, int id) {
        this(firstName, lastName, null, null, id);
    }

    public User(String firstName, String email, String password, int id) {
        this(firstName, null, null, password, id);
        this.email = email;
    }

    public User(String firstName, String lastName, String middleName, String password, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.id = id;
        this.password = password;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("firstName='").append(firstName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
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