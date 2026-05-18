package boundary;

import java.util.Scanner;
import control.LoginControl;
import control.RegisterControl;
import entity.User;
import utility.ConsoleUtil;
import adt.*;

public class RegisterUI {
    private Scanner scanner;
    private RegisterControl registerControl;

    public RegisterUI(Scanner scanner, RegisterControl registerControl) {
        this.scanner = scanner;
        this.registerControl = registerControl;
    }
    
    public void startRegistration(){
        while (true) {
            
            System.out.println("+-------------------------------------------+");
            ConsoleUtil.printCentered("NEW ACCOUNT REGISTRATION", 43);
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|  Please fill in the details below.        |");
            System.out.println("|  Enter '0' at User ID to cancel.          |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            
            System.out.print("> Set 6-Digit User ID (0 to return): ");
            String id = scanner.nextLine();
            
            if (id.equals("0")) return;

            if (!id.matches("\\d{6}")) {
                ConsoleUtil.printErrorBox("Format Error: ID must be 6 digits.");
                ConsoleUtil.enter(scanner);
                continue;
            }
            
            if (!registerControl.isIdAvailable(id)) {
                ConsoleUtil.printErrorBox("Error: ID '" + id + "' already exists.");
                ConsoleUtil.enter(scanner);
                continue;
            }
            
            System.out.print("> Enter Full Name: ");
            String name = scanner.nextLine();

            String pass;
            while(true){
                System.out.print("> Set Password: ");
                pass = scanner.nextLine();
                if (registerControl.isValidPassword(pass)) {
                    break; 
                } else {
                    ConsoleUtil.printErrorBox("Insecure Password! Requirements:\n" +
                                             " - Minimum length of 5 characters.");
                }
            }
            
            String email;
            while (true) {
                System.out.print("> Enter Email Address: ");
                email = scanner.nextLine();
                if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    break; 
                } else {
                    ConsoleUtil.printErrorBox("Invalid email format (e.g., user@mail.com)");
                }
            }

            System.out.println("\nAccount Role: Student");
            String role = "Student";
            
            System.out.println("\n+-------------------------------------------+");
            ConsoleUtil.printCentered("CONFIRM REGISTRATION", 43);
            System.out.println("+-------------------------------------------+");
            System.out.printf("| %-8s : %-30s |\n", "ID", id);
            System.out.printf("| %-8s : %-30s |\n", "Name", ConsoleUtil.truncate(name, 30));
            System.out.printf("| %-8s : %-30s |\n", "Email", ConsoleUtil.truncate(email, 30));
            System.out.printf("| %-8s : %-30s |\n", "Role", role);
            System.out.println("+-------------------------------------------+");
            System.out.print("> Confirm details? (Y/N): ");
            
            if (scanner.nextLine().equalsIgnoreCase("Y")) {
                User newUser = new User(id, name, email, pass, role);
                registerControl.registerUser(newUser);
                System.out.println("\n[V] Registration Successful!");
                break; 
            } else {
                System.out.println("\n[!] Registration Cancelled.");
                ConsoleUtil.enter(scanner);
            }
        }
    }
}