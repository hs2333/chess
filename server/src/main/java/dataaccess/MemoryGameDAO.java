package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    //clear
    public void clear() {
        games.clear();
        nextId.set(1);
    }

    //Game
    public int createGame(GameData game) {
        int id = nextId.getAndIncrement();
        games.put(id, game);
        return id;
    }

//    public GameData getGame(int id) {
//        return games.get(id);
//    }

    public Map<Integer, GameData> listGames() {
        return games;
    }

    public void joinGame(int gameID, String username, String color) throws DataAccessException {
        //get game from the map
        GameData game = games.get(gameID);
        if (game == null)
        {throw new DataAccessException("Error: bad request");}
        //handle joining (white)
        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.whiteUsername() != null)
            {throw new DataAccessException("Error: already taken");}
            //update (new white)
            games.put(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
        }
        //handle joining (black)
        else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.blackUsername() != null)
            {throw new DataAccessException("Error: already taken");}
            //update (new black)
            games.put(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
        } else {
            throw new DataAccessException("Error: bad request");
        }
    }

}
