// Author： Elisha Loh Tien Rong
package control;

import adt.LinkedHashMap;
import entity.RoomBooking;
import entity.User;
import java.time.LocalDate;

public class RoomBookingControl {

    public static final int SLOT_START = 9;
    public static final int SLOT_END = 17;
    public static final int MAX_DAYS_AHEAD = 7;

    private LinkedHashMap<String, RoomBooking> bookingMap;
    private LinkedHashMap<String, String> roomMap;
    private LinkedHashMap<Integer, String[]> roomUndoStack;
    private int undoCount = 0;

    public RoomBookingControl() {
        this.bookingMap = new LinkedHashMap<>();
        this.roomMap = new LinkedHashMap<>();
        this.roomUndoStack = new LinkedHashMap<>();

        roomMap.put("RoomA", "Room A");
        roomMap.put("RoomB", "Room B");
        roomMap.put("RoomC", "Room C");
    }

    public boolean addRoom(String label) {
        if (label == null || label.trim().isEmpty()) return false;
        String key = "Room" + label.trim().toUpperCase().replace(" ", "");
        if (roomMap.containsKey(key)) return false;
        roomMap.put(key, "Room " + label.trim().toUpperCase());
        return true;
    }

    public String removeRoom(String roomLabel) {
        String key = roomLabel.replace(" ", "");
        if (!roomMap.containsKey(key)) return "NOT_FOUND";

        if (roomHasActiveBookings(roomLabel)) return "HAS_BOOKINGS";

        String label = roomMap.get(key);
        roomMap.remove(key);

        undoCount++;
        roomUndoStack.put(undoCount, new String[]{key, label});

        return "REMOVED";
    }

    public String undoRemoveRoom() {
        if (roomUndoStack.isEmpty()) return null;

        String[] entry = roomUndoStack.get(undoCount);
        roomUndoStack.remove(undoCount);
        undoCount--;

        roomMap.put(entry[0], entry[1]);
        return entry[1];
    }

    public boolean canUndoRemove() {
        return !roomUndoStack.isEmpty();
    }

    public String peekUndoRoom() {
        if (roomUndoStack.isEmpty()) return null;
        return roomUndoStack.get(undoCount)[1];
    }

    public String[] getAllRooms() {
        Object[] raw = roomMap.toArray();
        String[] rooms = new String[raw.length];
        for (int i = 0; i < raw.length; i++) rooms[i] = (String) raw[i];
        return rooms;
    }

    public int getRoomCount() { 
        return roomMap.size(); 
    }

