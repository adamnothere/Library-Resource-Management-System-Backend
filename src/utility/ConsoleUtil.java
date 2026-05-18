package utility;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUtil {
    
    public static void clearScreen() {
        try {
            // Try to clear screen
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[2J\033[H");
            }
        } catch (IOException | InterruptedException e) {
            // If clearing fails, just print some newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }
    
    public static void enter(Scanner scanner) {
        System.out.println("\nPress Enter to proceed...");
        scanner.nextLine();
    }
    
    public static void printErrorBox(String message) {
        System.out.println("\n[!] -----------------------------------------");
        // Ensure the error message doesn't break the line if it's too long
        if (message.length() > 40) {
            System.out.println("    " + message);
        } else {
            System.out.printf("    %-40s\n", message);
        }
        System.out.println("    -----------------------------------------");
    }
    
    public static void printCentered(String text, int width) {
        int pad = (width - text.length()) / 2;
        String format = "|%" + (pad + text.length()) + "s%" + (width - pad - text.length() + 1) + "s";
        System.out.printf(format + "\n", text, "|");
    }
    
    public static String truncate(String str, int width) {
        if (str == null) return "";
        return str.length() > width ? str.substring(0, width - 3) + "..." : str;
    }
}