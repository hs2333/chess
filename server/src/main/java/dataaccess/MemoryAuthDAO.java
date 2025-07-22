package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private static final HashMap<String, AuthData> TOKENS = new HashMap<>();
    //insertAuth
    public void insertAuth(AuthData auth) {
        TOKENS.put(auth.authToken(), auth);
    }

    //getAuth
    public AuthData getAuth(String token) {
        return TOKENS.get(token);
    }

    //clear and deleteAuth
    public void clear() {
        TOKENS.clear();
    }

    public void deleteAuth(String token) {
        TOKENS.remove(token);
    }

    //check isValidToken
    public boolean isValidToken(String token) {
        return TOKENS.containsKey(token);
    }
}
