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

    //for some reason, these two gameover methods cannot be used. They have been disgarded. I created an internal variable in WebSocketHandler.
    //but it's hard to remove them since I've updated them in other places,
    //so just leave them here but don't call them don't!
    void setGameOver(int gameID, boolean gameOver) throws DataAccessException;
    boolean getGameOver(int gameID) throws DataAccessException;
}

