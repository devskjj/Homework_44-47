package kg.attractor.java.lesson44.entities;

public class Record {
    private final int recordId;
    private int bookId;
    private int userId;
    private String takeDate;
    private String returnDate;

    public Record(int recordId, int bookId, int userId, String takeDate, String returnDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.userId = userId;
        this.takeDate = takeDate;
        this.returnDate = returnDate;
    }
}