    private boolean roomHasActiveBookings(String roomLabel) {
        Object[] all = bookingMap.toArray();
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if ((b.isActive() || b.isCheckedIn()) && b.getRoomId().equals(roomLabel)) return true;
        }
        return false;
    }

    public String checkOut(String bookingId) {
        RoomBooking b = bookingMap.get(bookingId);
        if (b == null) return "NOT_FOUND";
        if (!b.isCheckedIn()) return "NOT_CHECKED_IN";
        b.checkOut();
        return "CHECKED_OUT";
    }

    public String checkIn(String bookingId) {
        RoomBooking b = bookingMap.get(bookingId);
        if (b == null) return "NOT_FOUND";
        if (b.isCheckedIn()) return "ALREADY_CHECKED_IN";
        if (!b.isActive()) return "NOT_ACTIVE";
        if (!b.getDate().equals(LocalDate.now())) return "WRONG_DATE";
        b.checkIn();
        return "CHECKED_IN";
    }

    public RoomStatus[] getRoomStatuses() {
        String[] rooms = getAllRooms();
        LocalDate today = LocalDate.now();
        int currentHour = java.time.LocalTime.now().getHour();

        RoomStatus[] statuses = new RoomStatus[rooms.length];
        Object[] allBookings = bookingMap.toArray();

        for (int r = 0; r < rooms.length; r++) {
            String roomLabel = rooms[r];
            String status = "Available";
            String bookedBy = "-";
            String userId = "-";

            for (Object obj : allBookings) {
                RoomBooking b = (RoomBooking) obj;
                if (!b.getRoomId().equals(roomLabel)) continue;
                if (!b.getDate().equals(today)) continue;
                if (b.getSlotHour() != currentHour) continue;

                if (b.isCheckedIn()) {
                    status = "In Use";
                    bookedBy = b.getUsername();
                    userId = b.getUserId();
                    break;
                } else if (b.isActive()) {
                    status = "Booked";
                    bookedBy = b.getUsername();
                    userId = b.getUserId();
                }
            }
            statuses[r] = new RoomStatus(roomLabel, status, bookedBy, userId, slotLabel(currentHour));
        }
        return statuses;
    }

    private static String slotLabel(int hour) {
        return RoomBooking.slotLabel(hour);
    }

    public static class RoomStatus {
        public final String roomLabel;
        public final String status;
        public final String bookedBy;
        public final String userId;
        public final String currentSlot;

        public RoomStatus(String roomLabel, String status, String bookedBy, String userId, String currentSlot) {
            this.roomLabel = roomLabel;
            this.status = status;
            this.bookedBy = bookedBy;
            this.userId = userId;
            this.currentSlot = currentSlot;
        }
    }

    public String book(String roomLabel, String userId, String username, LocalDate date, int slotHour) {

        if (!isValidRoom(roomLabel)) return "INVALID_ROOM";
        if (!isValidDate(date)) return "INVALID_DATE";
        if (!isValidSlot(slotHour)) return "INVALID_SLOT";

        String bookingId = RoomBooking.generateId(roomLabel.replace(" ", ""), date, slotHour);

        RoomBooking existing = bookingMap.get(bookingId);
        if (existing != null && existing.isActive()) return "ROOM_TAKEN";

        if (userHasConflict(userId, date, slotHour)) return "USER_CONFLICT";

        RoomBooking booking = new RoomBooking(roomLabel, userId, username, date, slotHour);
        bookingMap.put(booking.getBookingId(), booking);
        return "BOOKED";
    }

    public String cancel(String bookingId, String requesterId, boolean isAdmin) {
        RoomBooking b = bookingMap.get(bookingId);
        if (b == null) return "NOT_FOUND";
        if (b.isCheckedIn()) return "ALREADY_CHECKED_IN";
        if (b.isCompleted()) return "ALREADY_COMPLETED";
        if (!b.isActive()) return "ALREADY_CANCELLED";
        if (!isAdmin && !b.getUserId().equals(requesterId)) return "NOT_OWNER";
        b.cancel();
        return "CANCELLED";
    }

    public RoomBooking[] getActiveBookingsForUser(String userId) {
        Object[] all = bookingMap.toArray();
        LocalDate today = LocalDate.now();

        int count = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive() && b.getUserId().equals(userId)
                    && !b.getDate().isBefore(today)) count++;
        }

        RoomBooking[] result = new RoomBooking[count];
        int i = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive() && b.getUserId().equals(userId)
                    && !b.getDate().isBefore(today)) result[i++] = b;
        }
        sortByDateTime(result);
        return result;
    }

    public RoomBooking[] getAllBookingsForUser(String userId) {
        Object[] all = bookingMap.toArray();
        int count = 0;
        for (Object obj : all) {
            if (((RoomBooking) obj).getUserId().equals(userId)) count++;
        }
        RoomBooking[] result = new RoomBooking[count];
        int i = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.getUserId().equals(userId)) result[i++] = b;
        }
        sortByDateTime(result);
        return result;
    }

    public RoomBooking[] getAllActiveBookings() {
        Object[] all = bookingMap.toArray();
        int count = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive() || b.isCheckedIn()) count++;
        }
        RoomBooking[] result = new RoomBooking[count];
        int i = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive() || b.isCheckedIn()) result[i++] = b;
        }
        sortByDateTime(result);
        return result;
    }

    public RoomBooking[] getAllBookings() {
        Object[] all = bookingMap.toArray();
        RoomBooking[] result = new RoomBooking[all.length];
        for (int i = 0; i < all.length; i++) result[i] = (RoomBooking) all[i];
        sortByDateTime(result);
        return result;
    }

    public boolean[][] getAvailabilityGrid(LocalDate date, String[] roomLabels) {
        int slots = SLOT_END - SLOT_START + 1;
        boolean[][] grid = new boolean[roomLabels.length][slots];

        Object[] all = bookingMap.toArray();
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (!b.isActive() || !b.getDate().equals(date)) continue;
            for (int r = 0; r < roomLabels.length; r++) {
                if (b.getRoomId().equals(roomLabels[r])) {
                    grid[r][b.getSlotHour() - SLOT_START] = true;
                }
            }
        }
        return grid;
    }

    public boolean isValidSlot(int hour) {
        return hour >= SLOT_START && hour <= SLOT_END;
    }

    public boolean isValidDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate max   = today.plusDays(MAX_DAYS_AHEAD);
        return !date.isBefore(today) && !date.isAfter(max);
    }

    public boolean isValidRoom(String roomLabel) {
        String key = roomLabel.replace(" ", "");
        return roomMap.containsKey(key);
    }

    private boolean userHasConflict(String userId, LocalDate date, int slotHour) {
        Object[] all = bookingMap.toArray();
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive() && b.getUserId().equals(userId)
                    && b.getDate().equals(date) && b.getSlotHour() == slotHour) {
                return true;
            }
        }
        return false;
    }

    private void sortByDateTime(RoomBooking[] arr) {
        if (arr.length < 2) return;
        bookingMap.mergeSort(arr, 0, arr.length - 1, (a, b) -> {
            int dateCmp = a.getDate().compareTo(b.getDate());
            if (dateCmp != 0) return dateCmp;
            return Integer.compare(a.getSlotHour(), b.getSlotHour());
        });
    }
    
    public RoomBooking[] getCancellableBookings() {
        Object[] all = bookingMap.toArray();
        int count = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive()) count++;
        }
        RoomBooking[] result = new RoomBooking[count];
        int i = 0;
        for (Object obj : all) {
            RoomBooking b = (RoomBooking) obj;
            if (b.isActive()) result[i++] = b;
        }
        sortByDateTime(result);
        return result;
    }
}