// Author: Adam Ho Swee En

package control;

import adt.*;
import entity.User;

public class RegisterControl {
    private LinkedHashMap<String, User> userMap;

    public RegisterControl(LinkedHashMap<String, User> userMap) {
        this.userMap = userMap;
    }
    
    public void registerUser(User user) {
        if (user == null) return;
        userMap.put(user.getUserId(), user);
    }
    
    public boolean isIdAvailable(String id) {
        return !userMap.containsKey(id);
    }
    
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 5;
    }
}
