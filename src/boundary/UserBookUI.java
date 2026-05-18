package boundary;

import control.MaintainBookControl;
import control.TransactionControl;

import entity.*;
import utility.ConsoleUtil;
import java.util.Scanner;

public class UserBookUI {
    private MaintainBookControl bookControl;
    private TransactionControl  transactionControl;
    private Scanner scanner;
    private User currentUser;
    private static final String BOX_BORDER = "+---------------------------------------------------------------------------------------+";

    public UserBookUI(Scanner scanner, User currentUser,
                      MaintainBookControl bookControl,
                      TransactionControl transactionControl) {
        this.bookControl        = bookControl;
        this.transactionControl = transactionControl;
        this.currentUser        = currentUser;
        this.scanner            = scanner;
    }

    public void displayUserMenu() {
        int choice = -1;
        while (choice != 0) {
            
            System.out.println("+-------------------------------------------+");
            System.out.println("|           USER: LIBRARY SERVICES          |");
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|   1. Browse Available Books               |");
            System.out.println("|   2. Borrow a Book                        |");
            System.out.println("|   3. Return a Book                        |");
            System.out.println("|   0. Return to Main Menu                  |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1: viewBooksSubMenu(); break;
                case 2: borrowBookUI();     break;
                case 3: returnBookUI();     break;
                case 0: break;
                default:
                    ConsoleUtil.printErrorBox("Invalid selection.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }

    private void viewBooksSubMenu() {
        int subChoice = -1;
        do {
            
            System.out.println("+------------------------------------------+");
            System.out.println("|           BROWSE BOOKS MENU              |");
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
                        
                        printFilteredBookArray(bookControl.searchBooks(""), "CURRENT BOOK CATALOG");
                        ConsoleUtil.pause(scanner);
                        break;
                    case 2:
                        searchAndDisplayUI();
                        break;
                    case 3:
                        
                        printFilteredBookArray(bookControl.getSortedBooks(true), "SORTED CATALOG (A-Z)");
                        ConsoleUtil.pause(scanner);
                        break;
                    case 4:
                        
                        printFilteredBookArray(bookControl.getSortedBooks(false), "SORTED CATALOG (Z-A)");
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
        
        printFilteredBookArray(results, "SEARCH RESULTS FOR: '" + keyword + "'");
        ConsoleUtil.pause(scanner);
    }

    private void printFilteredBookArray(Book[] books, String headerTitle) {
        int visibleCount = 0;
        if (books != null) {
            for (Book b : books) {
                if (!b.getStatus().equalsIgnoreCase("Lost"))
                    visibleCount++;
            }
        }
        if (visibleCount == 0) {
            System.out.println("\n+------------------------------------------------+");
            System.out.println("| [i] " + String.format("%-43s", "No available books found for this view.") + "|");
            System.out.println("+------------------------------------------------+");
            return;
        }
        System.out.println("\n" + BOX_BORDER);
        int padding = (85 - headerTitle.length()) / 2;
        System.out.printf("|%" + padding + "s%s%" + (85 - padding - headerTitle.length() + 2) + "s|\n", "", headerTitle, "");
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
        System.out.printf("| %-10s | %-30s | %-20s | %-16s |\n", "Book ID", "Title", "Author", "Status");
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
        for (Book b : books) {
            if (!b.getStatus().equalsIgnoreCase("Lost"))
                System.out.println(b.toString());
        }
        System.out.println("+------------+--------------------------------+----------------------+------------------+");
    }

    private void borrowBookUI() {
        while (true) {
            
            printWindowHeader("BORROW A BOOK");
            printFilteredBookArray(bookControl.searchBooks(""), "AVAILABLE BOOKS FOR BORROWING");

            System.out.println("\n+-------------------------------------------------------+");
            System.out.printf("|  Role    : %-42s |\n", currentUser.getRole());
            System.out.printf("|  Borrowed: %-42s |\n", currentUser.getActiveBorrowCount() + " / " + currentUser.getBorrowLimit());
            System.out.printf("|  Slots   : %-42s |\n", currentUser.getRemainingBorrowSlots() + " remaining");
            System.out.println("+-------------------------------------------------------+");
            System.out.print("> Enter Book ID to borrow (0 to return): ");
            String bookId = scanner.nextLine().trim().toUpperCase();
            if (bookId.equals("0")) return;

            Book book = bookControl.searchBook(bookId);
            if (book == null) {
                ConsoleUtil.printErrorBox("BOOK NOT FOUND: ID '" + bookId + "' does not exist.");
                ConsoleUtil.enter(scanner);
                continue;
            }

            String result = transactionControl.borrowBook(bookId, currentUser.getUserId());

            switch (result) {
                case "BORROWED":
                    System.out.println("\n+-------------------------------------------------------+");
                    System.out.println("| [V] SUCCESS: Book borrowed successfully!              |");
                    System.out.println("|-------------------------------------------------------|");
                    System.out.printf("|   Book     : %-40s |\n", truncate(book.getTitle(), 40));
                    System.out.printf("|   Due Date : %-40s |\n", java.time.LocalDate.now().plusDays(BorrowRecord.DUE_DAYS));
                    System.out.printf("|   Borrowed : %-40s |\n", currentUser.getActiveBorrowCount() + " / " + currentUser.getBorrowLimit());
                    System.out.printf("|   Slots    : %-40s |\n", currentUser.getRemainingBorrowSlots() + " remaining");
                    System.out.println("+-------------------------------------------------------+");
                    break;
                case "WAITLISTED":
                    System.out.println("\n+-------------------------------------------------------+");
                    System.out.println("| [i] NOTICE: Book unavailable. Added to waitlist.      |");
                    System.out.printf("|   Book : %-44s |\n", truncate(book.getTitle(), 44));
                    System.out.println("|   You will be notified when the book is ready.        |");
                    System.out.println("+-------------------------------------------------------+");
                    break;
                case "LIMIT_REACHED":
                    System.out.println("\n+-------------------------------------------------------+");
                    System.out.println("| [X] LIMIT REACHED: Cannot borrow any more books.      |");
                    System.out.printf("|   Limit : %-43s |\n", currentUser.getBorrowLimit() + " books max");
                    System.out.println("|   Please return a book before borrowing another.      |");
                    System.out.println("+-------------------------------------------------------+");
                    break;
                case "ALREADY_BORROWED":
                    ConsoleUtil.printErrorBox("ACCESS DENIED: You have already borrowed this book.");
                    break;
                case "DUPLICATE":
                    ConsoleUtil.printErrorBox("DUPLICATE: You are already in the waitlist for this book.");
                    break;
                case "NOT_FOUND":
                    ConsoleUtil.printErrorBox("SYSTEM ERROR: User or book could not be resolved.");
                    break;
                default:
                    ConsoleUtil.printErrorBox("ACCESS DENIED: You are not first in line for this book.");
                    break;
            }
            ConsoleUtil.enter(scanner);
            break;
        }
    }

    private void returnBookUI() {
        while (true) {
            
            printWindowHeader("RETURN A BOOK");

            Object[] records = currentUser.getBorrowRecords().toArray();
            int count = 0;
            for (Object obj : records) {
                BorrowRecord r = (BorrowRecord) obj;
                if (r.getStatus().equals("Borrowing")) count++;
            }
            if (count == 0) {
                System.out.println("\n+-------------------------------------------+");
                System.out.println("| [i] You have no books currently borrowed. |");
                System.out.println("+-------------------------------------------+");
                ConsoleUtil.enter(scanner);
                return;
            }

            displayMyBorrowedBooks();

            System.out.print("> Enter Book ID to return (0 to cancel): ");
            String bookId = scanner.nextLine().trim().toUpperCase();

            if (bookId.equals("0")) return;

            Book book = bookControl.searchBook(bookId);
            if (book == null) {
                ConsoleUtil.printErrorBox("BOOK NOT FOUND: ID '" + bookId + "' does not exist.");
                ConsoleUtil.enter(scanner);
                continue;
            }

            String userId = currentUser.getUserId();

            String result = transactionControl.returnBook(bookId, userId);

            switch (result) {
                case "RETURNED":
                case "RETURNED_RESERVED":
                    System.out.println("\n+-------------------------------------------------------+");
                    System.out.println("| [V] SUCCESS: Book returned successfully!              |");
                    System.out.println("|-------------------------------------------------------|");
                    System.out.printf("|   Book   : %-42s |\n", truncate(book.getTitle(), 42));
                    if (result.equals("RETURNED_RESERVED")) {
                        System.out.printf("|   Status : %-42s |\n", "Reserved (someone is waiting)");
                    } else {
                        System.out.printf("|   Status : %-42s |\n", "Available");
                    }
                    System.out.println("+-------------------------------------------------------+");

                    FineRecord fine = transactionControl.checkOverdue(bookId, userId);
                    if (fine != null) {
                        System.out.println("+-------------------------------------------------------+");
                        System.out.println("| [!] OVERDUE FINE ISSUED                               |");
                        System.out.println("|-------------------------------------------------------|");
                        System.out.printf("|   Book      : %-39s |\n", truncate(fine.getBookTitle(), 38));
                        System.out.printf("|   Days Late : %-39s |\n", fine.getDaysOverdue() + " day(s)");
                        System.out.printf("|   Rate      : %-39s |\n", "RM " + String.format("%.2f", FineRecord.RATE_PER_DAY) + " per day");
                        System.out.printf("|   Fine      : %-39s |\n", "RM " + String.format("%.2f", fine.getFineAmount()));
                        System.out.println("|-------------------------------------------------------|");
                        System.out.println("|   Go to My Account > My Fines to pay.                 |");
                        System.out.println("+-------------------------------------------------------+");
                    } else {
                        System.out.println("+-------------------------------------------------------+");
                        System.out.println("| [V] Returned on time. No fine issued.                 |");
                        System.out.println("+-------------------------------------------------------+");
                    }
                    break;

                case "NOT_BORROWER":
                    ConsoleUtil.printErrorBox("ACCESS DENIED: You did not borrow this book.");
                    break;
                case "NOT_BORROWED":
                    ConsoleUtil.printErrorBox("ERROR: This book is not currently borrowed.");
                    break;
                default:
                    ConsoleUtil.printErrorBox("SYSTEM ERROR: Action could not be completed.");
                    break;
            }
            ConsoleUtil.enter(scanner);
            return;
        }
    }

    private void displayMyBorrowedBooks() {
        Object[] records = currentUser.getBorrowRecords().toArray();
        System.out.println("\n" + "+-----------------------------------------------------------------------------------+");
        System.out.println("|                       YOUR CURRENTLY BORROWED BOOKS                               |");
        System.out.println("+------------+--------------------------------+------------------+------------------+");
        System.out.printf("| %-10s | %-30s | %-16s | %-16s |\n",
                "Book ID", "Title", "Borrowed On", "Due Date");
        System.out.println("+------------+--------------------------------+------------------+------------------+");
        for (Object obj : records) {
            BorrowRecord r = (BorrowRecord) obj;
            if (r.getStatus().equals("Borrowing")) {
                System.out.printf("| %-10s | %-30s | %-16s | %-16s |\n",
                        r.getBookId(),
                        truncate(r.getBookTitle(), 30),
                        r.getBorrowDate().toString(),
                        r.getDueDate().toString());
            }
        }
        System.out.println("+------------+--------------------------------+------------------+------------------+");
    }

    private void printWindowHeader(String title) {
        System.out.println(BOX_BORDER);
        int padding = (87 - title.length()) / 2;
        System.out.printf("|%" + padding + "s%s%" + (87 - padding - title.length()) + "s|\n", "", title, "");
        System.out.println(BOX_BORDER);
    }

    private String truncate(String str, int width) {
        if (str == null) return "";
        return (str.length() > width) ? str.substring(0, width - 3) + "..." : str;
    }
}