// Author: Elisha Loh Tien Rong

package control;

import adt.LinkedHashMap;
import entity.Book;
import entity.BorrowRecord;
import entity.User;

public class ReportControl {

    private LinkedHashMap<String, Book> bookMap;
    private LinkedHashMap<String, User> userMap;

    public ReportControl(MaintainBookControl bookControl,
                         LinkedHashMap<String, User> userMap) {
        this.bookMap = bookControl.getBookMap();
        this.userMap = userMap;
    }

    public static class BookStat {
        public String bookId;
        public String title;
        public String author;
        public String status;
        public int    totalBorrows;

        public BookStat(String bookId, String title, String author,
                        String status, int totalBorrows) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.status = status;
            this.totalBorrows = totalBorrows;
        }
    }

    public static class UserStat {
        public String userId;
        public String username;
        public String role;
        public int activeBorrows;
        public int totalBorrows;
        public int totalFines;

        public UserStat(String userId, String username, String role,
                        int activeBorrows, int totalBorrows, int totalFines) {
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.activeBorrows = activeBorrows;
            this.totalBorrows = totalBorrows;
            this.totalFines = totalFines;
        }
    }

    public static class Summary {
        public int totalBooks;
        public int availableBooks;
        public int borrowedBooks;
        public int reservedBooks;
        public int totalUsers;
        public int totalStudents;
        public int totalStaff;
        public int totalBorrowRecords;
        public int activeBorrowRecords;
    }

    public static class OverdueStat {
        public String bookId;
        public String bookTitle;
        public String borrowerId;
        public String borrowerName;
        public java.time.LocalDate dueDate;
        public long daysOverdue;

        public OverdueStat(String bookId, String bookTitle,
                           String borrowerId, String borrowerName,
                           java.time.LocalDate dueDate, long daysOverdue) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.borrowerId = borrowerId;
            this.borrowerName = borrowerName;
            this.dueDate = dueDate;
            this.daysOverdue = daysOverdue;
        }
    }

    public Summary getSummary() {
        Summary s = new Summary();

        if (!bookMap.isEmpty()) {
            Object[] books = bookMap.toArray();
            s.totalBooks = books.length;
            for (Object obj : books) {
                Book b = (Book) obj;
                if      (b.getStatus().equalsIgnoreCase("Available")) s.availableBooks++;
                else if (b.getStatus().equalsIgnoreCase("Borrowed"))  s.borrowedBooks++;
                else if (b.getStatus().equalsIgnoreCase("Reserved"))  s.reservedBooks++;
            }
        }

        if (!userMap.isEmpty()) {
            Object[] users = userMap.toArray();
            s.totalUsers = userMap.size();
            for (Object obj : users) {
                User u = (User) obj;
                if      (u.getRole().equalsIgnoreCase("Student")) s.totalStudents++;
                else if (u.getRole().equalsIgnoreCase("Staff"))   s.totalStaff++;

                if (!u.getBorrowRecords().isEmpty()) {
                    s.totalBorrowRecords += u.getBorrowRecords().size();
                    Object[] records = u.getBorrowRecords().toArray();
                    for (Object r : records) {
                        BorrowRecord br = (BorrowRecord) r;
                        if (br.getStatus().equals("Borrowing")) s.activeBorrowRecords++;
                    }
                }
            }
        }
        return s;
    }

    public BookStat[] getBookPopularity() {
        Object[] books = bookMap.toArray();
        BookStat[] stats = new BookStat[books.length];

        for (int i = 0; i < books.length; i++) {
            Book b   = (Book) books[i];
            int count = countBorrowsForBook(b.getBookId());
            stats[i]  = new BookStat(b.getBookId(), b.getTitle(),
                                     b.getAuthor(), b.getStatus(), count);
        }

        sortBookStats(stats);

        return stats;
    }

    public UserStat[] getActiveBorrowers() {
        Object[] users = userMap.toArray();

        int nonAdminCount = 0;
        for (Object obj : users) {
            if (!((User) obj).getRole().equalsIgnoreCase("Admin")) nonAdminCount++;
        }

        UserStat[] stats = new UserStat[nonAdminCount];
        int idx = 0;

        for (Object obj : users) {
            User u = (User) obj;
            if (u.getRole().equalsIgnoreCase("Admin")) continue;

            int totalBorrows = u.getBorrowRecords().size();
            int fineCount    = u.getFineRecords().size();

            stats[idx++] = new UserStat(u.getUserId(), u.getUsername(), u.getRole(),
                                        u.getActiveBorrowCount(), totalBorrows, fineCount);
        }

        sortUserStats(stats);

        return stats;
    }

    public OverdueStat[] getOverdueBooks() {
        java.time.LocalDate today = java.time.LocalDate.now();

        Object[] users = userMap.toArray();

        int count = 0;
        for (Object obj : users) {
            User u = (User) obj;
            if (u.getBorrowRecords().isEmpty()) continue;
            Object[] records = u.getBorrowRecords().toArray();
            for (Object r : records) {
                BorrowRecord br = (BorrowRecord) r;
                if (br.getStatus().equals("Borrowing") &&
                    br.getDueDate() != null &&
                    today.isAfter(br.getDueDate())) {
                    count++;
                }
            }
        }

        OverdueStat[] stats = new OverdueStat[count];
        int idx = 0;

        for (Object obj : users) {
            User u = (User) obj;
            if (u.getBorrowRecords().isEmpty()) continue;
            Object[] records = u.getBorrowRecords().toArray();
            for (Object r : records) {
                BorrowRecord br = (BorrowRecord) r;
                if (br.getStatus().equals("Borrowing") &&
                    br.getDueDate() != null &&
                    today.isAfter(br.getDueDate())) {
                    long days = java.time.temporal.ChronoUnit.DAYS.between(br.getDueDate(), today);
                    stats[idx++] = new OverdueStat(
                            br.getBookId(), br.getBookTitle(),
                            u.getUserId(), u.getUsername(),
                            br.getDueDate(), days);
                }
            }
        }

        sortOverdueStats(stats);

        return stats;
    }

    private void sortBookStats(BookStat[] arr) {
        for (int i = 1; i < arr.length; i++) {
            BookStat key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].totalBorrows < key.totalBorrows) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private void sortUserStats(UserStat[] arr) {
        for (int i = 1; i < arr.length; i++) {
            UserStat key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].totalBorrows < key.totalBorrows) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private void sortOverdueStats(OverdueStat[] arr) {
        for (int i = 1; i < arr.length; i++) {
            OverdueStat key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].daysOverdue < key.daysOverdue) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private int countBorrowsForBook(String bookId) {
        int count = 0;
        Object[] users = userMap.toArray();
        for (Object obj : users) {
            User u = (User) obj;
            if (u.getBorrowRecords().isEmpty()) continue;
            if (!u.getBorrowRecords().containsKey(
                    u.getUserId() + "_" + bookId + "_")) {
            }
            Object[] records = u.getBorrowRecords().toArray();
            for (Object r : records) {
                BorrowRecord br = (BorrowRecord) r;
                if (br.getBookId().equals(bookId)) count++;
            }
        }
        return count;
    }
}