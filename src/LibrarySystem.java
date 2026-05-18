import adt.*;
import entity.*;
import control.*;
import boundary.StartUI;
import java.io.UnsupportedEncodingException;
import utility.DataInitializer;
import java.util.Scanner;

public class LibrarySystem {
    public static void main(String[] args) throws UnsupportedEncodingException{
        
        Scanner scanner = new Scanner(System.in);
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        
        LinkedHashMap<String, User> userMap = new LinkedHashMap<>();
        LinkedHashMap<String, Book> bookMap = new LinkedHashMap<>();
        DataInitializer.loadDummyData(userMap);
        DataInitializer.loadBookData(bookMap, userMap);
        
        LoginControl loginControl = new LoginControl(userMap, bookMap);
        RegisterControl registerControl = new RegisterControl(userMap);
        StartUI ui = new StartUI(scanner, loginControl, registerControl);
        ui.displayMainMenu();
        
        scanner.close();
      
    }
}