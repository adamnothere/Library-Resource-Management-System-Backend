package boundary;

import control.MaintainBookControl;
import control.TransactionControl;
import entity.Book;
import entity.User;
import java.util.Scanner;
import utility.*;

public class AdminBookUI {
    private MaintainBookControl bookControl;
    private TransactionControl transactionControl;
    private Scanner scanner;

    public AdminBookUI(Scanner scanner, MaintainBookControl bookControl, TransactionControl transactionControl) {
        this.bookControl = bookControl;
        this.transactionControl = transactionControl;
        this.scanner = scanner;
    }

    public void startAdminMenu() {
        int choice = -1;
        do {
            
            System.out.println("+------------------------------------------+");
            System.out.println("|        ADMIN: BOOK MANAGEMENT            |");
            System.out.println("+------------------------------------------+");
            System.out.println("|                                          |");
            System.out.println("|   1. Add a New Book                      |");
            System.out.println("|   2. Update Book Status                  |");
            System.out.println("|   3. Process Next User in Waitlist       |");
            System.out.println("|   4. View All Books                      |");
            System.out.println("|   5. Remove a Book                       |");
            System.out.println("|   6. Undo Last Deletion                  |");
            System.out.println("|   7. View Currently Borrowed Books       |");
            System.out.println("|   0. Return to Main Menu                 |");
            System.out.println("|                                          |");
            System.out.println("+------------------------------------------+");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: addBookUI(); break;
                    case 2: updateStatusUI(); break;
                    case 3: processWaitlistUI(); break;
                    case 4: viewBooksSubMenu(); break;
                    case 5: removeBookUI(); break;
                    case 6: undoRemovalUI(); break;
                    case 7: viewBorrowReportUI(); break;
                    case 0: break;
                    default:
                        System.out.println("| [!] Invalid choice.                      |");
                }
                if (choice != 0) ConsoleUtil.pause(scanner);
            } else {
                ConsoleUtil.printErrorBox("| [!] Invalid input.                       |");
                scanner.nextLine();
                ConsoleUtil.pause(scanner);
            }
        } while (choice != 0);
    }

    private void viewBooksSubMenu() {
        int subChoice = -1;
        do {
            
            System.out.println("+------------------------------------------+");
            System.out.println("|           VIEW BOOKS MENU                |");
            System.out.println("+------------------------------------------+");
            System.out.println("|   1. View All (Default Order)            |");
            System.out.println("|   2. Search by Title / Author            |");
            System.out.println("|   3. Sort by Title (A to Z)              |");
            System.out.println("|   4. Sort by Title (Z to A)              |");
            System.out.println("|   0. Back to Main Menu                   |");
            System.out.println("+------------------------------------------+");
            System.out.print("Enter choice: ");

            if (scanner.hasNextInt()) {
                subChoice = scanner.nextInt();
                scanner.nextLine();
                switch (subChoice) {
                    case 1:
                        
                        displayAllBooks();
                        ConsoleUtil.pause(scanner);
                        break;
                    case 2:
                        searchAndDisplayUI();
                        break;
                    case 3:
                        
                        printBookArray(bookControl.getSortedBooks(true), "SORTED CATALOG (A-Z)");
                        ConsoleUtil.pause(scanner);
                        break;
                    case 4:
                        
                        printBookArray(bookControl.getSortedBooks(false), "SORTED CATALOG (Z-A)");
                        ConsoleUtil.pause(scanner);
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("| [!] Invalid choice.                      |");
                        ConsoleUtil.pause(scanner);
                }
            } else {
                ConsoleUtil.printErrorBox("| [!] Invalid input.                       |");
                scanner.nextLine();
                ConsoleUtil.pause(scanner);
            }
        } while (subChoice != 0);
    }

    private void searchAndDisplayUI() {
        
        System.out.println("+------------------------------------------+");
        System.out.println("|              SEARCH BOOKS                |");
        System.out.println("+------------------------------------------+");
        System.out.print("Enter keyword (Title or Author): ");
        String keyword = scanner.nextLine().trim();

        Book[] results = bookControl.searchBooks(keyword);
        
        printBookArray(results, "SEARCH RESULTS FOR: '" + keyword + "'");
        ConsoleUtil.pause(scanner);
    }

    private void printBookArray(Book[] books, String headerTitle) {
        if (books == null || books.length == 0) {
            System.out.println("\n+------------------------------------------------+");
            System.out.println("| [i] " + String.format("%-43s", "No books found for this view.") + "|");
            System.out.println("+------------------------------------------------+");
            return;
        }
        System.out.println("\n+---------------------------------------------------------------------------------------+");
        int padding = (85 - headerTitle.length()) / 2;
        System.out.printf("|%" + padding + "s%s%" + (85 - padding - headerTitle.length() + 2) + "s|\n", "", headerTitle, "");
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
        System.out.printf("| %-10s | %-30s | %-20s | %-16s |\n", "Book ID", "Title", "Author", "Status");
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
        for (Book b : books) {
            System.out.println(b.toString());
        }
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
    }

    public void displayAllBooks() {
        if (bookControl.isBookMapEmpty()) {
            System.out.println("\n+-------------------------------------------+");
            System.out.println("| [i] Catalog: No books currently available |");
            System.out.println("+-------------------------------------------+");
            return;
        }
        System.out.println("\n+---------------------------------------------------------------------------------------+");
        System.out.println("|                                 CURRENT BOOK CATALOG                                  |");
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
        System.out.printf("| %-10s | %-30s | %-20s | %-16s |\n", "Book ID", "Title", "Author", "Status");
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
        bookControl.printTable();
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
    }

    private void processWaitlistUI() {
        
        displayAllBooks();
        System.out.println("+------------------------------------------+");
        System.out.println("|       PROCESS BOOK WAITLIST              |");
        System.out.println("+------------------------------------------+");
        System.out.print("Enter Book ID to notify next user: ");
        String bookId = scanner.nextLine().trim();

        Book book = bookControl.searchBook(bookId);

        if (book == null) {
            System.out.println("+------------------------------------------+");
            System.out.println("| [X] Error: Book ID not found.            |");
            System.out.println("+------------------------------------------+");
            return;
        }

        if (!book.getStatus().equalsIgnoreCase("Reserved")) {
            System.out.println("+------------------------------------------+");
            System.out.printf("| [X] Failed: Book status is %-13s|\n", "'" + book.getStatus() + "'.");
            System.out.println("|     Must be 'Reserved' to process.       |");
            System.out.println("+------------------------------------------+");
            return;
        }

        if (book.isWaitListEmpty()) {
            System.out.println("+------------------------------------------+");
            System.out.println("| [i] Waitlist is currently empty.         |");
            System.out.println("+------------------------------------------+");
            return;
        }

        User nextUser = transactionControl.notifyNextInWaitlist(bookId);

        System.out.println("+------------------------------------------+");
        System.out.println("| [V] Next user to be notified:            |");
        System.out.println("+------------------------------------------+");
        System.out.printf("| Name    : %-30s |\n", nextUser.getUsername());
        System.out.printf("| User ID : %-30s |\n", nextUser.getUserId());
        System.out.printf("| Book    : %-30s |\n",
                book.getTitle().length() > 30 ? book.getTitle().substring(0, 27) + "..." : book.getTitle());
        System.out.println("+------------------------------------------+");
        System.out.println("| [!] Please notify this user directly.    |");
        System.out.println("|     They may now borrow the book.        |");
        System.out.println("+------------------------------------------+");
    }

    private void addBookUI() {
        
        System.out.println("+------------------------------------------+");
        System.out.println("|              ADD NEW BOOK                |");
        System.out.println("+------------------------------------------+");

        Book latestBook = bookControl.getLatestBook();
        String lastId   = latestBook.getBookId();
        String autoId   = FormatHandler.generateNextBookId(lastId);

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Author: ");
        String author = scanner.nextLine();

        System.out.println("\n+------------------------------------------+");
        System.out.println("|           BOOK ENTRY SUMMARY             |");
        System.out.println("+------------------------------------------+");
        System.out.printf("| ID    : %-32s |\n", autoId);
        System.out.printf("| Title : %-32s |\n", (title.length()  > 32 ? title.substring(0, 29)  + "..." : title));
        System.out.printf("| Author: %-32s |\n", (author.length() > 32 ? author.substring(0, 29) + "..." : author));
        System.out.println("+------------------------------------------+");

        System.out.print("> Save this book? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            if (bookControl.addNewBook(autoId, title, author)) {
                System.out.println("| [V] Book successfully added.             |");
            } else {
                System.out.println("| [X] Error: ID already exists.            |");
            }
        }
    }

    private void removeBookUI() {
        
        displayAllBooks();
        System.out.print("Enter Book ID to remove: ");
        String id = scanner.nextLine();

        if (bookControl.removeBook(id)) {
            System.out.println("| [V] Book removed. Use Undo to restore.   |");
        } else {
            System.out.println("| [X] Book ID not found.                   |");
        }
    }

    private void undoRemovalUI() {
        
        System.out.println("+------------------------------------------+");
        System.out.println("|            UNDO DELETION                 |");
        System.out.println("+------------------------------------------+");

        Book restoredBook = bookControl.undoLastRemoval();

        if (restoredBook != null) {
            String title = restoredBook.getTitle();
            if (title.length() > 25) title = title.substring(0, 22) + "...";
            System.out.println("| [V] Restored: " + String.format("%-26s", title) + "|");
        } else {
            System.out.println("| [X] Nothing to undo.                     |");
        }
        System.out.println("+------------------------------------------+");
    }

    private void viewBorrowReportUI() {
        
        Book[] borrowed = bookControl.getCurrentBorrowReport();

        System.out.println("+--------------------------------------------------------------------------------------------------+");
        System.out.println("|                              CURRENTLY BORROWED BOOKS REPORT                                     |");
        System.out.println("+--------------------------------------------------------------------------------------------------+");

        if (borrowed == null || borrowed.length == 0) {
            System.out.println("|                                                                                                  |");
            System.out.println("|              [ No books are currently being borrowed ]                                           |");
            System.out.println("|                                                                                                  |");
            System.out.println("+--------------------------------------------------------------------------------------------------+");
            ConsoleUtil.pause(scanner);
            return;
        }

        System.out.println("+------------+----------------------------+------------------+------------------+------------------+");
        System.out.printf("| %-10s | %-26s | %-16s | %-16s | %-16s |\n",
                "Book ID", "Title", "Borrower Name", "Borrower ID", "Due Date");
        System.out.println("+------------+----------------------------+------------------+------------------+------------------+");

        for (Book b : borrowed) {
            entity.User borrower = b.getBorrowed();
            String borrowerName = (borrower != null) ? borrower.getUsername() : "Unknown";
            String borrowerId   = (borrower != null) ? borrower.getUserId()   : "------";

            String dueDate = "N/A";
            if (borrower != null) {
                Object[] records = borrower.getBorrowRecords().toArray();
                for (Object obj : records) {
                    entity.BorrowRecord r = (entity.BorrowRecord) obj;
                    if (r.getBookId().equals(b.getBookId()) && r.getStatus().equals("Borrowing")) {
                        dueDate = r.getDueDate().toString();
                        break;
                    }
                }
            }

            String title        = b.getTitle().length()       > 26 ? b.getTitle().substring(0, 23)       + "..." : b.getTitle();
            String displayName  = borrowerName.length()        > 16 ? borrowerName.substring(0, 13)        + "..." : borrowerName;

            System.out.printf("| %-10s | %-26s | %-16s | %-16s | %-16s |\n",
                    b.getBookId(), title, displayName, borrowerId, dueDate);
        }

        System.out.println("+------------+----------------------------+------------------+------------------+------------------+");
        System.out.printf("| Total Books Currently Borrowed: %-61d    |\n", borrowed.length);
        System.out.println("+--------------------------------------------------------------------------------------------------+");
    }

    private void updateStatusUI() {
        
        displayAllBooks();
        System.out.print("Enter Book ID: ");
        String id = scanner.nextLine();
        System.out.print("New Status (Available/Borrowed/Reserved): ");
        String status = scanner.nextLine();

        if (bookControl.updateBookStatus(id, status)) {
            System.out.println("| [V] Status updated.                      |");
        } else {
            System.out.println("| [X] Book ID not found.                   |");
        }
    }
}