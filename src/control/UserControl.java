package control;

import entity.User;
import adt.*;

public class UserControl {
    
    private LinkedHashMap<String, User> userMap;

    public UserControl(LinkedHashMap<String, User> userMap) {
        this.userMap = userMap;
    }
    
    public boolean updateProfile(User currentUser){
        if (currentUser != null && userMap.containsKey(currentUser.getUserId())) {
            userMap.put(currentUser.getUserId(), currentUser);
            return true;
        }
        return false;
    }
    
    public boolean changePassword(User currentUser, String oldPass, String newPass) {
        if (currentUser.getPassword().equals(oldPass)) {
            currentUser.setPassword(newPass);
            return updateProfile(currentUser);
        }
        return false;
    }
}
