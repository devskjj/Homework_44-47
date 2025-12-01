package kg.attractor.java.booklender.models;

import kg.attractor.java.booklender.entities.Book;
import kg.attractor.java.booklender.entities.BookRecord;
import kg.attractor.java.booklender.entities.User;
import kg.attractor.java.booklender.utility.JsonUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private transient User user;
    private transient Book book;
    private transient LocalDateTime currentDateTime = LocalDateTime.now();
    private List<User> users = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
    private List<BookRecord> records = new ArrayList<>();

    public DataModel() {
        loadData();
    }

    public void loadData() {
        if (users.isEmpty() || books.isEmpty() || records.isEmpty()) {
            try {
                JsonDataClass data = JsonUtil.load("data.json");
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

    public void takeBook(int id, int bookId) {
        if (!books.get(bookId - 1).isAvailable()) return;

        records.add(new BookRecord(records.size() + 1, bookId, id,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                null));
        books.get(bookId - 1).setAvailable(false);
    }

    public void returnBook(int id, int bookId) {
        if (books.get(bookId - 1).isAvailable()) return;
        records.stream()
                .filter(b -> b.getReturnDate() == null)
                .filter(b -> b.getBookId() == bookId)
                .filter(b -> b.getUserId() == id)
                .findFirst().ifPresent(b -> b.setReturnDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        books.get(bookId - 1).setAvailable(true);
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
