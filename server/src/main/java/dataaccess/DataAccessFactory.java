package dataaccess;

public class DataAccessFactory {
    private static boolean USE_SQL = true; // set to false for memory mode

    private static final MemoryUserDAO MUDAO = new MemoryUserDAO();
    private static final MemoryAuthDAO MADAO = new MemoryAuthDAO();
    private static final MemoryGameDAO MGDAO = new MemoryGameDAO();

    public static void configure(boolean useSqlPersistence) {
        USE_SQL = useSqlPersistence;
    }

    public static boolean usingSQL() {
        return USE_SQL;
    }

    public static UserDAO getUserDAO() {
        return USE_SQL ? new MySqlUserDAO() : new MemoryUserDAO();
    }
    public static AuthDAO getAuthDAO() {
        return USE_SQL ? new MySqlAuthDAO() : new MemoryAuthDAO();
    }
    public static GameDAO getGameDAO() {
        return USE_SQL ? new MySqlGameDAO() : new MemoryGameDAO();
    }

    public static void resetMemoryDAOs() {
        MUDAO.clear();
        MADAO.clear();
        MGDAO.clear();
    }
}
