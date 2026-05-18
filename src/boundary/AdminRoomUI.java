package boundary;

import control.RoomBookingControl;
import entity.RoomBooking;
import utility.ConsoleUtil;
import java.util.Scanner;

public class AdminRoomUI {

    private Scanner            scanner;
    private RoomBookingControl roomControl;

    public AdminRoomUI(Scanner scanner, RoomBookingControl roomControl) {
        this.scanner     = scanner;
        this.roomControl = roomControl;
    }

    public void displayMenu() {
        int choice = -1;
        while (choice != 0) {
            System.out.println("+-------------------------------------------+");
            System.out.println("|       DISCUSSION ROOM MANAGEMENT          |");
            System.out.println("+-------------------------------------------+");
            System.out.println("|                                           |");
            System.out.println("|   1. View Full Booking History            |");
            System.out.println("|   2. Cancel a Booking                     |");
            System.out.println("|   3. Check-In User                        |");
            System.out.println("|   4. Check-Out User                       |");
            System.out.println("|   5. Add New Room                         |");
            System.out.println("|   6. Remove a Room                        |");
            System.out.println("|   7. Undo Last Room Removal               |");
            System.out.println("|   8. View All Rooms & Status              |");
            System.out.println("|   0. Back                                 |");
            System.out.println("|                                           |");
            System.out.println("+-------------------------------------------+");
            System.out.print("> Enter choice: ");

            try { choice = Integer.parseInt(scanner.nextLine()); }
            catch (NumberFormatException e) { choice = -1; }

            switch (choice) {
                case 1: viewAllBookings();     break;
                case 2: adminCancelBooking();  break;
                case 3: checkInUser();         break;
                case 4: checkOutUser();        break;
                case 5: addRoom();             break;
                case 6: removeRoom();          break;
                case 7: undoRemoveRoom();      break;
                case 8: viewAllRooms();        break;
                case 0: break;
                default:
                    ConsoleUtil.printErrorBox("Invalid option. Please select 0–8.");
                    ConsoleUtil.enter(scanner);
            }
        }
    }

