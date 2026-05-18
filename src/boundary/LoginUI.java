package boundary;

import adt.*;
import java.util.Scanner;
import control.*;
import control.ReportControl;
import control.TransactionControl;
import control.RoomBookingControl;
import entity.User;
import utility.ConsoleUtil;

public class LoginUI {
    private LoginControl loginControl;
    private Scanner scanner;

    public LoginUI(Scanner scanner, LoginControl loginControl) {
        this.loginControl = loginControl;
        this.scanner = scanner;
    }

    public void startLoginMenu() {
        int choice = -1;

        while (true) {
            System.out.println("+-------------------------------------------+");
            System.out.println("|       TAR UMT LIBRARY SERVICES SYSTEM     |");
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|   Select your role:                       |");
            System.out.println("|   1. Administrator                        |");
            System.out.println("|   2. Library User (Student/Staff)         |");
            System.out.println("|   0. Return to Menu                       |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                ConsoleUtil.printErrorBox("Invalid option. Please select 1, 2, or 0.");
                ConsoleUtil.enter(scanner);
                continue;
            }

            if (choice == 0) {
                return;
            }

            if (choice < 0 || choice > 2) {
                ConsoleUtil.printErrorBox("Invalid option. Please select 1, 2, or 0.");
                ConsoleUtil.enter(scanner);
                continue;
            }

            loginProcess(choice);
        }
    }

    private void loginProcess(int choice) {
        String roleType = (choice == 1) ? "ADMINISTRATOR" : "LIBRARY USER";
        String userId, password;

        while (true) {

            System.out.println("+-------------------------------------------+");
            ConsoleUtil.printCentered(roleType + " LOGIN", 43);
            System.out.println("+-------------------------------------------+");
            System.out.print("> User ID (0 to return): ");
            userId = scanner.nextLine();

            if (userId.equals("0")) return;

            if (!userId.matches("\\d{6}")) {
                ConsoleUtil.printErrorBox("Invalid Format: ID must be exactly 6 digits.");
                ConsoleUtil.enter(scanner);
                continue;
            }

            User foundUser = loginControl.getUserByUserID(userId);

            if (foundUser == null) {
                ConsoleUtil.printErrorBox("Error: User ID '" + userId + "' not found.");
                ConsoleUtil.enter(scanner);
                continue;
            }

            String userRole      = foundUser.getRole();
            boolean isAuthorized = false;

            if (choice == 1 && userRole.equalsIgnoreCase("Admin")) {
                isAuthorized = true;
            } else if (choice == 2 && (userRole.equalsIgnoreCase("Staff") || userRole.equalsIgnoreCase("Student"))) {
                isAuthorized = true;
            }

            if (!isAuthorized) {
                System.out.println("\n[!] ACCESS DENIED");
                System.out.printf("    This account (%s) cannot access the %s portal.\n", userRole, roleType);
                ConsoleUtil.enter(scanner);
                continue;
            }

            System.out.println("\n+-------------------------------------------+");
            ConsoleUtil.printCentered(roleType + " LOGIN", 43);
            System.out.println("+-------------------------------------------+");
            System.out.printf("| Hello, %-34s |\n", ConsoleUtil.truncate(foundUser.getUsername(), 33) + "!");
            System.out.printf("| Role: %-35s |\n", foundUser.getRole());
            System.out.println("+-------------------------------------------+");

            System.out.print("> Please enter your password: ");
            password = scanner.nextLine();

            if (loginControl.processLogin(userId, password)) {
                System.out.println("\n[V] Access granted. Redirecting...");
                ConsoleUtil.enter(scanner);

                AdminControl        adminControl        = loginControl.getAdminControl();
                UserControl         userControl         = loginControl.getUserControl();
                MaintainBookControl bookControl         = loginControl.getBookControl();
                TransactionControl  transactionControl  = loginControl.getTransactionControl();
                ReportControl       reportControl       = loginControl.getReportControl();
                RoomBookingControl  roomBookingControl  = loginControl.getRoomBookingControl(); // shared instance

                if (userRole.equalsIgnoreCase("Admin")) {
                    AdminUI adminUI = new AdminUI(scanner, foundUser,
                                                  bookControl, adminControl,
                                                  transactionControl, reportControl,
                                                  roomBookingControl);
                    adminUI.displayMainMenu();
                } else {
                    UserUI userUI = new UserUI(scanner, foundUser,
                                               bookControl, transactionControl,
                                               userControl, roomBookingControl);
                    userUI.displayUserMenu();
                }
                return;
            } else {
                System.out.println("\n[!] Incorrect password.");
                System.out.println("    Returning to Identification step...");
                ConsoleUtil.enter(scanner);
            }
        }
    }
}