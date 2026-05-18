// Author: Adam Ho Swee En

package control;

import adt.*;
import entity.*;
import utility.*;


public class AdminControl {
    private LinkedHashMap<String, User> userMap;
    private LinkedHashMap<String, Book> bookMap;
    private LinkedHashMap<Integer, UndoEntry> undoHistory = new LinkedHashMap<>();
    private int undoCount = 0;
    
    private class UndoEntry{
        String type;
        User backup;
        UndoEntry(String type, User backup){
            this.type = type;
            this.backup = backup;
        } 
    }

    public AdminControl(LinkedHashMap<String, User> userMap,  LinkedHashMap<String, Book> bookMap) {
        this.userMap = userMap;
        this.bookMap = bookMap;
    }
    
    public User getUserById(String userId) {
        return userMap.get(userId);
    }
    
    public void saveUser(User user) {
        User existing = userMap.get(user.getUserId());
        
        if (existing != null){
            User oldBackup = new User(existing.getUserId(), existing.getUsername(), existing.getEmail(), existing.getPassword(), existing.getRole());
            pushUndo("Update", oldBackup);
        } else {
            pushUndo("Add", user);
        }
        userMap.put(user.getUserId(), user);
    }

    public boolean deleteUser(String userId) {
        User toDelete = userMap.get(userId);
        if (toDelete == null) return false;
        
        if (!isUserClearForDeletion(userId)) {
            return false;
        }
        
        pushUndo("Delete", toDelete);
        userMap.remove(userId);
        return true;
    }
    
    private void pushUndo(String type, User user){
        undoCount++;
        undoHistory.put(undoCount, new UndoEntry(type, user));
    }
    
    public String undo(){
        if (undoHistory.isEmpty()) return "Nothing to undo.";
        
        UndoEntry last = undoHistory.get(undoCount);
        undoHistory.remove(undoCount);
        undoCount--;
        
        User user = last.backup;
        switch(last.type){
            case "Add": 
                userMap.remove(user.getUserId());
                return "Registration of " + user.getUserId() + " removed.";
            case "Delete":
            case "Update":
                userMap.put(user.getUserId(), user);
                return "Data for " + user.getUserId() + " restored.";
            default:
                return "Undo failed.";
        }
    }

    public boolean canUndo() {
        return !undoHistory.isEmpty();
    }
    
    public boolean isIdTaken(String userId) {
        return userMap.containsKey(userId);
    }

    public void listAllUsers() {
        if (userMap.isEmpty()) {
            System.out.println("\n[!] The user database is currently empty.");
        } else {
            System.out.println("\n--- Current User Registry (Registration Order) ---");
            userMap.printInOrder();
        }
    }
    
    public void printTable(){
        userMap.printTableFormat();
    }
    
    public void printSortedTable(String sortOption) {
        if (userMap.isEmpty()) {
            System.out.println("| Database is currently empty.                                                             |");
            return;
        }

        Object[] rawArray = userMap.toArray();
        User[] users = new User[rawArray.length];
        for (int i = 0; i < rawArray.length; i++) {
            users[i] = (User) rawArray[i];
        }

        switch (sortOption) {
            case "1": break;

            case "2": 
                for (int i = 0; i < users.length / 2; i++) {
                    User temp = users[i];
                    users[i] = users[users.length - 1 - i];
                    users[users.length - 1 - i] = temp;
                }
                break;

            case "3": 
                userMap.mergeSort(users, 0, users.length - 1, (u1, u2) -> 
                    u1.getUsername().compareToIgnoreCase(u2.getUsername()));
                break;

            case "4": 
                userMap.mergeSort(users, 0, users.length - 1, (u1, u2) -> {
                    int roleCompare = u1.getRole().compareTo(u2.getRole());
                    if (roleCompare == 0) {
                        return u1.getUsername().compareTo(u2.getUsername());
                    }
                    return roleCompare;
                });
                break;
        }

        for (User u : users) {
            System.out.printf("| %-10s | %-20s | %-25s | %-12s | %-12s |\n",
                    u.getUserId(), ConsoleUtil.truncate(u.getUsername(), 20), ConsoleUtil.truncate(u.getEmail(), 25), u.getRole(), u.getRegistrationDate());
        }
    }
    
    public boolean isUserInAnyWaitlist(String userId) {
        Object [] allBooks = bookMap.toArray();
        
        for(Object obj : allBooks) {
            Book book = (Book) obj;
            
            if (book.isUserInWaitlist(userId)){
                return true;
            }
        }
        return false;
    }
    
    public boolean isUserClearForDeletion(String userId) {
        if (!userMap.containsKey(userId)) return false;
        
        User current = userMap.get(userId);
        
        return current.getActiveBorrowCount() == 0 && !isUserInAnyWaitlist(userId);
    }
    
    public User[] searchUsersByCriteria(String query, String type) {
        Object[] allObjects = userMap.toArray();

        int matchCount = 0;
        for (Object obj : allObjects) {
            User u = (User) obj;
            if (isMatch(u, query, type)) {
                matchCount++;
            }
        }

        User[] results = new User[matchCount];
        int index = 0;

        for (Object obj : allObjects) {
            User u = (User) obj;
            if (isMatch(u, query, type)) {
                results[index++] = u;
            }
        }

        return results;
    }
    
    private boolean isMatch(User u, String query, String type) {
        String q = query.toLowerCase();
        switch (type.toLowerCase()) {
            case "name":  return u.getUsername().toLowerCase().contains(q);
            case "email": return u.getEmail().toLowerCase().contains(q);
            case "role":  return u.getRole().equalsIgnoreCase(q);
            default:      return false;
        }
    }
}
