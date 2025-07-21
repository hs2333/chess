package dataaccess;

import model.GameData;

import java.util.Collection;

//GameDAO interface
public interface GameDAO {
    GameData createGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
}

