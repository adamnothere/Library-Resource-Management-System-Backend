// Author: Adam Ho Swee En

package control;

import adt.*;
import entity.*;

public class LoginControl {

    private LinkedHashMap<String, User> userMap;
    private LinkedHashMap<String, Book> bookMap;
    private User currentUser;

    private RoomBookingControl roomBookingControl; 

    public LoginControl(LinkedHashMap<String, User> userMap, LinkedHashMap<String, Book> bookMap) {
        this.userMap = userMap;
        this.bookMap = bookMap;
        this.currentUser = null;
        this.roomBookingControl = new RoomBookingControl(); 
    }

    public AdminControl getAdminControl() {
        return new AdminControl(this.userMap, this.bookMap);
    }

    public MaintainBookControl getBookControl() {
        return new MaintainBookControl(this.bookMap);
    }

    public TransactionControl getTransactionControl() {
        MaintainBookControl bookControl = getBookControl();
        return new TransactionControl(bookControl, this.userMap, this.currentUser);
    }

    public RegisterControl getRegisterControl() {
        return new RegisterControl(this.userMap);
    }

    public UserControl getUserControl() {
        return new UserControl(this.userMap);
    }

    public ReportControl getReportControl() {
        return new ReportControl(getBookControl(), this.userMap);
    }

    public RoomBookingControl getRoomBookingControl() {
        return roomBookingControl;
    }

    public User getUserByUserID(String userId) {
        if (userId == null || userId.isEmpty()) return null;
        return userMap.get(userId);
    }

    public boolean processLogin(String userId, String password) {
        User user = userMap.get(userId);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logOut() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}