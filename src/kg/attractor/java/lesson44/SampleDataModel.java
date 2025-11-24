package kg.attractor.java.lesson44;

import kg.attractor.java.lesson44.entities.Book;
import kg.attractor.java.lesson44.entities.BookRecord;
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
    private List<Book> books = new ArrayList<>();
    private List<BookRecord> records = new ArrayList<>();

    public SampleDataModel() {
        try {
            JournalDataModel data = JsonUtil.load("data.json");
            List<Book> bookData = data.getBooks();
            books.addAll(bookData);

            List<BookRecord> recordsData = data.getRecords();
            records.addAll(recordsData);

            List<User> users = data.getUsers();
            customers.addAll(users);
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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<BookRecord> getRecords() {
        return records;
    }

    public void setRecords(List<BookRecord> records) {
        this.records = records;
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
