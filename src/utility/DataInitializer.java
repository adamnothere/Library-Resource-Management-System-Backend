package utility;

import adt.LinkedHashMap;
import entity.*;
import java.time.LocalDate;

public class DataInitializer {

    public static void loadDummyData(LinkedHashMap<String, User> map) {
        // ── Admins ────────────────────────────────────────────────────────────
        User admin      = new User("000000", "Alice",   "alice@system.com",            "admin123",   "Admin", LocalDate.of(2026, 1, 1));

        // ── Staff ─────────────────────────────────────────────────────────────
        User staff_1    = new User("261001", "Bob",     "bob@tarumt.edu.my",           "bob123",     "Staff", LocalDate.of(2026, 4, 3));
        User staff_2    = new User("111111", "David",   "david@tarumt.edu.my",         "111111",     "Staff", LocalDate.of(2026, 4, 3));
        User staff_3    = new User("261002", "Fiona",   "fiona@tarumt.edu.my",         "fiona123",   "Staff", LocalDate.of(2026, 4, 3));
        User staff_4    = new User("261003", "George",  "george@tarumt.edu.my",        "george123",  "Staff");

        // ── Students ──────────────────────────────────────────────────────────
        User student_1  = new User("260001", "Charles", "charles@student.tarumt.edu.my", "charles123", "Student");
        User student_2  = new User("222222", "Emma",    "emma@student.tarumt.edu.my",    "222222",     "Student");
        User student_3  = new User("260002", "Hannah",  "hannah@student.tarumt.edu.my",  "hannah123",  "Student");
        User student_4  = new User("260003", "Ivan",    "ivan@student.tarumt.edu.my",    "ivan123",    "Student");
        User student_5  = new User("260004", "Jenny",   "jenny@student.tarumt.edu.my",   "jenny123",   "Student");
        User student_6  = new User("260005", "Kevin",   "kevin@student.tarumt.edu.my",   "kevin123",   "Student");

        map.put(admin.getUserId(),     admin);
        map.put(staff_1.getUserId(),   staff_1);
        map.put(staff_2.getUserId(),   staff_2);
        map.put(staff_3.getUserId(),   staff_3);
        map.put(staff_4.getUserId(),   staff_4);
        map.put(student_1.getUserId(), student_1);
        map.put(student_2.getUserId(), student_2);
        map.put(student_3.getUserId(), student_3);
        map.put(student_4.getUserId(), student_4);
        map.put(student_5.getUserId(), student_5);
        map.put(student_6.getUserId(), student_6);

        // ── Seed Borrow Records ───────────────────────────────────────────────
        // Rule: only ONE active (null returnDate) record per book at any time

        // Bob (Staff) — 2 returned, 1 active (B1003)
        staff_1.seedBorrowRecord("B1001", "Harry Potter",          "JK",                LocalDate.of(2026, 1,  5), LocalDate.of(2026, 1, 20));
        staff_1.seedBorrowRecord("B1002", "DSA",                   "Thamarai",          LocalDate.of(2026, 2,  1), LocalDate.of(2026, 2, 20));
        staff_1.seedBorrowRecord("B1003", "Formula 1",             "MBS",               LocalDate.of(2026, 3,  2), null);                      // ACTIVE -> B1003 Borrowed

        // Charles (Student) — 1 returned, 1 active (B1002)
        student_1.seedBorrowRecord("B1001", "Harry Potter",        "JK",                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 25));
        student_1.seedBorrowRecord("B1002", "DSA",                 "Thamarai",          LocalDate.of(2026, 3,  25), null);                       // ACTIVE -> B1002 Borrowed

        // David (Staff) — 2 returned, 2 active (B1001, B1004)
        staff_2.seedBorrowRecord("B1005", "The Great Gatsby",      "Fitzgerald",        LocalDate.of(2026, 2, 20), LocalDate.of(2026, 3,  1));
        staff_2.seedBorrowRecord("B1004", "Clean Code",            "Robert Martin",     LocalDate.of(2026, 2,  1), LocalDate.of(2026, 2, 20));
        staff_2.seedBorrowRecord("B1004", "Clean Code",            "Robert Martin",     LocalDate.of(2026, 3, 18), null);                       // ACTIVE -> B1004 Borrowed
        staff_2.seedBorrowRecord("B1001", "Harry Potter",          "JK",                LocalDate.of(2026, 3, 15), null);                       // ACTIVE -> B1001 Borrowed

