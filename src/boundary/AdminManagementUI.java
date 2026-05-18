package boundary;

import java.util.Scanner;
import control.AdminControl;
import entity.User;
import utility.ConsoleUtil;

public class AdminManagementUI {
    private Scanner scanner;
    private AdminControl adminControl;

    private static final String BOX_BORDER = "+--------------------------------------------------------------------------------------------------+";

    public AdminManagementUI(Scanner scanner, AdminControl adminControl) {
        this.scanner = scanner;
        this.adminControl = adminControl;
    }

    public void displayMenu() {
        int choice = -1;
        while (choice != 0) {
            
            System.out.println("+-------------------------------------------+");
            System.out.println("|           USER MANAGEMENT SYSTEM          |");
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|   1. Register New User                    |");
            System.out.println("|   2. Search/View User Details             |");
            System.out.println("|   3. Update Existing User                 |");
            System.out.println("|   4. Remove User Account                  |");
            System.out.println("|   5. Display All Users                    |");
            System.out.println("|   6. Undo Last Operation                  |");
            System.out.println("|   0. Back to Admin Panel                  |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            handleChoice(choice);
        }
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1: registerNewUser(); break;
            case 2: searchUser(); break;
            case 3: updateUser(); break;
            case 4: removeUser(); break;
            case 5: displayAllUsers(); break;
            case 6: performUndo(); break;
            case 0: break;
            default:
                ConsoleUtil.printErrorBox("Invalid selection.");
                ConsoleUtil.enter(scanner);
        }
    }

    private void registerNewUser() {
        
        printWindowHeader("REGISTER NEW USER");

        String id;
        while (true) {
            System.out.print("> Enter User ID (0 to return): ");
            id = scanner.nextLine().trim();
            if (id.equals("0")) return;
            if (id.matches("\\d{6}")) {
                if (!adminControl.isIdTaken(id)) break;
                else ConsoleUtil.printErrorBox("DUPLICATE ID: " + id + " is already in the system.");
            } else {
                ConsoleUtil.printErrorBox("INVALID FORMAT: ID must be exactly 6 numeric digits.");
            }
        }

        String name;
        while (true) {
            System.out.print("> Enter Full Name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) break;
            ConsoleUtil.printErrorBox("REQUIRED: Name cannot be blank.");
        }

        String email;
        while (true) {
            System.out.print("> Enter Email Address: ");
            email = scanner.nextLine().trim();
            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) break;
            ConsoleUtil.printErrorBox("INVALID EMAIL: Please enter a valid email format.");
        }

        String pass;
        while (true) {
            System.out.print("> Set Password (min 5 chars): ");
            pass = scanner.nextLine();
            if (pass.length() >= 5) break;
            ConsoleUtil.printErrorBox("WEAK PASSWORD: Password must be at least 5 characters long.");
        }

        String role;
        while (true) {
            System.out.print("> Enter Role (Student/Staff): ");
            role = scanner.nextLine().trim();

            if (role.equalsIgnoreCase("Admin")) {
                ConsoleUtil.printErrorBox("ACCESS DENIED: Cannot register new Admin accounts through this menu.");
            } else if (role.equalsIgnoreCase("Student") || role.equalsIgnoreCase("Staff")) {
                role = role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase();
                break;
            } else {
                ConsoleUtil.printErrorBox("INVALID ROLE: Please choose 'Student' or 'Staff'.");
            }
        }

        User newUser = new User(id, name, email, pass, role);
        adminControl.saveUser(newUser);

        System.out.println("\n[V] SUCCESS: " + role + " '" + name + "' registered.");
        ConsoleUtil.enter(scanner);
    }

//    private void searchUser() {
//        
//        printWindowHeader("SEARCH USER DETAILS");
//
//        String id;
//        while(true) {
//            System.out.print("> Enter User ID to search (0 to return): ");
//            id = scanner.nextLine().trim();
//            User user = adminControl.getUserById(id);
//            
//            if(id.equals("0")){
//                return;
//            }
//            
//            if (user != null) {
//                System.out.println("\n+-----------------------+");
//                System.out.println("|      USER FOUND       |");
//                System.out.println("+-----------------------+");
//                System.out.println("  ID    : " + user.getUserId());
//                System.out.println("  Name  : " + user.getUsername());
//                System.out.println("  Email : " + user.getEmail());
//                System.out.println("  Role  : " + user.getRole());
//                System.out.println("  Joined: " + user.getRegistrationDate());
//                ConsoleUtil.enter(scanner);
//                break;
//            } else {
//                ConsoleUtil.printErrorBox("User not found.");
//                ConsoleUtil.enter(scanner); 
//            }
//            
//        }
//  
//    }
    
