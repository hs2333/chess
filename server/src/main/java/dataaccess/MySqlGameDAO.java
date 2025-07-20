package dataaccess;

import model.GameData;
import java.sql.*;
import java.util.*;

import com.google.gson.Gson;

public class MySqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        var sql = "INSERT INTO game (gameName, whiteUsername, blackUsername, gameJSON) VALUES (?, ?, ?, ?)";
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
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM game")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear game table", e);
        }
    }
}
