package boundary;

import java.util.Scanner;
import entity.*;
import control.*;
import utility.*;

public class UserUI {
    private Scanner scanner;
    private User currentUser;

    private MaintainBookControl bookControl;
    private TransactionControl transactionControl;
    private UserControl userControl;
    private RoomBookingControl roomBookingControl;

    private UserBookUI bookUI;
    private UserProfileUI accountUI;
    private UserRoomUI roomUI;

    public UserUI(Scanner scanner, User currentUser,
                  MaintainBookControl bookControl,
                  TransactionControl transactionControl,
                  UserControl userControl,
                  RoomBookingControl roomBookingControl) {
        this.scanner            = scanner;
        this.currentUser        = currentUser;
        this.bookControl        = bookControl;
        this.transactionControl = transactionControl;
        this.userControl        = userControl;
        this.roomBookingControl = roomBookingControl;
        this.bookUI             = new UserBookUI(scanner, currentUser, bookControl, transactionControl);
        this.accountUI          = new UserProfileUI(scanner, currentUser, userControl);
        this.roomUI             = new UserRoomUI(scanner, currentUser, roomBookingControl);
    }

    public void displayUserMenu() {
        int choice = -1;

        while (choice != 0) {

            System.out.println("+-------------------------------------------+");
            System.out.println("|            USER SERVICES PANEL            |");
            System.out.println("+-------------------------------------------+");
            System.out.printf("| Welcome, %-32s |\n", ConsoleUtil.truncate(currentUser.getUsername(), 32) + "!");
            System.out.printf("| Role: %-35s |\n", currentUser.getRole());
            System.out.println("|                                           |");
            System.out.println("|   1. Library Services                     |");
            System.out.println("|   2. My Account                           |");
            System.out.println("|   3. Discussion Room Booking              |");
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
                    bookUI.displayUserMenu();
                    break;
                case 2:
                    accountUI.displayMenu();
                    break;
                case 3:
                    roomUI.displayMenu();
                    break;
                case 0:
                    System.out.println("\nLogging out...");
                    break;
                default:
                    ConsoleUtil.printErrorBox("Invalid option. Please select 1, 2, 3, or 0.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }
}