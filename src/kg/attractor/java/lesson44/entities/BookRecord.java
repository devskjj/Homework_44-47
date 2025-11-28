package kg.attractor.java.lesson44.entities;

public class BookRecord {
    private final int recordId;
    private int bookId;
    private int userId;
    private String takeDate;
    private String returnDate;

    public BookRecord(int recordId, int bookId, int userId, String takeDate, String returnDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.userId = userId;
        this.takeDate = takeDate;
        this.returnDate = returnDate;
    }

    public int getRecordId() {
        return recordId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTakeDate() {
        return takeDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setTakeDate(String takeDate) {
        this.takeDate = takeDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