        // Emma (Student) — 2 returned, 1 active (B1005)
        student_2.seedBorrowRecord("B1001", "Harry Potter",        "JK",                LocalDate.of(2026, 1,  3), LocalDate.of(2026, 1, 18));
        student_2.seedBorrowRecord("B1003", "Formula 1",           "MBS",               LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 28));
        student_2.seedBorrowRecord("B1005", "The Great Gatsby",    "Fitzgerald",        LocalDate.of(2026, 3, 18), null);                       // ACTIVE -> B1005 Borrowed

        // Fiona (Staff) — 4 returned, NO active (books returned, B1006/B1007 now Available)
        staff_3.seedBorrowRecord("B1006", "Atomic Habits",         "James Clear",       LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 28));
        staff_3.seedBorrowRecord("B1007", "The Pragmatic Prog.",   "Hunt & Thomas",     LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2,  5));
        staff_3.seedBorrowRecord("B1002", "DSA",                   "Thamarai",          LocalDate.of(2026, 2, 10), LocalDate.of(2026, 3,  1));
        staff_3.seedBorrowRecord("B1008", "The Alchemist",         "Paulo Coelho",      LocalDate.of(2026, 3, 29), LocalDate.of(2026, 3, 20)); // returned -> B1008 Available

        // George (Staff) — 3 returned, NO active (B1006 now Available)
        staff_4.seedBorrowRecord("B1009", "Rich Dad Poor Dad",     "Kiyosaki",          LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2,  5));
        staff_4.seedBorrowRecord("B1010", "Sapiens",               "Harari",            LocalDate.of(2026, 2, 10), LocalDate.of(2026, 3,  1));
        staff_4.seedBorrowRecord("B1006", "Atomic Habits",         "James Clear",       LocalDate.of(2026, 3,  25), LocalDate.of(2026, 3, 25)); // returned -> B1006 Available

        // Hannah (Student) — 2 returned, 1 active (B1010)
        student_3.seedBorrowRecord("B1007", "The Pragmatic Prog.", "Hunt & Thomas",     LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2,  5));
        student_3.seedBorrowRecord("B1009", "Rich Dad Poor Dad",   "Kiyosaki",          LocalDate.of(2026, 2,  1), LocalDate.of(2026, 2, 25)); // returned — Kevin has active B1009
        student_3.seedBorrowRecord("B1010", "Sapiens",             "Harari",            LocalDate.of(2026, 3,  18), null);                      // ACTIVE -> B1010 Borrowed

        // Ivan (Student) — 2 returned, NO active (B1007 now Available)
        student_4.seedBorrowRecord("B1010", "Sapiens",             "Harari",            LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2,  1));
        student_4.seedBorrowRecord("B1006", "Atomic Habits",       "James Clear",       LocalDate.of(2026, 2,  5), LocalDate.of(2026, 2, 20));
        student_4.seedBorrowRecord("B1007", "The Pragmatic Prog.", "Hunt & Thomas",     LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 10)); // returned -> B1007 Available

        // Jenny (Student) — 1 returned, 1 active (B1008) -- overdue (borrowed Feb 10, due Feb 24)
        student_5.seedBorrowRecord("B1008", "The Alchemist",       "Paulo Coelho",      LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 28));
        student_5.seedBorrowRecord("B1008", "The Alchemist",       "Paulo Coelho",      LocalDate.of(2026, 3, 10), null);                      // ACTIVE overdue -> B1008 Borrowed

        // Kevin (Student) — 2 returned, 1 active (B1009) -- overdue (borrowed Feb 20, due Mar 6)
        student_6.seedBorrowRecord("B1001", "Harry Potter",        "JK",                LocalDate.of(2026, 1,  5), LocalDate.of(2026, 1, 22));
        student_6.seedBorrowRecord("B1002", "DSA",                 "Thamarai",          LocalDate.of(2026, 2,  1), LocalDate.of(2026, 2, 18));
        student_6.seedBorrowRecord("B1009", "Rich Dad Poor Dad",   "Kiyosaki",          LocalDate.of(2026, 3, 20), null);                      // ACTIVE overdue -> B1009 Borrowed

        System.out.println("User Data Loaded Successfully!");
    }

    public static void loadBookData(LinkedHashMap<String, Book> map, LinkedHashMap<String, User> userMap) {
        Book book1  = new Book("B1001", "Harry Potter",             "JK");
        Book book2  = new Book("B1002", "DSA",                      "Thamarai");
        Book book3  = new Book("B1003", "Formula 1",                "MBS");
        Book book4  = new Book("B1004", "Clean Code",               "Robert Martin");
        Book book5  = new Book("B1005", "The Great Gatsby",         "F. Scott Fitzgerald");
        Book book6  = new Book("B1006", "Atomic Habits",            "James Clear");
        Book book7  = new Book("B1007", "The Pragmatic Programmer", "Hunt & Thomas");
        Book book8  = new Book("B1008", "The Alchemist",            "Paulo Coelho");
        Book book9  = new Book("B1009", "Rich Dad Poor Dad",        "Robert Kiyosaki");
        Book book10 = new Book("B1010", "Sapiens",                  "Yuval Noah Harari");

        map.put(book1.getBookId(),  book1);
        map.put(book2.getBookId(),  book2);
        map.put(book3.getBookId(),  book3);
        map.put(book4.getBookId(),  book4);
        map.put(book5.getBookId(),  book5);
        map.put(book6.getBookId(),  book6);
        map.put(book7.getBookId(),  book7);
        map.put(book8.getBookId(),  book8);
        map.put(book9.getBookId(),  book9);
        map.put(book10.getBookId(), book10);

        // ── Borrowed (8 books) ────────────────────────────────────────────────
        book1.setStatus("Borrowed");  book1.setBorrowed(userMap.get("111111")); // David
        book2.setStatus("Borrowed");  book2.setBorrowed(userMap.get("260001")); // Charles
        book3.setStatus("Borrowed");  book3.setBorrowed(userMap.get("261001")); // Bob
        book4.setStatus("Borrowed");  book4.setBorrowed(userMap.get("111111")); // David
        book5.setStatus("Borrowed");  book5.setBorrowed(userMap.get("222222")); // Emma
        book8.setStatus("Borrowed");  book8.setBorrowed(userMap.get("260004")); // Jenny
        book9.setStatus("Borrowed");  book9.setBorrowed(userMap.get("260005")); // Kevin
        book10.setStatus("Borrowed"); book10.setBorrowed(userMap.get("260002")); // Hannah

        // ── Available (2 books) ───────────────────────────────────────────────
        book6.setStatus("Available"); // Atomic Habits        — returned by George
        book7.setStatus("Available"); // The Pragmatic Prog.  — returned by Ivan
        // book8 already set above

        System.out.println("Book Data Loaded Successfully!");
    }
}