package kg.attractor.java.booklender.models;

import kg.attractor.java.booklender.entities.Book;
import kg.attractor.java.booklender.entities.BookRecord;
import kg.attractor.java.booklender.entities.User;

import java.util.List;

public class JsonDataClass {
    private List<User> users;
    private List<Book> books;
    private List<BookRecord> records;

    public JsonDataClass(List<User> users, List<Book> books, List<BookRecord> records) {
        this.users = users;
        this.books = books;
        this.records = records;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<BookRecord> getRecords() {
        return records;
    }
}