    private void viewAllBookings() {
        RoomBooking[] bookings = roomControl.getAllBookings();

        System.out.println("\n" + "+==========================================================================================+");
        System.out.println("|                               FULL BOOKING HISTORY                                       |");
        System.out.println("+==========================================================================================+");

        if (bookings.length == 0) {
            System.out.println("|                      [ No bookings on record ]                                           |");
            System.out.println("+==========================================================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        printBookingTable(bookings);
        System.out.println("+==========================================================================================+");
        System.out.printf("  Total records: %d\n", bookings.length);
        ConsoleUtil.enter(scanner);
    }

    private void adminCancelBooking() {
        RoomBooking[] bookings = roomControl.getCancellableBookings();

        System.out.println("\n" + "+==========================================================================================+");
        System.out.println("|                              CANCEL A BOOKING (ADMIN)                                    |");
        System.out.println("+==========================================================================================+");

        if (bookings.length == 0) {
            System.out.println("|                   [ No active bookings to cancel ]                                       |");
            System.out.println("+==========================================================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        printBookingTable(bookings);
        System.out.println("+==========================================================================================+");
        System.out.print("> Enter booking number to cancel (0 to go back): ");

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
        System.out.printf("\n> Cancel booking #%d (%s, %s, %s) by %s? (Y/N): ",
                sel, chosen.getRoomId(), chosen.getDate(),
                RoomBooking.slotLabel(chosen.getSlotHour()),
                chosen.getUsername());

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Cancellation aborted.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String result = roomControl.cancel(chosen.getBookingId(), null, true);
        if (result.equals("CANCELLED")) {
            System.out.println("\n[V] Booking cancelled successfully.");
        } else {
            ConsoleUtil.printErrorBox("Cancellation failed: " + result);
        }
        ConsoleUtil.enter(scanner);
    }

    private void checkOutUser() {
        System.out.println("\n" + "+==============================================================================+");
        System.out.println("|                       CHECK-OUT USER FROM ROOM BOOKING                       |");
        System.out.println("+==============================================================================+");
        System.out.println("|  Only bookings with status 'CheckedIn' can be checked out.                   |");
        System.out.println("+------------------------------------------------------------------------------+");

        RoomBooking[] all         = roomControl.getAllActiveBookings();
        java.time.LocalDate today = java.time.LocalDate.now();

        int count = 0;
        for (RoomBooking b : all) {
            if (b.isCheckedIn() && b.getDate().equals(today)) count++;
        }

        if (count == 0) {
            System.out.println("|  [ No checked-in bookings for today to check out ]                           |");
            System.out.println("+==============================================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        RoomBooking[] checkedIn = new RoomBooking[count];
        int idx = 0;
        for (RoomBooking b : all) {
            if (b.isCheckedIn() && b.getDate().equals(today)) checkedIn[idx++] = b;
        }

        System.out.printf("| %-4s | %-8s | %-17s | %-12s | %-10s | %-10s |\n",
                "No.", "Room", "Time Slot", "User", "User ID", "Status");
        System.out.println("+------------------------------------------------------------------------------+");
        for (int i = 0; i < checkedIn.length; i++) {
            RoomBooking b = checkedIn[i];
            System.out.printf("| %-4d | %-8s | %-17s | %-12s | %-10s | %-10s |\n",
                    i + 1,
                    b.getRoomId(),
                    RoomBooking.slotLabel(b.getSlotHour()),
                    ConsoleUtil.truncate(b.getUsername(), 12),
                    b.getUserId(),
                    b.getStatus());
        }
        System.out.println("+==============================================================================+");
        System.out.print("> Enter booking number to check out (0 to go back): ");

        int sel = -1;
        try { sel = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { sel = -1; }

        if (sel == 0) return;
        if (sel < 1 || sel > checkedIn.length) {
            ConsoleUtil.printErrorBox("Invalid selection.");
            ConsoleUtil.enter(scanner);
            return;
        }

        RoomBooking chosen = checkedIn[sel - 1];
        System.out.printf("\n> Check out %s (%s) from %s at %s? (Y/N): ",
                chosen.getUsername(), chosen.getUserId(),
                chosen.getRoomId(), RoomBooking.slotLabel(chosen.getSlotHour()));

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Check-out aborted.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String result = roomControl.checkOut(chosen.getBookingId());
        switch (result) {
            case "CHECKED_OUT":
                System.out.println("\n+-------------------------------------------+");
                System.out.println("|        CHECK-OUT SUCCESSFUL               |");
                System.out.println("+-------------------------------------------+");
                System.out.printf("|  User    : %-30s |\n", ConsoleUtil.truncate(chosen.getUsername(), 30));
                System.out.printf("|  User ID : %-30s |\n", chosen.getUserId());
                System.out.printf("|  Room    : %-30s |\n", chosen.getRoomId());
                System.out.printf("|  Slot    : %-30s |\n", RoomBooking.slotLabel(chosen.getSlotHour()));
                System.out.printf("|  Status  : %-30s |\n", "In Use -> Completed");
                System.out.println("+-------------------------------------------+");
                break;
            case "NOT_CHECKED_IN":
                ConsoleUtil.printErrorBox("This booking has not been checked in yet.");
                break;
            case "NOT_FOUND":
                ConsoleUtil.printErrorBox("Booking not found.");
                break;
            default:
                ConsoleUtil.printErrorBox("Check-out failed: " + result);
        }
        ConsoleUtil.enter(scanner);
    }

    private void addRoom() {
        System.out.println("\n" + "+------------------------------------+");
        System.out.println("|       ADD NEW DISCUSSION ROOM      |");
        System.out.println("+------------------------------------+");

        viewAllRoomsInline();

        System.out.print("> Enter new room label (e.g. D, E) or 0 to cancel: ");
        String label = scanner.nextLine().trim();

        if (label.equals("0")) return;

        if (label.isEmpty()) {
            ConsoleUtil.printErrorBox("Room label cannot be empty.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String preview = "Room " + label.toUpperCase();
        System.out.printf("\n> Add \"%s\" to the system? (Y/N): ", preview);

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Cancelled.");
            ConsoleUtil.enter(scanner);
            return;
        }

        if (roomControl.addRoom(label)) {
            System.out.printf("\n[V] \"%s\" has been added successfully.\n", preview);
        } else {
            ConsoleUtil.printErrorBox("A room with that label already exists.");
        }
        ConsoleUtil.enter(scanner);
    }

    private void removeRoom() {
        System.out.println("\n" + "+------------------------------------+");
        System.out.println("|       REMOVE A DISCUSSION ROOM     |");
        System.out.println("+------------------------------------+");

        String[] rooms = roomControl.getAllRooms();
        if (rooms.length == 0) {
            System.out.println("|   [ No rooms registered ]          |");
            System.out.println("+------------------------------------+");
            ConsoleUtil.enter(scanner);
            return;
        }

        viewAllRoomsInline();

        System.out.print("> Select room number to remove (0 to cancel): ");
        int sel = -1;
        try { sel = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { sel = -1; }

        if (sel == 0) return;
        if (sel < 1 || sel > rooms.length) {
            ConsoleUtil.printErrorBox("Invalid selection.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String selectedRoom = rooms[sel - 1];
        System.out.printf("\n> Remove \"%s\" from the system? This cannot be done if\n", selectedRoom);
        System.out.println("  the room has active bookings. Confirm? (Y/N): ");
        System.out.print("> ");

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Removal cancelled.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String result = roomControl.removeRoom(selectedRoom);
        switch (result) {
            case "REMOVED":
                System.out.printf("\n[V] \"%s\" removed successfully.\n", selectedRoom);
                System.out.println("    Tip: Select 'Undo Last Room Removal' to restore it.");
                break;
            case "HAS_BOOKINGS":
                ConsoleUtil.printErrorBox("Cannot remove \"" + selectedRoom + "\":\n"
                        + " It has active bookings. Cancel them first.");
                break;
            case "NOT_FOUND":
                ConsoleUtil.printErrorBox("Room not found.");
                break;
            default:
                ConsoleUtil.printErrorBox("Removal failed: " + result);
        }
        ConsoleUtil.enter(scanner);
    }

    private void undoRemoveRoom() {
        System.out.println("\n" + "+-------------------------------------------+");
        System.out.println("|         UNDO LAST ROOM REMOVAL            |");
        System.out.println("+-------------------------------------------+");

        if (!roomControl.canUndoRemove()) {
            System.out.println("|   [ No room removal to undo ]             |");
            System.out.println("+-------------------------------------------+");
            ConsoleUtil.enter(scanner);
            return;
        }

        String roomToRestore = roomControl.peekUndoRoom();
        System.out.printf("\n  This will restore: \"%s\"\n", roomToRestore);
        System.out.print("> Proceed with undo? (Y/N): ");

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Undo cancelled.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String restored = roomControl.undoRemoveRoom();
        if (restored != null) {
            System.out.printf("\n[V] \"%s\" has been restored successfully.\n", restored);
        } else {
            ConsoleUtil.printErrorBox("Undo failed — nothing to restore.");
        }
        ConsoleUtil.enter(scanner);
    }

    private void checkInUser() {
        System.out.println("\n" + "+=============================================================================+");
        System.out.println("|                        CHECK-IN USER TO ROOM BOOKING                        |");
        System.out.println("+=============================================================================+");
        System.out.println("|  Only bookings for TODAY can be checked in.                                 |");
        System.out.println("+-----------------------------------------------------------------------------+");

        RoomBooking[] all         = roomControl.getAllActiveBookings();
        java.time.LocalDate today = java.time.LocalDate.now();

        int count = 0;
        for (RoomBooking b : all) {
            if (b.isActive() && b.getDate().equals(today)) count++;
        }

        if (count == 0) {
            System.out.println("|  [ No active bookings for today to check in ]                               |");
            System.out.println("+=============================================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        RoomBooking[] todayBookings = new RoomBooking[count];
        int idx = 0;
        for (RoomBooking b : all) {
            if (b.isActive() && b.getDate().equals(today)) todayBookings[idx++] = b;
        }

        System.out.printf("| %-4s | %-8s | %-17s | %-12s | %-10s | %-9s |\n",
                "No.", "Room", "Time Slot", "Booked By", "User ID", "Status");
        System.out.println("+-----------------------------------------------------------------------------+");
        for (int i = 0; i < todayBookings.length; i++) {
            RoomBooking b = todayBookings[i];
            System.out.printf("| %-4d | %-8s | %-17s | %-12s | %-10s | %-9s |\n",
                    i + 1,
                    b.getRoomId(),
                    RoomBooking.slotLabel(b.getSlotHour()),
                    ConsoleUtil.truncate(b.getUsername(), 12),
                    b.getUserId(),
                    b.getStatus());
        }
        System.out.println("+=============================================================================+");
        System.out.print("> Enter booking number to check in (0 to go back): ");

        int sel = -1;
        try { sel = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { sel = -1; }

        if (sel == 0) return;
        if (sel < 1 || sel > todayBookings.length) {
            ConsoleUtil.printErrorBox("Invalid selection.");
            ConsoleUtil.enter(scanner);
            return;
        }

        RoomBooking chosen = todayBookings[sel - 1];
        System.out.printf("\n> Check in %s (%s) for %s at %s? (Y/N): ",
                chosen.getUsername(), chosen.getUserId(),
                chosen.getRoomId(), RoomBooking.slotLabel(chosen.getSlotHour()));

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[!] Check-in aborted.");
            ConsoleUtil.enter(scanner);
            return;
        }

        String result = roomControl.checkIn(chosen.getBookingId());
        switch (result) {
            case "CHECKED_IN":
                System.out.println("\n+-------------------------------------------+");
                System.out.println("|         CHECK-IN SUCCESSFUL               |");
                System.out.println("+-------------------------------------------+");
                System.out.printf("|  User    : %-30s |\n", ConsoleUtil.truncate(chosen.getUsername(), 30));
                System.out.printf("|  User ID : %-30s |\n", chosen.getUserId());
                System.out.printf("|  Room    : %-30s |\n", chosen.getRoomId());
                System.out.printf("|  Slot    : %-30s |\n", RoomBooking.slotLabel(chosen.getSlotHour()));
                System.out.printf("|  Status  : %-30s |\n", "Active -> In Use");
                System.out.println("+-------------------------------------------+");
                break;
            case "ALREADY_CHECKED_IN":
                ConsoleUtil.printErrorBox("This booking is already checked in.");
                break;
            case "WRONG_DATE":
                ConsoleUtil.printErrorBox("Check-in is only allowed on the booking date.");
                break;
            case "NOT_ACTIVE":
                ConsoleUtil.printErrorBox("Booking is not in an active state.");
                break;
            default:
                ConsoleUtil.printErrorBox("Check-in failed: " + result);
        }
        ConsoleUtil.enter(scanner);
    }

    private void viewAllRooms() {
        System.out.println("\n" + "+==================================================================+");
        System.out.println("|              REGISTERED DISCUSSION ROOMS & LIVE STATUS           |");
        System.out.println("+==================================================================+");
        System.out.printf("|  Current time slot: %-45s|\n",
                RoomBooking.slotLabel(java.time.LocalTime.now().getHour()));
        System.out.println("+------------------------------------------------------------------+");

        RoomBookingControl.RoomStatus[] statuses = roomControl.getRoomStatuses();

        if (statuses.length == 0) {
            System.out.println("|  [ No rooms registered ]                                         |");
            System.out.println("+==================================================================+");
            ConsoleUtil.enter(scanner);
            return;
        }

        System.out.printf("| %-4s | %-10s | %-13s | %-15s | %-10s |\n",
                "No.", "Room", "Status", "Occupied By", "User ID");
        System.out.println("+------------------------------------------------------------------+");
        for (int i = 0; i < statuses.length; i++) {
            RoomBookingControl.RoomStatus s = statuses[i];
            String statusDisplay;
            switch (s.status) {
                case "In Use":  statusDisplay = "[~] In Use  "; break;
                case "Booked":  statusDisplay = "[B] Booked  "; break;
                default:        statusDisplay = "[V] Available"; break;
            }
            System.out.printf("| %-4d | %-10s | %-13s | %-15s | %-10s |\n",
                    i + 1,
                    s.roomLabel,
                    statusDisplay,
                    ConsoleUtil.truncate(s.bookedBy, 15),
                    s.userId);
        }
        System.out.println("+==================================================================+");
        System.out.printf("  Total rooms: %d\n", statuses.length);
        ConsoleUtil.enter(scanner);
    }

    private void viewAllRoomsInline() {
        RoomBookingControl.RoomStatus[] statuses = roomControl.getRoomStatuses();
        System.out.printf("| %-4s | %-10s | %-13s |\n", "No.", "Room", "Current Status");
        System.out.println("+------------------------------------+");
        for (int i = 0; i < statuses.length; i++) {
            RoomBookingControl.RoomStatus s = statuses[i];
            String statusDisplay;
            switch (s.status) {
                case "In Use":  statusDisplay = "[~] In Use   "; break;
                case "Booked":  statusDisplay = "[B] Booked   "; break;
                default:        statusDisplay = "[V] Available"; break;
            }
            System.out.printf("| %-4d | %-10s | %-14s |\n", i + 1, s.roomLabel, statusDisplay);
        }
        System.out.println("+------------------------------------+");
        System.out.printf("  Total rooms: %d\n\n", statuses.length);
    }

    private void printBookingTable(RoomBooking[] bookings) {
        System.out.printf("| %-4s | %-8s | %-10s | %-17s | %-12s | %-10s | %-9s |\n",
                "No.", "Room", "Date", "Time Slot", "Booked By", "User ID", "Status");
        System.out.println("+------------------------------------------------------------------------------------------+");
        for (int i = 0; i < bookings.length; i++) {
            RoomBooking b = bookings[i];
            System.out.printf("| %-4d | %-8s | %-10s | %-17s | %-12s | %-10s | %-9s |\n",
                    i + 1,
                    b.getRoomId(),
                    b.getDate().toString(),
                    RoomBooking.slotLabel(b.getSlotHour()),
                    ConsoleUtil.truncate(b.getUsername(), 12),
                    b.getUserId(),
                    b.getStatus());
        }
    }
}