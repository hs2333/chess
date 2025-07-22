package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO implements GameDAO {
    private static final Map<Integer, GameData> GAMES = new HashMap<>();
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);

    //clear
    public void clear() {
        GAMES.clear();
        NEXT_ID.set(1);
    }

    //Game
    public GameData createGame(GameData game) {
        int id = NEXT_ID.getAndIncrement();
        GameData newGame = new GameData(id, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        GAMES.put(id, newGame);
        return newGame;
    }

    public GameData getGame(int id) {return GAMES.get(id);}

    public Collection<GameData> listGames() { return GAMES.values(); }

    public void joinGame(int gameID, String username, String color) throws DataAccessException {
        //get game from the map
        GameData game = GAMES.get(gameID);
        if (game == null)
        {throw new DataAccessException("Error: bad request");}
        //handle joining (white)
        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.whiteUsername() != null)
            {throw new DataAccessException("Error: already taken");}
            //update (new white)
            GAMES.put(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
        }
        //handle joining (black)
        else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.blackUsername() != null)
            {throw new DataAccessException("Error: already taken");}
            //update (new black)
            GAMES.put(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

    @Override
    public void updateGame(GameData game) {
        GAMES.put(game.gameID(), game);
    }

}
