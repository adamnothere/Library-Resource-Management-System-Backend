package boundary;

import java.util.Scanner;
import entity.User;
import entity.BorrowRecord;
import entity.FineRecord;
import adt.LinkedHashMap;
import control.UserControl;
import utility.ConsoleUtil;

public class UserProfileUI {
    private Scanner scanner;
    private User currentUser;
    private UserControl userControl;

    private static final String BOX_BORDER = "+-------------------------------------------+";
    private static final String WIDE   = "+============================================================+";
    private static final String WDASH  = "+------------------------------------------------------------+";

    public UserProfileUI(Scanner scanner, User currentUser, UserControl userControl) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
        this.userControl = userControl;
    }

    public void displayMenu() {
        int choice = -1;
        while (choice != 0) {
            
            System.out.println(BOX_BORDER);
            System.out.println("|         MY ACCOUNT SETTINGS               |");
            System.out.println(BOX_BORDER);
            System.out.println("|                                           |");
            System.out.println("|   1. View My Profile                      |");
            System.out.println("|   2. Update Full Name                     |");
            System.out.println("|   3. Change Email                         |");
            System.out.println("|   4. Change Password                      |");
            System.out.println("|   5. My Borrow Records                    |");
            System.out.println("|   6. My Fines                             |");  
            System.out.println("|   0. Back to Main Menu                    |");
            System.out.println("|                                           |");
            System.out.println(BOX_BORDER);
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1: viewProfile();      break;
                case 2: updateName();       break;
                case 3: updateEmail();      break;
                case 4: changePassword();   break;
                case 5: viewBorrowRecords();break;
                case 6: viewFinesMenu();    break;  
                case 0: break;
                default:
                    ConsoleUtil.printErrorBox("Invalid selection.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }

    private void viewProfile() {
        
        System.out.println(BOX_BORDER);
        System.out.println("|               YOUR PROFILE                |");
        System.out.println(BOX_BORDER);
        System.out.printf("|  User ID : %-30s |\n", currentUser.getUserId());
        System.out.printf("|  Name    : %-30s |\n", ConsoleUtil.truncate(currentUser.getUsername(), 30));
        System.out.printf("|  Email   : %-30s |\n", ConsoleUtil.truncate(currentUser.getEmail(), 30));
        System.out.printf("|  Role    : %-30s |\n", currentUser.getRole());
        System.out.printf("|  Joined  : %-30s |\n", currentUser.getRegistrationDate());
        System.out.println(BOX_BORDER);
        ConsoleUtil.enter(scanner);
    }

    private void updateName() {
        System.out.print("\n> Enter New Full Name: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            currentUser.setUsername(newName);
            if (userControl.updateProfile(currentUser))
                System.out.println("[V] SUCCESS: Name updated.");
        } else {
            ConsoleUtil.printErrorBox("ERROR: Name cannot be empty.");
        }
        ConsoleUtil.enter(scanner);
    }

    private void updateEmail() {
        System.out.print("\n> Enter New Email: ");
        String newEmail = scanner.nextLine().trim();
        if (newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            currentUser.setEmail(newEmail);
            if (userControl.updateProfile(currentUser))
                System.out.println("[V] SUCCESS: Email updated.");
        } else {
            ConsoleUtil.printErrorBox("INVALID FORMAT: Please enter a valid email.");
        }
        ConsoleUtil.enter(scanner);
    }

    private void changePassword() {
        System.out.print("\n> Current Password: ");
        String oldPass = scanner.nextLine();
        System.out.print("> New Password (min 5 chars): ");
        String newPass = scanner.nextLine();
        if (newPass.length() >= 5) {
            if (userControl.changePassword(currentUser, oldPass, newPass))
                System.out.println("[V] SUCCESS: Password changed successfully.");
            else
                ConsoleUtil.printErrorBox("AUTH ERROR: Current password does not match.");
        } else {
            ConsoleUtil.printErrorBox("WEAK PASSWORD: Must be at least 5 characters.");
        }
        ConsoleUtil.enter(scanner);
    }

    private void viewBorrowRecords() {
        

        System.out.println("+==========================================================================+");
        System.out.println("|                       MY BORROW RECORDS                                  |");
        System.out.println("+==========================================================================+");
        System.out.printf("|  Name  : %-64s|\n", currentUser.getUsername());
        System.out.printf("|  ID    : %-64s|\n", currentUser.getUserId());
        System.out.println("+--------------------------------------------------------------------------+");

        if (!currentUser.hasBorrowRecords()) {
            System.out.println("|                                                                          |");
            System.out.println("|        [ No borrow records found for this account ]                      |");
            System.out.println("|                                                                          |");
            System.out.println("+==========================================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        System.out.println("+--------+----------------------+------------+------------+----------------+");
        System.out.printf("| %-6s | %-20s | %-10s | %-10s | %-14s |\n",
                "BookID", "Title", "Borrowed", "Returned", "Status");
        System.out.println("+--------+----------------------+------------+------------+----------------+");

        currentUser.getBorrowRecords().printTableFormat();

        System.out.println("+--------+----------------------+------------+------------+----------------+");

        int total = 0, active = 0, returned = 0;
        Object[] arr = currentUser.getBorrowRecords().toArray();
        for (Object obj : arr) {
            BorrowRecord r = (BorrowRecord) obj;
            total++;
            if (r.getStatus().equals("Borrowing")) active++;
            else returned++;
        }

        System.out.println("+--------------------------------------------------------------------------+");
        System.out.printf("| Total: %-3d   Currently Borrowing: %-3d   Returned: %-3d                    |\n",
                total, active, returned);
        System.out.println("+==========================================================================+");

        ConsoleUtil.enter(scanner);
    }

    private void viewFinesMenu() {
        int choice = -1;
        while (choice != 0) {
            
            System.out.println(WIDE);
            System.out.println("|                     MY FINES                               |");
            System.out.println(WIDE);
            System.out.printf("|  Name : %-50s |\n", currentUser.getUsername());
            System.out.printf("|  ID   : %-50s |\n", currentUser.getUserId());
            System.out.println(WDASH);

            if (!currentUser.hasFineRecords()) {
                System.out.println("|                                                            |");
                System.out.println("|        [ No fines found for this account ]                 |");
                System.out.println("|                                                            |");
            } else {
                System.out.println("+-----+---------+----------------+------+----------+---------+");
                System.out.printf("| %-3s | %-7s | %-14s | %-4s | %-8s | %-7s |" + "\n", "No.", "Book ID", "Title", "Days", "Amount", "Status");
                System.out.println("+-----+---------+----------------+------+----------+---------+");

                Object[] arr  = currentUser.getFineRecords().toArray();
                int index     = 1;
                double total  = 0;
                double unpaid = 0;

                for (Object obj : arr) {
                    FineRecord f  = (FineRecord) obj;
                    String title  = ConsoleUtil.truncate(f.getBookTitle(), 14);
                    String amt    = "RM " + String.format("%.2f", f.getFineAmount());
                    String status = f.isPaid() ? "[V]Paid" : "[!]Unpd";

                    System.out.printf("| %-3s | %-7s | %-14s | %-4s | %-8s | %-7s |" + "\n",
                            index++, f.getBookId(), title, f.getDaysOverdue(), amt, status);

                    total  += f.getFineAmount();
                    if (!f.isPaid()) unpaid += f.getFineAmount();
                }

                System.out.println("+-----+---------+----------------+------+----------+---------+");
                System.out.printf("|  %-16s RM%-8.2f                               |\n", "Total Fines :", total);
                System.out.printf("|  %-16s RM%-8.2f                               |\n", "Unpaid      :", unpaid);
                System.out.printf("|  %-16s RM%-8.2f                               |\n", "Paid        :", total - unpaid);
            }

            System.out.println(WIDE);
            System.out.println("|                                                            |");
            if (currentUser.hasUnpaidFines()) {
                System.out.println("|   1. Pay a Fine                                            |");
            }
            System.out.println("|   0. Back                                                  |");
            System.out.println("|                                                            |");
            System.out.println(WIDE);
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            if (choice == 1 && currentUser.hasUnpaidFines()) {
                payFineUI();
            } else if (choice == 0) {
                break;
            } else {
                ConsoleUtil.printErrorBox("Invalid selection.");
                ConsoleUtil.enter(scanner);
            }
        }
    }

    private void payFineUI() {
        
        System.out.println(WIDE);
        System.out.println("|                     PAY A FINE                             |");
        System.out.println(WIDE);

        Object[]   arr         = currentUser.getFineRecords().toArray();
        FineRecord[] unpaidList = new FineRecord[arr.length];
        int unpaidCount        = 0;

        System.out.println("+-----+---------+----------------+------+----------+");
        System.out.printf("| %-3s | %-7s | %-14s | %-4s | %-8s |" + "\n", "No.", "Book ID", "Title", "Days", "Amount");
        System.out.println("+-----+---------+----------------+------+----------+");

        for (Object obj : arr) {
            FineRecord f = (FineRecord) obj;
            if (!f.isPaid()) {
                String amt = "RM " + String.format("%.2f", f.getFineAmount());
                System.out.printf("| %-3s | %-7s | %-14s | %-4s | %-8s |" + "\n",
                        unpaidCount + 1, f.getBookId(), ConsoleUtil.truncate(f.getBookTitle(), 14),
                        f.getDaysOverdue(), amt);
                unpaidList[unpaidCount++] = f;
            }
        }

        System.out.println("+-----+---------+----------------+------+----------+");
        System.out.println(WIDE);
        System.out.print("> Select fine number to pay (0 to cancel): ");

        int sel = -1;
        try {
            sel = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            sel = -1;
        }

        if (sel == 0) return;

        if (sel < 1 || sel > unpaidCount) {
            ConsoleUtil.printErrorBox("Invalid selection.");
            ConsoleUtil.enter(scanner);
            return;
        }

        FineRecord selected = unpaidList[sel - 1];
        selected.markPaid();
        double remaining = currentUser.getTotalUnpaidFines();

        
        System.out.println(WIDE);
        System.out.println("|                PAYMENT SUCCESSFUL                          |");
        System.out.println(WIDE);
        System.out.println("|                                                            |");
        System.out.printf("|   %-14s : %-39s |\n", "User",         currentUser.getUsername());
        System.out.printf("|   %-14s : %-39s |\n", "User ID",      currentUser.getUserId());
        System.out.println(WDASH);
        System.out.printf("|   %-14s : %-39s |\n", "Book ID",      selected.getBookId());
        System.out.printf("|   %-14s : %-39s |\n", "Book Title",   ConsoleUtil.truncate(selected.getBookTitle(), 41));
        System.out.printf("|   %-14s : %-39s |\n", "Days Overdue", selected.getDaysOverdue() + " day(s)");
        System.out.printf("|   %-14s : %-39s |\n", "Rate",         "RM " + String.format("%.2f", FineRecord.RATE_PER_DAY) + " / day");
        System.out.printf("|   %-14s : %-39s |\n", "Amount Paid",  "RM " + String.format("%.2f", selected.getFineAmount()));
        System.out.printf("|   %-14s : %-39s |\n", "Date Issued",  selected.getIssuedDate());
        System.out.println(WDASH);
        System.out.println("|                                                            |");
        System.out.println("|   [V]  Fine marked as PAID successfully.                   |");
        System.out.println("|        Thank you for your payment!                         |");
        if (remaining > 0) {
            System.out.printf("|        Remaining unpaid : RM%-30.2f |\n", remaining);
        } else {
            System.out.println("|        No more outstanding fines. You are all clear!       |");
        }
        System.out.println("|                                                            |");
        System.out.println(WIDE);
        ConsoleUtil.enter(scanner);
    }

}