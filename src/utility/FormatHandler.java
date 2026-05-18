package utility;

import java.io.IOException;
import java.util.Scanner;

public class FormatHandler {
    
    public static boolean isValidBookIdFormat(String bookId) {
        return bookId != null && bookId.matches("^B\\d{4}$");
    }
    
    public static String generateNextBookId(String lastId) {
        if (lastId == null || !lastId.startsWith("B")) {
            return "B1001"; 
        }

        try {
            int numericPart = Integer.parseInt(lastId.substring(1));

            return String.format("B%04d", numericPart + 1);
        } catch (NumberFormatException e) {
            return "B1001"; 
        }
    }    
}
