package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> tokens = new HashMap<>();
    //createAuth
    public void createAuth(AuthData auth) {
        tokens.put(auth.authToken(), auth);
    }

    //getAuth
    public AuthData getAuth(String token) {
        return tokens.get(token);
    }

    //clear and deleteAuth
    public void clear() {
        tokens.clear();
    }
    public void deleteAuth(String token) {
        tokens.remove(token);
    }

    //check isValidToken
    public boolean isValidToken(String token) {
        return tokens.containsKey(token);
    }

}
