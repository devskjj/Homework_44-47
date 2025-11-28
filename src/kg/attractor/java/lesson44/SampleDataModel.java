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
    private transient User user;
    private transient Book book;
    private transient LocalDateTime currentDateTime = LocalDateTime.now();
    private  List<User> users = new ArrayList<>();
    private  List<Book> books = new ArrayList<>();
    private  List<BookRecord> records = new ArrayList<>();

    public SampleDataModel() {
        loadData();
    }

    public  void loadData() {
        if (users.isEmpty() || books.isEmpty() || records.isEmpty()) {
            try {
                JournalDataModel data = JsonUtil.load("data.json");
                books.addAll(data.getBooks());
                records.addAll(data.getRecords());
                users.addAll(data.getUsers());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser() {
        return user;
    }

    public User getUserById(int id) {
        if (id <= 0 || id > users.size()) {
            return null;
        }
        return users.get(id - 1);
    }

    public void setUser(int id) {
        if (id - 1 < users.size()) {
            this.user = users.get(id - 1);
        }
    }

    public Book getBook() {
        return book;
    }

    public Book getBookById(int id) {
        if (id <= 0 || id > books.size()) {
            return null;
        }
        return books.get(id - 1);
    }

    public void setBook(int id) {
        if (id - 1 < books.size()) {
            this.book = books.get(id - 1);
        }
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

    public  List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
