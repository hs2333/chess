package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO {
    private final HashMap<String, AuthData> tokens = new HashMap<>();

    public void insertAuth(AuthData auth) {
        tokens.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String token) {
        return tokens.get(token);
    }

    public void clear() {
        tokens.clear();
    }

    public void deleteAuth(String token) {
        tokens.remove(token);
    }

    public boolean isValidToken(String token) {
        return tokens.containsKey(token);
    }

}