    private void searchUser() {
        while (true) {
            
            System.out.println("+------------------------------------------+");
            System.out.println("|           SEARCH USER REGISTRY           |");
            System.out.println("+------------------------------------------+");
            System.out.println("| 1. Search by User ID                     |");
            System.out.println("| 2. Search by Name                        |");
            System.out.println("| 3. Search by Email                       |");
            System.out.println("| 4. Filter by Role                        |");
            System.out.println("| 0. Return                                |");
            System.out.println("+------------------------------------------+");
            System.out.print("> Choice: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) return;
            
            if (!choice.matches("[1-4]")) {
                ConsoleUtil.printErrorBox("Invalid choice! Please select 0-4.");
                ConsoleUtil.enter(scanner);
                continue; 
            }

            System.out.print("> Enter search term: ");
            String term = scanner.nextLine().trim();

            User[] results;

            if (choice.equals("1")) {
                User user = adminControl.getUserById(term);
                results = (user != null) ? new User[]{user} : new User[0];
            } else {
                String type = (choice.equals("2")) ? "name" : (choice.equals("3")) ? "email" : "role";
                results = adminControl.searchUsersByCriteria(term, type);
            }

            displaySearchResults(results);
        }
        
    }

    private void displaySearchResults(User[] results) {
        if (results.length == 0) {
            ConsoleUtil.printErrorBox("No users found matching your criteria.");
        } else {
            System.out.println("\n+------------+----------------------+---------------------------+--------------+");
            System.out.printf("| %-10s | %-20s | %-25s | %-12s |\n", "ID", "Name", "Email", "Role");
            System.out.println("+------------+----------------------+---------------------------+--------------+");
            for (User u : results) {
                System.out.printf("| %-10s | %-20s | %-25s | %-12s |\n", 
                    u.getUserId(), 
                    (u.getUsername().length() > 20 ? u.getUsername().substring(0, 17) + "..." : u.getUsername()), 
                    (u.getEmail().length() > 25 ? u.getEmail().substring(0, 22) + "..." : u.getEmail()), 
                    u.getRole());
            }
            System.out.println("+------------+----------------------+---------------------------+--------------+");
            System.out.println("| Total Matches: " + String.format("%-61d", results.length) + " |");
            System.out.println("+------------+----------------------+---------------------------+--------------+");
        }
        ConsoleUtil.enter(scanner);
    }

    private void updateUser() {
        
        printWindowHeader("UPDATE EXISTING USER");

        while (true) {
            System.out.print("> Enter User ID to update (0 to return): ");
            String id = scanner.nextLine().trim();

            if (id.equals("0")) return;

            User user = adminControl.getUserById(id);

            if (user != null) {
                
                if (user.getRole().equalsIgnoreCase("Admin")) {
                    ConsoleUtil.printErrorBox("ACCESS DENIED: Administrative accounts cannot be modified.");
                    ConsoleUtil.enter(scanner);
                    continue;
                }

                System.out.print("> New Name (Leave blank to keep '" + user.getUsername() + "'): ");
                String newName = scanner.nextLine().trim();
                if (newName.isEmpty()) newName = user.getUsername();

                System.out.print("> New Email (Leave blank to keep '" + user.getEmail() + "'): ");
                String newEmail = scanner.nextLine().trim();
                if (newEmail.isEmpty()) newEmail = user.getEmail();

                String newRole = user.getRole();
                while (true) {
                    System.out.print("> New Role (Student/Staff) (Leave blank to keep '" + user.getRole() + "'): ");
                    String inputRole = scanner.nextLine().trim();

                    if (inputRole.isEmpty()) break; 

                    if (inputRole.equalsIgnoreCase("Admin")) {
                        ConsoleUtil.printErrorBox("INVALID ACTION: Cannot promote User to Admin via this panel.");
                    } else if (inputRole.equalsIgnoreCase("Student") || inputRole.equalsIgnoreCase("Staff")) {
                        newRole = inputRole.substring(0, 1).toUpperCase() + inputRole.substring(1).toLowerCase();
                        break;
                    } else {
                        ConsoleUtil.printErrorBox("INVALID ROLE: Please enter 'Student' or 'Staff'.");
                    }
                }

               
                User updatedUser = new User(user.getUserId(), newName, newEmail, user.getPassword(), newRole);
                adminControl.saveUser(updatedUser); 

                System.out.println("\n[V] SUCCESS: User " + id + " has been updated.");
                ConsoleUtil.enter(scanner);
                break;

            } else {
                ConsoleUtil.printErrorBox("User not found.");
                ConsoleUtil.enter(scanner);
            }
        }
    }

    private void removeUser() {
        while (true) {
            
            printWindowHeader("REMOVE USER ACCOUNT");

            System.out.print("> Enter User ID to remove (0 to return): ");
            String id = scanner.nextLine().trim();

           
            if (id.equals("0")) {
                return;
            }
            User user = adminControl.getUserById(id);
            
            if (user != null) {
                if (user.getRole().equalsIgnoreCase("Admin")) {
                    ConsoleUtil.printErrorBox("CRITICAL ERROR: Administrative accounts cannot be deleted.");
                    ConsoleUtil.enter(scanner);
                    continue;
                } 
            }
            
            if (adminControl.deleteUser(id)) {
                System.out.println("\n[V] SUCCESS: Account " + id + " has been permanently deleted.");
                ConsoleUtil.enter(scanner);
                break; 
            } else {
                ConsoleUtil.printErrorBox("DELETE FAILED: User not found or has active dependencies.");
                ConsoleUtil.enter(scanner);
            }
        }
    }

    private void displayAllUsers() {
        while (true) {
            printWindowHeader("VIEW USER DATABASE");
            System.out.println("|    Choose Display Order:                                                                         |");
            System.out.println("|    1. Oldest First                                                                               |");
            System.out.println("|    2. Latest First                                                                               |");
            System.out.println("|    3. Alphabetical                                                                               |");
            System.out.println("|    4. Group by Role                                                                              |");
            System.out.println("|    0. Return to Admin Menu                                                                       |");
            System.out.println("+--------------------------------------------------------------------------------------------------+");

            String choice;
          
            while (true) {
                System.out.print("> Select sorting method: ");
                choice = scanner.nextLine().trim(); 

                if (choice.equals("0")) return; 
                if (choice.matches("[1-4]")) break; 

                ConsoleUtil.printErrorBox("Invalid selection. Please enter 0-4.");
            }

            String headerTitle = "USER DATABASE";
            switch(choice) {
                case "2": headerTitle += " (LATEST FIRST)"; break;
                case "3": headerTitle += " (ALPHABETICAL)"; break;
                case "4": headerTitle += " (BY ROLE)"; break;
                default: headerTitle += " (CHRONOLOGICAL)"; break;
            }

            printWindowHeader(headerTitle);
            System.out.println("+------------+----------------------+---------------------------+--------------+--------------+");
            System.out.printf("| %-10s | %-20s | %-25s | %-12s | %-12s |\n", 
                              "User ID", "Name", "Email", "Role", "Joined");
            System.out.println("+------------+----------------------+---------------------------+--------------+--------------+");

            adminControl.printSortedTable(choice);

            System.out.println("+------------+----------------------+---------------------------+--------------+--------------+");

            ConsoleUtil.pause(scanner); 
        }
    }
    
    private void performUndo(){
        
        printWindowHeader("UNDO SYSTEM");
        
        if (adminControl.canUndo()){
            String result = adminControl.undo();
            System.out.println("\n[V] SUCCESS: " + result);
        } else {
            ConsoleUtil.printErrorBox("No history found in history stack.");
        }
        ConsoleUtil.enter(scanner);
    }
    
   
    private void printWindowHeader(String title) {
        System.out.println(BOX_BORDER);
        // Centers the title within the 98-character inner space
        int padding = (98 - title.length()) / 2;
        System.out.printf("|%" + padding + "s%s%" + (98 - padding - title.length()) + "s|\n", "", title, "");
        System.out.println(BOX_BORDER);
    }
}