package entity;

import java.time.LocalDate;

public class FineRecord {
    private String fineId;
    private String userId;
    private String bookId;
    private String bookTitle;
    private int daysOverdue;
    private double fineAmount;
    private LocalDate issuedDate;
    private boolean paid;

    public static final double RATE_PER_DAY = 0.50;

    public FineRecord(String fineId, String userId, String bookId, String bookTitle, int daysOverdue) {
        this.fineId = fineId;
        this.userId = userId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.daysOverdue = daysOverdue;
        this.fineAmount = daysOverdue * RATE_PER_DAY;
        this.issuedDate = LocalDate.now();
        this.paid = false;
    }

    public String getFineId() { 
        return fineId; 
    }

    public String getUserId() { 
        return userId; 
    }

    public String getBookId() { 
        return bookId; 
    }

    public String getBookTitle() {
        return bookTitle; 
    }

    public int getDaysOverdue() { 
        return daysOverdue; 
    }
    
    public double getFineAmount() { 
        return fineAmount; 
    }
    
    public LocalDate getIssuedDate() {
        return issuedDate; 
    }
   
    public boolean isPaid() { 
        return paid; 
    }
   
    public void markPaid() { 
        this.paid = true; 
    }

    @Override
    public String toString() {
        String title  = bookTitle.length() > 20 ? bookTitle.substring(0, 17) + "..." : bookTitle;
        String status = paid ? "[V] Paid   " : "[!] Unpaid ";
        return String.format("| %-8s | %-20s | %3d days | RM%-7.2f | %-11s |",
                bookId, title, daysOverdue, fineAmount, status);
    }
}