package entity;

import adt.LinkedHashMap;
import java.time.LocalDate;
import utility.ConsoleUtil;

public class User {

    private String userId;
    private String username;
    private String email;
    private String password;
    private String role;
    private LocalDate registrationDate;

    private LinkedHashMap<String, BorrowRecord> borrowRecords;
    private LinkedHashMap<String, BorrowRecord> activeRecord; 
    private LinkedHashMap<String, FineRecord>   fineRecords;    

    private int activeBorrowCount = 0;

    private static final int LIMIT_STAFF   = 5;
    private static final int LIMIT_STUDENT = 3;

    public User(String userId, String username, String email, String password, String role) {
        this.userId = userId;
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setRole(role);
        this.registrationDate = LocalDate.now();
        this.borrowRecords    = new LinkedHashMap<>();
        this.activeRecord     = new LinkedHashMap<>();
        this.fineRecords      = new LinkedHashMap<>();  
    }
    
    public User(String userId, String username, String email, String password, String role, LocalDate date) {
        this.userId = userId;
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setRole(role);
        this.registrationDate = date;
        this.borrowRecords    = new LinkedHashMap<>();
        this.activeRecord     = new LinkedHashMap<>();
        this.fineRecords      = new LinkedHashMap<>();  
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 5) {
            throw new IllegalArgumentException("Password must be at least 5 characters.");
        }
        this.password = password;
    }

    public String getUserId() {
        return userId; 
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    public String getRole() {
        return role;
    }
    
    public void setRole(String role){
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        
        String lowerRole = role.toLowerCase();
        if (lowerRole.equals("student") || lowerRole.equals("staff")) {
            this.role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
        } else if (lowerRole.equals("admin")) {
            this.role = "Admin";
        } else {
            throw new IllegalArgumentException("Invalid role assigned: " + role);
        }
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public int getBorrowLimit() {
        return role.equalsIgnoreCase("Staff") ? LIMIT_STAFF : LIMIT_STUDENT;
    }

    public int getActiveBorrowCount() { 
        return activeBorrowCount; 
    }

    public int getRemainingBorrowSlots() { 
        return getBorrowLimit() - activeBorrowCount; 
    }

    public boolean canBorrow() { 
        return activeBorrowCount < getBorrowLimit(); 
    }

    public void addBorrowRecordTracked(String bookId, String bookTitle, String bookAuthor) {
        String recordId = userId + "_" + bookId + "_" + LocalDate.now().toString();
        BorrowRecord record = new BorrowRecord(recordId, bookId, bookTitle, bookAuthor);
        borrowRecords.put(recordId, record);
        activeRecord.put(bookId, record);
        activeBorrowCount++;
    }

    public void markBookReturned(String bookId) {
        BorrowRecord record = activeRecord.get(bookId);
        if (record != null) {
            record.markReturned();
            activeRecord.remove(bookId);
            activeBorrowCount--;
        }
    }

    public void seedBorrowRecord(String bookId, String bookTitle, String bookAuthor,
                                 LocalDate borrowDate, LocalDate returnDate) {
        String recordId = userId + "_" + bookId + "_" + borrowDate.toString();
        BorrowRecord record = new BorrowRecord(recordId, bookId, bookTitle, bookAuthor,
                                               borrowDate, returnDate);
        borrowRecords.put(recordId, record);
        if (returnDate == null) {
            activeRecord.put(bookId, record);
            activeBorrowCount++;
        }
    }

    public LinkedHashMap<String, BorrowRecord> getBorrowRecords() { 
        return borrowRecords; 
    }

    public boolean hasBorrowRecords() { 
        return !borrowRecords.isEmpty(); 
    }

    public void addFineRecord(FineRecord fine) {
        fineRecords.put(fine.getFineId(), fine);
    }
 
    public boolean hasUnpaidFines() {
        if (fineRecords.isEmpty()) return false;
        Object[] arr = fineRecords.toArray();
        for (Object obj : arr) {
            if (!((FineRecord) obj).isPaid()) return true;
        }
        return false;
    }

    public double getTotalUnpaidFines() {
        double total = 0;
        Object[] arr = fineRecords.toArray();
        for (Object obj : arr) {
            FineRecord f = (FineRecord) obj;
            if (!f.isPaid()) total += f.getFineAmount();
        }
        return total;
    }

    public LinkedHashMap<String, BorrowRecord> getActiveRecord()  { return activeRecord; } 
    
    public LinkedHashMap<String, FineRecord>   getFineRecords()   { return fineRecords; } 
    
    public boolean hasFineRecords()   { return !fineRecords.isEmpty(); }  

}