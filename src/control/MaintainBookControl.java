// Author: Darren Ong Der Ren

package control;

import adt.LinkedHashMap;
import entity.Book;

public class MaintainBookControl {
    private LinkedHashMap<String, Book> bookMap;

    public MaintainBookControl(LinkedHashMap<String, Book> bookMap) {
        this.bookMap = bookMap;
    }

    public MaintainBookControl() {
        this.bookMap = new LinkedHashMap<>();
    }

    public boolean addNewBook(String bookId, String title, String author) {
        if (bookMap.containsKey(bookId)) return false;
        bookMap.put(bookId, new Book(bookId, title, author));
        return true;
    }

    public Book searchBook(String bookID) {
        return bookMap.get(bookID);
    }

    public Book[] searchBooks(String keyword) {
        Object[] allObj = bookMap.toArray();
        int matchCount  = 0;
        for (Object obj : allObj) {
            Book b = (Book) obj;
            if (matches(b, keyword)) matchCount++;
        }
        Book[] results = new Book[matchCount];
        int index      = 0;
        for (Object obj : allObj) {
            Book b = (Book) obj;
            if (matches(b, keyword)) results[index++] = b;
        }
        return results;
    }

    public Book[] getSortedBooks(boolean ascending) {
        Object[] allObj = bookMap.toArray();
        Book[] allBooks = new Book[allObj.length];
        for (int i = 0; i < allObj.length; i++) allBooks[i] = (Book) allObj[i];
        bookMap.mergeSort(allBooks, 0, allBooks.length - 1, (b1, b2) -> ascending
                ? b1.getTitle().compareToIgnoreCase(b2.getTitle())
                : b2.getTitle().compareToIgnoreCase(b1.getTitle()));
        return allBooks;
    }

    public Book getLatestBook() {
        return bookMap.getLatest();
    }

    public boolean isBookMapEmpty() {
        return bookMap.isEmpty();
    }

    public void printTable() {
        bookMap.printTableFormat();
    }

    public boolean updateBookStatus(String bookID, String newStatus) {
        Book book = bookMap.get(bookID);
        if (book == null) return false;
        book.setStatus(newStatus);
        return true;
    }

    public boolean removeBook(String bookID) {
        return bookMap.remove(bookID) != null;
    }

    public Book undoLastRemoval() {
        return bookMap.undo() ? bookMap.getLatest() : null;
    }

    public LinkedHashMap<String, Book> getBookMap() {
        return bookMap;
    }

    /**
     * Returns an array of all books that are currently in "Borrowed" status.
     * Used by admin to see who is borrowing what at a glance.
     *
     * @return Book[] containing only books with status "Borrowed"
     */
    public Book[] getCurrentBorrowReport() {
        Object[] allObj = bookMap.toArray();
        int count = 0;
        for (Object obj : allObj) {
            if (((Book) obj).getStatus().equalsIgnoreCase("Borrowed")) count++;
        }
        Book[] report = new Book[count];
        int i = 0;
        for (Object obj : allObj) {
            Book b = (Book) obj;
            if (b.getStatus().equalsIgnoreCase("Borrowed")) report[i++] = b;
        }
        return report;
    }

    private boolean matches(Book b, String keyword) {
        String kw = keyword.toLowerCase();
        return b.getTitle().toLowerCase().contains(kw)
            || b.getAuthor().toLowerCase().contains(kw);
    }
}