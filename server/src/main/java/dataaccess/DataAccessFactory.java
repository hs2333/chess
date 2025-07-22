package dataaccess;

public class DataAccessFactory {
    private static final boolean UseSQL = true; // set to false for memory mode

    public static UserDAO getUserDAO() {
        return UseSQL ? new MySqlUserDAO() : new MemoryUserDAO();
    }
    public static AuthDAO getAuthDAO() {
        return UseSQL ? new MySqlAuthDAO() : new MemoryAuthDAO();
    }
    public static GameDAO getGameDAO() {
        return UseSQL ? new MySqlGameDAO() : new MemoryGameDAO();
    }
}
