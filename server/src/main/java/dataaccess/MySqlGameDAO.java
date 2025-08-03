package dataaccess;
import model.GameData;
import java.sql.*;
import java.util.*;
import com.google.gson.Gson;

//similar to MemoryXDAO
//MySQL implementation of the GameDAO interface
public class MySqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    //inserts a new game record into the database
    public GameData createGame(GameData game) throws DataAccessException {
        var sql = "INSERT INTO game (gameName, whiteUsername, blackUsername, gameJSON) VALUES (?, ?, ?, ?)";
        if (game == null) {
            throw new DataAccessException("GameData cannot be null");}
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int gameID = keys.getInt(1);
                    return new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
                } else {
                    throw new DataAccessException("Failed to get generated game ID");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to insert game", e);
        }
    }

    @Override
    //retrieves a specific game by its ID
    public GameData getGame(int gameID) throws DataAccessException {
        var sql = "SELECT * FROM game WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var gameObj = gson.fromJson(rs.getString("gameJSON"), chess.ChessGame.class);
                    return new GameData(rs.getInt("id"), rs.getString("whiteUsername"),
                            rs.getString("blackUsername"), rs.getString("gameName"), gameObj);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }
    }

    @Override
    //returns a collection of all games currently stored in the database
    public Collection<GameData> listGames() throws DataAccessException {
        var sql = "SELECT * FROM game";
        var games = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                var gameObj = gson.fromJson(rs.getString("gameJSON"), chess.ChessGame.class);
                games.add(new GameData(rs.getInt("id"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), gameObj));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
        return games;
    }

    @Override
    //updates the existing game information in the database
    public void updateGame(GameData game) throws DataAccessException {
        var sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJSON = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.setInt(5, game.gameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    @Override
    //call records from the game table in the database
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM game")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear game table", e);
        }
    }

    @Override
    public void setGameOver(int gameID, boolean gameOver) throws DataAccessException {
        String query = "UPDATE game SET gameOver = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, gameOver);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating gameOver flag", e);
        }
    }

    @Override
    public boolean getGameOver(int gameID) throws DataAccessException {
        String query = "SELECT gameOver FROM game WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("gameOver");
            } else {
                throw new DataAccessException("Game not found for ID: " + gameID);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading gameOver flag", e);
        }
    }

}
