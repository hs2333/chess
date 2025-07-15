package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    public void clear() {
        games.clear();
        nextId.set(1);
    }

    public int createGame(GameData game) {
        int id = nextId.getAndIncrement();
        games.put(id, game);
        return id;
    }

    public GameData getGame(int id) {
        return games.get(id);
    }

    public Map<Integer, GameData> listGames() {
        return games;
    }
}
