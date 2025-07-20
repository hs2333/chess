package dataaccess;

public class DataAccessFactory {
    private static final boolean useSQL = true; // set to false for memory mode

    public static UserDAO getUserDAO() {
        return useSQL ? new MySqlUserDAO() : new MemoryUserDAO();
    }

    public static AuthDAO getAuthDAO() {
        return useSQL ? new MySqlAuthDAO() : new MemoryAuthDAO();
    }

    public static GameDAO getGameDAO() {
        return useSQL ? new MySqlGameDAO() : new MemoryGameDAO();
    }
}
