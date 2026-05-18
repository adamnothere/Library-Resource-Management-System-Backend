package entity;

import java.time.LocalDate;

public class BorrowRecord {
    private String recordId;       
    private String bookId;
    private String bookTitle;
    private String bookAuthor;
    private LocalDate borrowDate;
    private LocalDate returnDate;  
    private String status;
    public static final int DUE_DAYS = 14;
    private LocalDate dueDate;        

    public BorrowRecord(String recordId, String bookId, String bookTitle, String bookAuthor) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.borrowDate = LocalDate.now();
        this.returnDate = null;
        this.status = "Borrowing";
        this.dueDate = this.borrowDate.plusDays(DUE_DAYS);
    }

    public BorrowRecord(String recordId, String bookId, String bookTitle, String bookAuthor,
                        LocalDate borrowDate, LocalDate returnDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = (returnDate == null) ? "Borrowing" : "Returned";
        this.dueDate = this.borrowDate.plusDays(DUE_DAYS);
    }

    public void markReturned() {
        this.returnDate = LocalDate.now();
        this.status = "Returned";
    }

    public String getRecordId() { 
        return recordId; 
    }
   
    public String getBookId() { 
        return bookId; 
    }
   
    public String getBookTitle() {
         return bookTitle; 
    }
   
    public String getBookAuthor() {
        return bookAuthor; 
    }
   
    public LocalDate getBorrowDate() { 
        return borrowDate;
    }
   
    public LocalDate getReturnDate() { 
        return returnDate; 
    }
   
    public String getStatus() { 
        return status; 
    }

    public LocalDate getDueDate() { 
        return dueDate; 
    }

    @Override
    public String toString() {
        String ret = (returnDate != null) ? returnDate.toString() : "---";
        String icon = status.equals("Borrowing") ? "[~] Borrowing" : "[V] Returned ";
        String title = bookTitle.length() > 20 ? bookTitle.substring(0, 17) + "..." : bookTitle;
        return String.format("| %-6s | %-20s | %-10s | %-10s | %-14s |",
                bookId, title, borrowDate.toString(), ret, icon);
    }
}