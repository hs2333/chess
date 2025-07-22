package dataaccess;

public class DataAccessFactory {
    private static boolean useSQL = true; // set to false for memory mode

    private static final MemoryUserDAO MUDAO = new MemoryUserDAO();
    private static final MemoryAuthDAO MADAO = new MemoryAuthDAO();
    private static final MemoryGameDAO MGDAO = new MemoryGameDAO();

    public static void configure(boolean useSqlPersistence) {
        useSQL = useSqlPersistence;
    }

    public static boolean usingSQL() {
        return useSQL;
    }

    public static UserDAO getUserDAO() {
        return useSQL ? new MySqlUserDAO() : new MemoryUserDAO();
    }
    public static AuthDAO getAuthDAO() {
        return useSQL ? new MySqlAuthDAO() : new MemoryAuthDAO();
    }
    public static GameDAO getGameDAO() {
        return useSQL ? new MySqlGameDAO() : new MemoryGameDAO();
    }

    public static void resetMemoryDAOs() {
        MUDAO.clear();
        MADAO.clear();
        MGDAO.clear();
    }
}
