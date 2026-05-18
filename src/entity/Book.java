package entity;

import adt.*;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String status;
    private User borrowed;
    private LinkedHashMap<String, User> waitList;
    

    public Book(String bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.status = "Available";
        this.borrowed = null;
        this.waitList = new LinkedHashMap<>();
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBorrowed(User user) {
        this.borrowed = user;
    }

    public void addToWaitList(User user) {
        if(user!=null){
            waitList.put(user.getUserId(), user);
        }
    }
    
    public boolean isUserInWaitlist(String userId){
        return waitList.containsKey(userId);
    }
    public User peekWaitlist() {
        return waitList.peek();
    }

    public User dequeueWaitlist() {
        if (waitList.isEmpty()) {
            return null;
        }
        return waitList.dequeue(); 
    } 
    
    public boolean isWaitListEmpty() {
        return waitList.isEmpty();
    }

    public User getBorrowed() {
        return borrowed;
    }

    public LinkedHashMap<String, User> getWaitList() {
        return waitList;
    }
    
    @Override
    public String toString() {
        return String.format("| %-10s | %-30s | %-20s | %-16s |", bookId, title, author, status);
    }
}