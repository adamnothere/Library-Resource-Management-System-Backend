package boundary;

import java.util.Scanner;
import adt.*;
import entity.User;
import utility.ConsoleUtil;
import control.LoginControl;
import control.RegisterControl;

public class StartUI {
    private Scanner scanner; 
    private LoginUI loginUI;
    private RegisterUI registerUI;

    public StartUI(Scanner scanner, LoginControl loginControl, RegisterControl registerControl) {
        this.scanner = scanner;
        this.loginUI = new LoginUI(scanner, loginControl);
        this.registerUI = new RegisterUI(scanner, registerControl);
    }

    public void displayMainMenu() {
        int choice = -1;

        while (choice != 0) {
            System.out.println("+-------------------------------------------+");
            ConsoleUtil.printCentered("TAR UMT LIBRARY MANAGEMENT", 43);
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|   1. Login to System                      |");
            System.out.println("|   2. Register New Account                 |");
            System.out.println("|   0. Shutdown System                      |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                ConsoleUtil.printErrorBox("Please enter a valid number (0-2).");
                ConsoleUtil.enter(scanner);
                continue;
            }

            switch (choice) {
                case 1:
                    loginUI.startLoginMenu(); 
                    break;
                case 2:
                    System.out.println("\n[i] Redirecting to Registration...");
                    registerUI.startRegistration(); 
                    ConsoleUtil.enter(scanner);
                    break;
                case 0:
                    System.out.println("\n[V] System shutting down. Have a nice day!");
                    break;
                default:
                    ConsoleUtil.printErrorBox("Invalid choice. Try again.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }
}