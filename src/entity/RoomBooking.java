package entity;

import java.time.LocalDate;

public class RoomBooking {

    private String bookingId;   
    private String roomId;      
    private String userId;
    private String username;
    private LocalDate date;
    private int slotHour;       
    private String status;     
    private LocalDate bookedOn;

    public RoomBooking(String roomId, String userId, String username,
                       LocalDate date, int slotHour) {
        this.roomId    = roomId;
        this.userId    = userId;
        this.username  = username;
        this.date      = date;
        this.slotHour  = slotHour;
        this.status    = "Active";
        this.bookedOn  = LocalDate.now();
        this.bookingId = generateId(roomId, date, slotHour);
    }

    public static String generateId(String roomId, LocalDate date, int slotHour) {
        return roomId.replace(" ", "") + "_" + date.toString() + "_" + String.format("%02d", slotHour);
    }

    public static String slotLabel(int hour) {
        return String.format("%02d:00 - %02d:00", hour, hour + 1);
    }

    public void cancel() {
        this.status = "Cancelled";
    }

    public void checkIn() {
        this.status = "CheckedIn";
    }

    public void checkOut() {
        this.status = "Completed";
    }

    public String   getBookingId() { return bookingId; }
    public String   getRoomId()    { return roomId; }
    public String   getUserId()    { return userId; }
    public String   getUsername()  { return username; }
    public LocalDate getDate()     { return date; }
    public int      getSlotHour()  { return slotHour; }
    public String   getStatus()    { return status; }
    public LocalDate getBookedOn() { return bookedOn; }

    public boolean isActive()    { return status.equals("Active"); }
    public boolean isCheckedIn() { return status.equals("CheckedIn"); }
    public boolean isCompleted() { return status.equals("Completed"); }

    @Override
    public String toString() {
        return String.format("| %-8s | %-10s | %-17s | %-10s |",
                roomId, date.toString(), slotLabel(slotHour), status);
    }
}