// Author: Toh Ming Yang

package control;

import adt.LinkedHashMap;
import entity.Book;
import entity.BorrowRecord;
import entity.FineRecord;
import entity.User;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TransactionControl {

    private LinkedHashMap<String, Book> bookMap;
    private LinkedHashMap<String, User> userMap;
    private User currentUser;

    public TransactionControl(MaintainBookControl bookControl,
                              LinkedHashMap<String, User> userMap,
                              User currentUser) {
        this.bookMap = bookControl.getBookMap();
        this.userMap = userMap;
        this.currentUser = currentUser;
    }

    public String borrowBook(String bookId, String userId) {
        Book book = bookMap.get(bookId);
        if (book == null) return "NOT_FOUND";

        User user = resolveUser(userId);
        if (user == null) return "NOT_FOUND";

        if (book.getBorrowed() != null && book.getBorrowed().getUserId().equals(userId))
            return "ALREADY_BORROWED";

        if (!user.canBorrow()) return "LIMIT_REACHED";

        String status = book.getStatus();

        if (status.equalsIgnoreCase("Available")) {
            book.setStatus("Borrowed");
            book.setBorrowed(user);
            user.addBorrowRecordTracked(book.getBookId(), book.getTitle(), book.getAuthor());
            return "BORROWED";
        }

        if (status.equalsIgnoreCase("Reserved")) {
            User firstInLine = book.peekWaitlist();
            if (firstInLine != null && firstInLine.getUserId().equals(userId)) {
                book.dequeueWaitlist();
                book.setStatus("Borrowed");
                book.setBorrowed(user);
                user.addBorrowRecordTracked(book.getBookId(), book.getTitle(), book.getAuthor());
                return "BORROWED";
            }
        }

        if (book.getWaitList().containsKey(userId)) return "DUPLICATE";

        book.addToWaitList(user);
        return "WAITLISTED";
    }

    public String returnBook(String bookId, String userId) {
        Book book = bookMap.get(bookId);
        if (book == null) return "NOT_FOUND";

        if (!book.getStatus().equalsIgnoreCase("Borrowed")) return "NOT_BORROWED";

        if (book.getBorrowed() == null || !book.getBorrowed().getUserId().equals(userId))
            return "NOT_BORROWER";

        User user = resolveUser(userId);

        book.setBorrowed(null);
        if (user != null) user.markBookReturned(bookId);

        if (!book.isWaitListEmpty()) {
            book.setStatus("Reserved");
            return "RETURNED_RESERVED";
        }

        book.setStatus("Available");
        return "RETURNED";
    }

    public FineRecord checkOverdue(String bookId, String userId) {
        User user = resolveUser(userId);
        if (user == null) return null;

        Object[] records = user.getBorrowRecords().toArray();
        BorrowRecord target = null;
        for (Object obj : records) {
            BorrowRecord r = (BorrowRecord) obj;
            if (r.getBookId().equals(bookId) && r.getStatus().equals("Returned")) {
                if (target == null || r.getReturnDate().isAfter(target.getReturnDate())) {
                    target = r;
                }
            }
        }

        if (target == null) return null;

        long daysOverdue = ChronoUnit.DAYS.between(target.getDueDate(), target.getReturnDate());
        if (daysOverdue <= 0) return null;

        String fineId = userId + "_" + bookId + "_" + LocalDate.now().toString();
        FineRecord fine = new FineRecord(fineId, userId, bookId, target.getBookTitle(), (int) daysOverdue);
        user.addFineRecord(fine);
        return fine;
    }

    public User notifyNextInWaitlist(String bookId) {
        Book book = bookMap.get(bookId);
        if (book != null && !book.isWaitListEmpty()) {
            return book.peekWaitlist();
        }
        return null;
    }

    public Book searchBook(String bookId) {
        return bookMap.get(bookId);
    }

    private User resolveUser(String userId) {
        return (userMap != null) ? userMap.get(userId) : currentUser;
    }
}