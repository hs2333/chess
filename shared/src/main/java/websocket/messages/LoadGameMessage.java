package websocket.messages;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private final GameData game;

    public LoadGameMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        if (game == null || game.game() == null) {
            throw new IllegalArgumentException("Invalid game data passed to LoadGameMessage");
        }


    }


    public GameData getGame() {
        return game;
    }}
