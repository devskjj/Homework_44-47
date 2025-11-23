package kg.attractor.java.lesson44;

import kg.attractor.java.lesson44.entities.User;
import kg.attractor.java.lesson44.models.JournalDataModel;
import kg.attractor.java.lesson44.utility.JsonUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SampleDataModel {
    private User user = new User("Apache", "Lio", 1);
    private LocalDateTime currentDateTime = LocalDateTime.now();
    private List<User> customers = new ArrayList<>();

    public SampleDataModel() {
        try {
            JournalDataModel data = JsonUtil.load("data.json");
            List<User> users = data.getUsers();
            customers.add(users.get(0));
            customers.add(users.get(1));
            customers.add(users.get(2));
            customers.add(users.get(3));
        } catch (IOException e) {
            e.printStackTrace();
        }



//        customers.add(new User("Marco", 2));
//        customers.add(new User("Winston", 3));
//        customers.add(new User("Amos", "Burton", 4));
        customers.get(1).setEmailConfirmed(true);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(LocalDateTime currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public List<User> getCustomers() {
        return customers;
    }

    public void setCustomers(List<User> customers) {
        this.customers = customers;
    }
}
