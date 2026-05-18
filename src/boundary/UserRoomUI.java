package boundary;

import control.RoomBookingControl;
import entity.RoomBooking;
import entity.User;
import utility.ConsoleUtil;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class UserRoomUI {

    private static final String BORDER = "+-------------------------------------------+";
    private static final String WIDE   = "+============================================================+";
    private static final String WDASH  = "+------------------------------------------------------------+";

    private Scanner              scanner;
    private User                 currentUser;
    private RoomBookingControl   roomControl;

    public UserRoomUI(Scanner scanner, User currentUser, RoomBookingControl roomControl) {
        this.scanner     = scanner;
        this.currentUser = currentUser;
        this.roomControl = roomControl;
    }

    public void displayMenu() {
        int choice = -1;
        while (choice != 0) {
            System.out.println(BORDER);
            System.out.println("|        DISCUSSION ROOM BOOKING            |");
            System.out.println(BORDER);
            System.out.println("|                                           |");
            System.out.println("|   1. Book a Discussion Room               |");
            System.out.println("|   2. View My Bookings                     |");
            System.out.println("|   3. Cancel a Booking                     |");
            System.out.println("|   0. Back                                 |");
            System.out.println("|                                           |");
            System.out.println(BORDER);
            System.out.print("> Enter choice: ");

            try { choice = Integer.parseInt(scanner.nextLine()); }
            catch (NumberFormatException e) { choice = -1; }

            switch (choice) {
                case 1: bookRoom();          break;
                case 2: viewMyBookings();    break;
                case 3: cancelBooking();     break;
                case 0: break;
                default:
                    ConsoleUtil.printErrorBox("Invalid option. Please select 0–3.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }

    private void bookRoom() {
        System.out.println("\n" + "+============================================================+");
        System.out.println("|                   BOOK A DISCUSSION ROOM                   |");
        System.out.println("+============================================================+");
        System.out.printf("|  Bookings available from today up to %-13s         |\n",
                LocalDate.now().plusDays(RoomBookingControl.MAX_DAYS_AHEAD).toString());
        System.out.println(WDASH);

        LocalDate date = pickDate();
        if (date == null) return;

        String[] rooms = roomControl.getAllRooms();
        printAvailabilityGrid(date, rooms);

        System.out.println("\n  Rooms available:");
        for (int i = 0; i < rooms.length; i++) {
            System.out.printf("  %d. %s\n", i + 1, rooms[i]);
        }
        System.out.print("> Select room number (0 to cancel): ");
        int roomChoice = -1;
        try { roomChoice = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { roomChoice = -1; }

        if (roomChoice == 0) return;
        if (roomChoice < 1 || roomChoice > rooms.length) {
            ConsoleUtil.printErrorBox("Invalid room selection.");
            ConsoleUtil.enter(scanner);
            return;
        }
        String selectedRoom = rooms[roomChoice - 1];

        int slotHour = pickSlot(date, selectedRoom, rooms);
        if (slotHour == -1) return;

        System.out.println("\n" + WDASH);
        System.out.println("|                  CONFIRM BOOKING                           |");
        System.out.println(WDASH);
        System.out.printf("|  Room  : %-49s |\n", selectedRoom);
        System.out.printf("|  Date  : %-49s |\n", date.toString());
        System.out.printf("|  Slot  : %-49s |\n", RoomBooking.slotLabel(slotHour));
        System.out.printf("|  Name  : %-49s |\n", currentUser.getUsername());
        System.out.println(WDASH);
        System.out.print("> Confirm booking? (Y/N): ");

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("\n[!] Booking cancelled.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String result = roomControl.book(selectedRoom, currentUser.getUserId(),
                                         currentUser.getUsername(), date, slotHour);
        printBookResult(result);
        ConsoleUtil.enter(scanner);
    }

    private void viewMyBookings() {
        RoomBooking[] all   = roomControl.getAllBookingsForUser(currentUser.getUserId());
        LocalDate today     = LocalDate.now();

        int upcomingCount = 0, historyCount = 0;
        for (RoomBooking b : all) {
            boolean isFuture = !b.getDate().isBefore(today);
            if ((b.isActive() || b.isCheckedIn()) && isFuture) upcomingCount++;
            else historyCount++;
        }

        RoomBooking[] upcoming = new RoomBooking[upcomingCount];
        RoomBooking[] history  = new RoomBooking[historyCount];
        int ui = 0, hi = 0;
        for (RoomBooking b : all) {
            boolean isFuture = !b.getDate().isBefore(today);
            if ((b.isActive() || b.isCheckedIn()) && isFuture) upcoming[ui++] = b;
            else history[hi++] = b;
        }

        System.out.println("\n" + "+=================================================================+");
        System.out.println("|                       MY ROOM BOOKINGS                          |");
        System.out.println("+=================================================================+");
        System.out.printf("|  User : %-50s      |\n", currentUser.getUsername());
        System.out.printf("|  ID   : %-50s      |\n", currentUser.getUserId());

        System.out.println("+=================================================================+");
        System.out.println("|  UPCOMING BOOKINGS                                              |");
        System.out.println("+=================================================================+");

        if (upcomingCount == 0) {
            System.out.println("|      [ No upcoming bookings ]                                   |");
        } else {
            System.out.printf("| %-4s | %-8s | %-10s | %-17s | %-12s |\n",
                    "No.", "Room", "Date", "Time Slot", "Status");
            System.out.println("+-----------------------------------------------------------------+");
            for (int i = 0; i < upcoming.length; i++) {
                RoomBooking b = upcoming[i];
                String statusLabel = b.isCheckedIn() ? "[~]CheckedIn" : "[A]Active   ";
                System.out.printf("| %-4d | %-8s | %-10s | %-17s | %-10s |\n",
                        i + 1, b.getRoomId(), b.getDate(),
                        RoomBooking.slotLabel(b.getSlotHour()), statusLabel);
            }
        }

        System.out.println("+-----------------------------------------------------------------+");
        System.out.println("|  BOOKING HISTORY                                                |");
        System.out.println("+-----------------------------------------------------------------+");

        if (historyCount == 0) {
            System.out.println("|      [ No booking history ]                                     |");
        } else {
            System.out.printf("| %-4s | %-8s | %-10s | %-17s | %-12s |\n",
                    "No.", "Room", "Date", "Time Slot", "Status");
            System.out.println("+-----------------------------------------------------------------+");
            for (int i = 0; i < history.length; i++) {
                RoomBooking b = history[i];
                String statusLabel;
                switch (b.getStatus()) {
                    case "Completed":  statusLabel = "[V]Completed"; break;
                    case "Cancelled":  statusLabel = "[X]Cancelled"; break;
                    case "CheckedIn":  statusLabel = "[~]CheckedIn"; break;
                    default:           statusLabel = "[A]Active   "; break;
                }
                System.out.printf("| %-4d | %-8s | %-10s | %-17s | %-11s |\n",
                        i + 1, b.getRoomId(), b.getDate(),
                        RoomBooking.slotLabel(b.getSlotHour()), statusLabel);
            }
        }

        System.out.println("+=================================================================+");
        System.out.printf("  Upcoming: %-3d   History: %d\n", upcomingCount, historyCount);
        ConsoleUtil.enter(scanner);
    }

    private void cancelBooking() {
        RoomBooking[] bookings = roomControl.getActiveBookingsForUser(currentUser.getUserId());

        System.out.println("\n" + "+==================================================+");
        System.out.println("|              CANCEL A BOOKING                    |");
        System.out.println("+==================================================+");

        if (bookings.length == 0) {
            System.out.println("|  [ No active bookings to cancel ]                |");
            System.out.println("+==================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        System.out.printf("| %-4s | %-8s | %-10s | %-17s |\n",
                "No.", "Room", "Date", "Time Slot");
        System.out.println("+--------------------------------------------------+");
        for (int i = 0; i < bookings.length; i++) {
            RoomBooking b = bookings[i];
            System.out.printf("| %-4d | %-8s | %-10s | %-17s |\n",
                    i + 1, b.getRoomId(), b.getDate(),
                    RoomBooking.slotLabel(b.getSlotHour()));
        }
        System.out.println("+==================================================+");
        System.out.print("> Select booking number to cancel (0 to go back): ");

        int sel = -1;
        try { sel = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { sel = -1; }

        if (sel == 0) return;
        if (sel < 1 || sel > bookings.length) {
            ConsoleUtil.printErrorBox("Invalid selection.");
            ConsoleUtil.enter(scanner);
            return;
        }

        RoomBooking chosen = bookings[sel - 1];
        System.out.printf("\n> Cancel booking for %s on %s at %s? (Y/N): ",
                chosen.getRoomId(), chosen.getDate(),
                RoomBooking.slotLabel(chosen.getSlotHour()));

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Cancellation aborted.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String result = roomControl.cancel(chosen.getBookingId(),
                                           currentUser.getUserId(), false);
        if (result.equals("CANCELLED")) {
            System.out.println("\n[V] Booking cancelled successfully.");
        } else if (result.equals("ALREADY_CHECKED_IN")) {
            ConsoleUtil.printErrorBox("Cannot cancel: booking is already checked in.");
        } else if (result.equals("ALREADY_COMPLETED")) {
            ConsoleUtil.printErrorBox("Cannot cancel: booking is already completed.");
        } else {
            ConsoleUtil.printErrorBox("Cancellation failed: " + result);
        }
        ConsoleUtil.enter(scanner);
    }

    private LocalDate pickDate() {
        System.out.println("\n  Enter the booking date.");
        System.out.printf("  Valid range: %s  to  %s\n",
                LocalDate.now(), LocalDate.now().plusDays(RoomBookingControl.MAX_DAYS_AHEAD));
        System.out.print("> Date (YYYY-MM-DD, 0 to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return null;
        try {
            LocalDate date = LocalDate.parse(input);
            if (!roomControl.isValidDate(date)) {
                ConsoleUtil.printErrorBox("Date out of allowed range (today to +7 days).");
                ConsoleUtil.enter(scanner);
                return null;
            }
            return date;
        } catch (DateTimeParseException e) {
            ConsoleUtil.printErrorBox("Invalid date format. Use YYYY-MM-DD.");
            ConsoleUtil.enter(scanner);
            return null;
        }
    }

    private int pickSlot(LocalDate date, String selectedRoom, String[] allRooms) {
        System.out.println("\n  Available time slots for " + selectedRoom + " on " + date + ":");
        System.out.println("  +---------+--------------------+");
        System.out.println("  | Slot No | Time               |");
        System.out.println("  +---------+--------------------+");

        boolean[][] grid = roomControl.getAvailabilityGrid(date, allRooms);
        int roomIdx = -1;
        for (int i = 0; i < allRooms.length; i++) {
            if (allRooms[i].equals(selectedRoom)) { roomIdx = i; break; }
        }

        for (int s = RoomBookingControl.SLOT_START; s <= RoomBookingControl.SLOT_END; s++) {
            int idx = s - RoomBookingControl.SLOT_START;
            boolean taken = (roomIdx >= 0) && grid[roomIdx][idx];
            String label = taken ? "  [TAKEN]  " : String.format("  %-2d       ", s - RoomBookingControl.SLOT_START + 1);
            System.out.printf("  | %-7s | %-18s |\n",
                    taken ? "------" : (s - RoomBookingControl.SLOT_START + 1) + "",
                    RoomBooking.slotLabel(s) + (taken ? " [X]" : ""));
        }
        System.out.println("  +---------+--------------------+");
        System.out.print("> Enter slot number (0 to cancel): ");

        int slotChoice = -1;
        try { slotChoice = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { slotChoice = -1; }

        if (slotChoice == 0) return -1;

        int totalSlots = RoomBookingControl.SLOT_END - RoomBookingControl.SLOT_START + 1;
        if (slotChoice < 1 || slotChoice > totalSlots) {
            ConsoleUtil.printErrorBox("Invalid slot number.");
            ConsoleUtil.enter(scanner);
            return -1;
        }

        int slotHour = RoomBookingControl.SLOT_START + slotChoice - 1;

        if (roomIdx >= 0 && grid[roomIdx][slotChoice - 1]) {
            ConsoleUtil.printErrorBox("That slot is already taken. Please choose another.");
            ConsoleUtil.enter(scanner);
            return -1;
        }

        return slotHour;
    }

    private void printAvailabilityGrid(LocalDate date, String[] rooms) {
        boolean[][] grid = roomControl.getAvailabilityGrid(date, rooms);

        int innerWidth = 19 + rooms.length * 11;
        String border = "+" + "-".repeat(innerWidth) + "+";

        System.out.println("\n  Room Availability for " + date);
        System.out.println(border);

        System.out.printf("|  %-17s", "Time Slot");
        for (String r : rooms) System.out.printf("| %-8s ", r);
        System.out.println("|");

        System.out.println("|" + "-".repeat(innerWidth) + "+");

        for (int s = RoomBookingControl.SLOT_START; s <= RoomBookingControl.SLOT_END; s++) {
            int idx = s - RoomBookingControl.SLOT_START;
            System.out.printf("|  %-17s", RoomBooking.slotLabel(s));
            for (int r = 0; r < rooms.length; r++) {
                System.out.printf("| %-8s ", grid[r][idx] ? "[TAKEN]" : "[FREE] ");
            }
            System.out.println("|");
        }

        System.out.println(border);
    }

    private void printBookResult(String result) {
        switch (result) {
            case "BOOKED":
                System.out.println("\n[V] Room booked successfully!");
                break;
            case "ROOM_TAKEN":
                ConsoleUtil.printErrorBox("That room slot is already taken.");
                break;
            case "USER_CONFLICT":
                ConsoleUtil.printErrorBox("You already have a booking at that date and time.");
                break;
            case "INVALID_DATE":
                ConsoleUtil.printErrorBox("Invalid date. Must be within 7 days from today.");
                break;
            case "INVALID_SLOT":
                ConsoleUtil.printErrorBox("Invalid time slot.");
                break;
            case "INVALID_ROOM":
                ConsoleUtil.printErrorBox("Invalid room selected.");
                break;
            default:
                ConsoleUtil.printErrorBox("Booking failed: " + result);
        }
    }
}