package dataaccess;
import model.AuthData;
import java.sql.*;

//similar to MemoryXDAO
//MySQL implementation of the AuthDAO interface
public class MySqlAuthDAO implements AuthDAO {

    @Override
    //insert a new auth token into the database
    public void insertAuth(AuthData token) throws DataAccessException {
        var sql = "INSERT INTO auth (token, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token.authToken());
            stmt.setString(2, token.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert auth token", e);
        }
    }

    @Override
    //retrieves the AuthData associated with a token
    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT * FROM auth WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("token"), rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth token", e);
        }
    }

    @Override
    //check isValidToken
    public boolean isValidToken(String authToken) throws DataAccessException {
        return getAuth(authToken) != null;
    }

    @Override
    //deletes an auth token from the database
    public void deleteAuth(String authToken) throws DataAccessException {
        var sql = "DELETE FROM auth WHERE token = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth token", e);
        }
    }

    @Override
    //clear auth tokens in the database (delete all)
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM auth")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth table", e);
        }
    }
}
