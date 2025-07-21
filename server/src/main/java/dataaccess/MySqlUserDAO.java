package dataaccess;
import model.UserData;
import java.sql.*;

//similar to MemoryXDAO
public class MySqlUserDAO implements UserDAO {

    @Override
    //inserts a new user into the database
    public void insertUser(UserData user) throws DataAccessException {
        var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, user.password()); // Hash before calling this!
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert user", e);
        }
    }

    @Override
    //retrieves a user from the database by username
    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user", e);
        }
    }

    @Override
    //check validateUser
    public boolean validateUser(String username, String password) throws DataAccessException {
            var sql = "SELECT password FROM user WHERE username = ?";

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");
                        if (org.mindrot.jbcrypt.BCrypt.checkpw(password, hashedPassword)) {
                            return true;
                        } else {
                            return false; // user exists, wrong password
                        }
                    } else {
                        throw new DataAccessException("User does not exist: " + username);
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException("Failed to authenticate user", e);
            }
        }

    @Override
    //clear all users from the user table
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM user")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear user table", e);
        }
    }
}
