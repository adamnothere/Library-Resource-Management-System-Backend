package boundary;

import java.util.Scanner;
import entity.*;
import control.*;
import utility.*;

public class AdminUI {
    private Scanner scanner;
    private User currentUser;

    private MaintainBookControl bookControl;
    private AdminControl adminControl;
    private TransactionControl transactionControl;
    private ReportControl reportControl;
    private RoomBookingControl roomBookingControl;

    private AdminManagementUI userManagement;
    private AdminBookUI bookManagement;
    private AdminReportUI reportUI;
    private AdminRoomUI roomUI;

    public AdminUI(Scanner scanner, User currentUser,
                   MaintainBookControl bookControl,
                   AdminControl adminControl,
                   TransactionControl transactionControl,
                   ReportControl reportControl,
                   RoomBookingControl roomBookingControl) {
        this.scanner            = scanner;
        this.currentUser        = currentUser;
        this.bookControl        = bookControl;
        this.adminControl       = adminControl;
        this.transactionControl = transactionControl;
        this.reportControl      = reportControl;
        this.roomBookingControl = roomBookingControl;
        this.userManagement     = new AdminManagementUI(scanner, adminControl);
        this.bookManagement     = new AdminBookUI(scanner, bookControl, transactionControl);
        this.reportUI           = new AdminReportUI(scanner, reportControl);
        this.roomUI             = new AdminRoomUI(scanner, roomBookingControl);
    }

    public void displayMainMenu() {
        int choice = -1;

        while (choice != 0) {

            System.out.println("+-------------------------------------------+");
            System.out.println("|            ADMINISTRATOR PANEL            |");
            System.out.println("+-------------------------------------------+");
            System.out.printf("| Welcome, %-32s |\n", currentUser.getUsername() + "!");
            System.out.println("|                                           |");
            System.out.println("|   1. User Management                      |");
            System.out.println("|   2. Book Management                      |");
            System.out.println("|   3. Reports & Statistics                 |");
            System.out.println("|   4. Room Booking Management              |");
            System.out.println("|   0. Logout                               |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1:
                    userManagement.displayMenu();
                    break;
                case 2:
                    bookManagement.startAdminMenu();
                    break;
                case 3:
                    reportUI.displayMenu();
                    break;
                case 4:
                    roomUI.displayMenu();
                    break;
                case 0:
                    System.out.println("\nExiting Admin Panel...");
                    break;
                default:
                    ConsoleUtil.printErrorBox("Invalid option. Please select 1–4 or 0.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }
}