package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private static final HashMap<String, UserData> users = new HashMap<>();
    public void insertUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: already taken");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public boolean validateUser(String username, String password) throws DataAccessException {
        boolean exist = false;
        for (UserData user : users.values()) {
            if (user.username().equals(username)) {
                exist = true;
//                System.out.println("Stored password: " + user.password());
//                System.out.println("Provided password: " + password);
                if (BCrypt.checkpw(password, user.password())) {
                    return true;
                }
            }}
        //incorrect password
        if (exist) {
            return false;
        } else {
            throw new DataAccessException("User does not exist: " + username);
        }
    }

    public void clear() {
        users.clear();
    }
}