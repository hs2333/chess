package dataaccess;

public class DataAccessFactory {
    private static final boolean USE_SQL = true; // set to false for memory mode

    public static UserDAO getUserDAO() {
        return USE_SQL ? new MySqlUserDAO() : new MemoryUserDAO();
    }
    public static AuthDAO getAuthDAO() {
        return USE_SQL ? new MySqlAuthDAO() : new MemoryAuthDAO();
    }
    public static GameDAO getGameDAO() {
        return USE_SQL ? new MySqlGameDAO() : new MemoryGameDAO();
    }
}
