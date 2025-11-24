package kg.attractor.java.lesson44.models;

import kg.attractor.java.lesson44.entities.Book;
import kg.attractor.java.lesson44.entities.BookRecord;
import kg.attractor.java.lesson44.entities.User;
import kg.attractor.java.lesson44.utility.JsonUtil;

import java.io.IOException;
import java.util.List;

public class JournalDataModel {
    private List<User> users;
    private List<Book> books;
    private List<BookRecord> records;

    public JournalDataModel(List<User> users, List<Book> books, List<BookRecord> records) {
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
