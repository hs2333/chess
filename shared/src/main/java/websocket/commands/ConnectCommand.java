package websocket.commands;
import chess.ChessGame.TeamColor;

public class ConnectCommand extends UserGameCommand {
    private final String username;
    private final TeamColor playerColor;

    public ConnectCommand(String authToken, Integer gameID, String username, TeamColor playerColor) {
        super(CommandType.CONNECT, authToken, gameID);
        this.username = username;
        this.playerColor = playerColor;
    }


    public String getUsername() {
        return username;
    }
    public TeamColor getPlayerColor() {
        return playerColor;
    }}

