package boundary;

import control.ReportControl;
import control.ReportControl.Summary;
import control.ReportControl.BookStat;
import control.ReportControl.UserStat;
import control.ReportControl.OverdueStat;
import utility.ConsoleUtil;
import java.util.Scanner;

public class AdminReportUI {

    private Scanner scanner;
    private ReportControl reportControl;

    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String RED = "\033[31m";
    private static final String CYAN = "\033[36m";
    private static final String MAGENTA = "\033[35m";
    private static final String BLUE = "\033[34m";
    private static final String WHITE = "\033[37m";

    private static final String[] BAR_COLORS = {
        GREEN, YELLOW, CYAN, MAGENTA, BLUE
    };

    private static final String COLOR_AVAILABLE = GREEN;
    private static final String COLOR_BORROWED  = YELLOW;
    private static final String COLOR_RESERVED  = CYAN;
    private static final String COLOR_OVERDUE   = RED;

    private static final int CHART_HEIGHT = 10;

    public AdminReportUI(Scanner scanner, ReportControl reportControl) {
        this.scanner       = scanner;
        this.reportControl = reportControl;
    }

    public void displayMenu() {
        int choice = -1;
        while (choice != 0) {
            
            System.out.println("+-------------------------------------------+");
            System.out.println("|         REPORTS & STATISTICS              |");
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|   1. System Summary Dashboard             |");
            System.out.println("|   2. Book Popularity Report               |");
            System.out.println("|   3. Most Active Borrowers                |");
            System.out.println("|   4. Overdue Books Report                 |");
            System.out.println("|   0. Back to Admin Panel                  |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                case 1: showSummary();         break;
                case 2: showBookPopularity();  break;
                case 3: showActiveBorrowers(); break;
                case 4: showOverdueBooks();    break;
                case 0: break;
                default:
                    ConsoleUtil.printErrorBox("Invalid selection.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }

    private void showSummary() {
        
        Summary s = reportControl.getSummary();

        System.out.println("+-------------------------------------------+");
        System.out.println("|        SYSTEM SUMMARY DASHBOARD           |");
        System.out.println("+-------------------------------------------+");
        System.out.println("|  BOOKS                                    |");
        System.out.println("+-------------------------------------------+");
        System.out.printf("|   Total Books       : %-19d |\n", s.totalBooks);
        System.out.printf("|   Available         : %-19d |\n", s.availableBooks);
        System.out.printf("|   Currently Borrowed: %-19d |\n", s.borrowedBooks);
        System.out.printf("|   Reserved          : %-19d |\n", s.reservedBooks);
        System.out.println("+-------------------------------------------+");
        System.out.println("|  USERS                                    |");
        System.out.println("+-------------------------------------------+");
        System.out.printf("|   Total Users       : %-19d |\n", s.totalUsers);
        System.out.printf("|   Students          : %-19d |\n", s.totalStudents);
        System.out.printf("|   Staff             : %-19d |\n", s.totalStaff);
        System.out.println("+-------------------------------------------+");
        System.out.println("|  BORROW ACTIVITY                          |");
        System.out.println("+-------------------------------------------+");
        System.out.printf("|   All-Time Records  : %-19d |\n", s.totalBorrowRecords);
        System.out.printf("|   Active Borrows    : %-19d |\n", s.activeBorrowRecords);
        System.out.println("+-------------------------------------------+");

        System.out.println();
        System.out.println("  GRAPHICAL REPRESENTATION");
        System.out.println("  --------------------------");
        System.out.println("  Book Status Distribution");
        System.out.println();

        String[] labels = { "Avail", "Borrow", "Reserve" };
        int[]    values = { s.availableBooks, s.borrowedBooks, s.reservedBooks };
        String[] colors = { COLOR_AVAILABLE, COLOR_BORROWED, COLOR_RESERVED };
        printColoredBarChart(labels, values, colors);

        System.out.println();
        System.out.println("  Legend:");
        System.out.println("  " + COLOR_AVAILABLE + " ### " + RESET + " Available   "
                         + COLOR_BORROWED  + " ### " + RESET + " Borrowed   "
                         + COLOR_RESERVED  + " ### " + RESET + " Reserved");
        System.out.println();
        ConsoleUtil.enter(scanner);
    }

    private void showBookPopularity() {
        
        BookStat[] stats = reportControl.getBookPopularity();

        System.out.println("+------------------------------------------------------------------------+");
        System.out.println("|                    BOOK POPULARITY REPORT                              |");
        System.out.println("|              (Ranked by Total Times Borrowed)                          |");
        System.out.println("+-----+----------+----------------------------+------------------+-------+");
        System.out.printf( "| %-3s | %-8s | %-26s | %-16s | %-5s |\n",
                "No.", "Book ID", "Title", "Status", "Times");
        System.out.println("+-----+----------+----------------------------+------------------+-------+");

        if (stats == null || stats.length == 0) {
            System.out.println("|              [ No book data available ]                                |");
            System.out.println("+------------------------------------------------------------------------+");
            ConsoleUtil.enter(scanner);
            return;
        }

        int rank = 1;
        for (BookStat b : stats) {
            System.out.printf("| %-3d | %-8s | %-26s | %-16s | %-5d |\n",
                    rank++, b.bookId, truncate(b.title, 26), b.status, b.totalBorrows);
        }
        System.out.println("+------------------------------------------------------------------------+");

        int chartSize = Math.min(5, stats.length);
        String[] labels = new String[chartSize];
        int[]    values = new int[chartSize];
        String[] colors = new String[chartSize];
        for (int i = 0; i < chartSize; i++) {
            labels[i] = stats[i].bookId;
            values[i] = stats[i].totalBorrows;
            colors[i] = BAR_COLORS[i % BAR_COLORS.length];
        }

        System.out.println();
        System.out.println("  GRAPHICAL REPRESENTATION");
        System.out.println("  --------------------------");
        System.out.println("  Top " + chartSize + " Most Borrowed Books");
        System.out.println();
        printColoredBarChart(labels, values, colors);

        System.out.println();
        System.out.println("  Legend:");
        System.out.print("  ");
        for (int i = 0; i < chartSize; i++) {
            System.out.print(colors[i] + " ### " + RESET + " " + truncate(stats[i].bookId, 8) + "   ");
        }
        System.out.println();
        System.out.println();
        ConsoleUtil.enter(scanner);
    }

    private void showActiveBorrowers() {
        
        UserStat[] stats = reportControl.getActiveBorrowers();

        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("|                    MOST ACTIVE BORROWERS                                 |");
        System.out.println("|              (Ranked by Total Borrow Records)                            |");
        System.out.println("+-----+----------+----------------------+---------+--------+--------+------+");
        System.out.printf( "| %-3s | %-8s | %-20s | %-7s | %-6s | %-6s | %-4s |\n",
                "No.", "ID", "Name", "Role", "Active", "Total", "Fine");
        System.out.println("+-----+----------+----------------------+---------+--------+--------+------+");

        if (stats == null || stats.length == 0) {
            System.out.println("|                 [ No user data available ]                               |");
            System.out.println("+--------------------------------------------------------------------------+");
            ConsoleUtil.enter(scanner);
            return;
        }

        int rank = 1;
        for (UserStat u : stats) {
            System.out.printf("| %-3d | %-8s | %-20s | %-7s | %-6d | %-6d | %-4d |\n",
                    rank++, u.userId, truncate(u.username, 20),
                    u.role, u.activeBorrows, u.totalBorrows, u.totalFines);
        }
        System.out.println("+-----+----------+----------------------+---------+--------+--------+------+");

        int chartSize = Math.min(5, stats.length);
        String[] labels = new String[chartSize];
        int[]    values = new int[chartSize];
        String[] colors = new String[chartSize];
        for (int i = 0; i < chartSize; i++) {
            labels[i] = stats[i].username;   
            values[i] = stats[i].totalBorrows;
            colors[i] = BAR_COLORS[i % BAR_COLORS.length];
        }

        System.out.println();
        System.out.println("  GRAPHICAL REPRESENTATION");
        System.out.println("  --------------------------");
        System.out.println("  Top " + chartSize + " Most Active Users (Total Borrows)");
        System.out.println();
        printColoredBarChart(labels, values, colors);

        System.out.println();
        System.out.println("  Legend:");
        System.out.print("  ");
        for (int i = 0; i < chartSize; i++) {
            System.out.print(colors[i] + " ### " + RESET + " " + truncate(stats[i].username, 8) + "   ");
        }
        System.out.println();
        System.out.println();
        ConsoleUtil.enter(scanner);
    }

    private void showOverdueBooks() {
        
        OverdueStat[] stats = reportControl.getOverdueBooks();

        System.out.println("+--------------------------------------------------------------------------------+");
        System.out.println("|                         OVERDUE BOOKS REPORT                                   |");
        System.out.println("|                   (Books borrowed past their due date)                         |");
        System.out.println("+----------+----------------------+------------+--------------------+------------+");
        System.out.printf( "| %-8s | %-20s | %-10s | %-18s | %-10s |\n",
                "Book ID", "Title", "Due Date", "Borrower", "Days Over");
        System.out.println("+----------+----------------------+------------+--------------------+------------+");

        if (stats == null || stats.length == 0) {
            System.out.println("|                    [ No overdue books - all clear! ]                           |");
            System.out.println("+--------------------------------------------------------------------------------+");
            ConsoleUtil.enter(scanner);
            return;
        }

        for (OverdueStat o : stats) {
            System.out.printf("| %-8s | %-20s | %-10s | %-18s | %-10d |\n",
                    o.bookId, truncate(o.bookTitle, 20),
                    o.dueDate.toString(), truncate(o.borrowerName, 18), o.daysOverdue);
        }
        System.out.println("+----------+----------------------+------------+--------------------+------------+");
        System.out.printf( "| Total Overdue: %-63d |\n", stats.length);
        System.out.println("+--------------------------------------------------------------------------------+");

        java.util.LinkedHashMap<String, int[]> userOverdue = new java.util.LinkedHashMap<>();
        for (OverdueStat o : stats) {
            String name = o.borrowerName;
            if (!userOverdue.containsKey(name)) {
                userOverdue.put(name, new int[]{0});
            }
            userOverdue.get(name)[0] += (int) o.daysOverdue;
        }

        int aggSize = userOverdue.size();
        String[] aggNames  = new String[aggSize];
        int[]    aggTotals = new int[aggSize];
        int idx = 0;
        for (java.util.Map.Entry<String, int[]> e : userOverdue.entrySet()) {
            aggNames[idx]  = e.getKey();
            aggTotals[idx] = e.getValue()[0];
            idx++;
        }
        for (int i = 1; i < aggSize; i++) {
            String tmpN = aggNames[i]; int tmpV = aggTotals[i];
            int j = i - 1;
            while (j >= 0 && aggTotals[j] < tmpV) {
                aggNames[j+1] = aggNames[j]; aggTotals[j+1] = aggTotals[j]; j--;
            }
            aggNames[j+1] = tmpN; aggTotals[j+1] = tmpV;
        }

        int chartSize = Math.min(5, aggSize);
        String[] labels = new String[chartSize];
        int[]    values = new int[chartSize];
        String[] colors = new String[chartSize];
        for (int i = 0; i < chartSize; i++) {
            labels[i] = aggNames[i];
            values[i] = aggTotals[i];
            colors[i] = (values[i] >= 14) ? RED : (values[i] >= 7) ? YELLOW : GREEN;
        }

        System.out.println();
        System.out.println("  GRAPHICAL REPRESENTATION");
        System.out.println("  --------------------------");
        System.out.println("  Total Overdue Days by User (Top " + chartSize + ")");
        System.out.println();
        printColoredBarChart(labels, values, colors);

        System.out.println();
        System.out.println("  Legend:");
        System.out.println("  " + GREEN  + "  ###  " + RESET + " < 7 days   "
                         + YELLOW + "  ###  " + RESET + " 7-13 days   "
                         + RED    + "  ###  " + RESET + " 14+ days (Critical)");
        System.out.println();
        ConsoleUtil.enter(scanner);
    }


    /**
     * Prints a vertical ASCII bar chart with per-bar ANSI colors.
     *
     * @param labels  X-axis label for each bar
     * @param values  Integer value for each bar
     * @param colors  ANSI color code for each bar
     */
    private void printColoredBarChart(String[] labels, int[] values, String[] colors) {
        if (labels == null || labels.length == 0) return;

        int maxVal = 0;
        for (int v : values) if (v > maxVal) maxVal = v;
        if (maxVal == 0) maxVal = 1;

        int displayRows = Math.min(maxVal, CHART_HEIGHT);

        System.out.println("      ↑ "); 
        for (int row = displayRows; row >= 1; row--) {
            int rowValue = (int) Math.round((double) maxVal * row / displayRows);
            System.out.printf("  %3d |", rowValue);

            for (int col = 0; col < values.length; col++) {
                String color = (col < colors.length) ? colors[col] : GREEN;
                double threshold = (double) maxVal * row / displayRows;
                String block = (values[col] >= threshold) ? (color + "  ###  " + RESET + "  ") : "         ";
                System.out.print(block);
            }
            System.out.println();
        }

        System.out.print("      +-");
        for (int col = 0; col < values.length; col++) System.out.print("---------");
        System.out.println("-> Values");

        System.out.print("       ");
        for (String label : labels) {
            System.out.printf("%-9s", truncate(label, 9));
        }
        System.out.println();
    }

    private String truncate(String str, int width) {
        if (str == null) return "";
        return (str.length() > width) ? str.substring(0, width - 3) + ".." : str;
    }
}