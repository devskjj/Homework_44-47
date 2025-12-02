package kg.attractor.java.booklender.entities;

public class Book {
    private String name;
    private String description;
    private String author;
    private int id;
    private boolean isAvailable;

    public Book(String name, String description, String author, int id, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.id = id;
        this.isAvailable = isAvailable;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getId() {
        return id;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
