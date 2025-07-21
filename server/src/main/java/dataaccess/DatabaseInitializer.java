package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            //create user table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS user (
                    username     VARCHAR(255) PRIMARY KEY,
                    password     VARCHAR(255) NOT NULL,
                    email        VARCHAR(255) NOT NULL
                );
            """);


            //create auth table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS auth (
                    token        VARCHAR(255) PRIMARY KEY,
                    username     VARCHAR(255) NOT NULL,
                    FOREIGN KEY (username) REFERENCES user(username) ON DELETE CASCADE
                );
            """);


            //create game table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS game (
                    id           INT AUTO_INCREMENT PRIMARY KEY,
                    gameName     VARCHAR(255) NOT NULL,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameJSON     TEXT,
                    FOREIGN KEY (whiteUsername) REFERENCES user(username) ON DELETE SET NULL,
                    FOREIGN KEY (blackUsername) REFERENCES user(username) ON DELETE SET NULL
                );
            """);

            
        } catch (SQLException e) {
            throw new DataAccessException("Failed to initialize database", e);
        }
    }
}
