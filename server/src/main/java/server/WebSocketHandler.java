package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private static final Map<Session, String> sessionToUser = new ConcurrentHashMap<>();
    private static final Map<Integer, Map<Session, Boolean>> gameSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessionToUser.remove(session);
        gameSessions.values().forEach(sessions -> sessions.remove(session));
        System.out.println("WebSocket closed: " + session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String messageJson) throws Exception {
        UserGameCommand command = gson.fromJson(messageJson, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, gson.fromJson(messageJson, ConnectCommand.class));
            case MAKE_MOVE -> handleMakeMove(session, gson.fromJson(messageJson, MakeMoveCommand.class));
            case RESIGN -> handleResign(session, gson.fromJson(messageJson, ResignCommand.class));
            case LEAVE -> handleLeave(session, gson.fromJson(messageJson, LeaveCommand.class));
        }
    }

    private void handleConnect(Session session, ConnectCommand cmd) throws IOException {
        try {
            AuthData auth = DataAccessFactory.getAuthDAO().getAuth(cmd.getAuthToken());
            GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found"));
                return;
            }
            sessionToUser.put(session, auth.username());
            gameSessions.putIfAbsent(cmd.getGameID(), new ConcurrentHashMap<>());
            gameSessions.get(cmd.getGameID()).put(session, true);

            String role = cmd.getPlayerColor() == null ? "an observer" : "a player";
            broadcast(cmd.getGameID(), new Notification(auth.username() + " joined as " + role),session);
            send(session, new LoadGameMessage(game));
        } catch (DataAccessException e) {
            send(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand cmd) throws IOException {
        try {
            AuthData auth = DataAccessFactory.getAuthDAO().getAuth(cmd.getAuthToken());
            GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found"));
                return;
            }
            ChessGame.TeamColor color = getPlayerColor(auth.username(), game);

            if (color == null) throw new DataAccessException("You are not a player in this game");
            if (DataAccessFactory.getGameDAO().getGameOver(cmd.getGameID()))
                throw new DataAccessException("Game is already over");
            if (game.game().getTeamTurn() != color)
                throw new DataAccessException("It's not your turn");

            game.game().makeMove(cmd.getMove());
            DataAccessFactory.getGameDAO().updateGame(game);

            String note;
            ChessGame.TeamColor opponent = color == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            if (game.game().isInCheckmate(opponent)) {
                note = "Checkmate! " + auth.username() + " wins!";
                DataAccessFactory.getGameDAO().setGameOver(cmd.getGameID(), true);
            } else if (game.game().isInStalemate(opponent)) {
                note = "Stalemate! It's a tie.";
                DataAccessFactory.getGameDAO().setGameOver(cmd.getGameID(), true);
            } else if (game.game().isInCheck(opponent)) {
                note = auth.username() + " moved. " + opponent + " is in check!";
            } else {
                note = auth.username() + " moved.";
            }
            broadcast(cmd.getGameID(), new Notification(note),session);
            broadcast(cmd.getGameID(), new LoadGameMessage(game),session);
        } catch (InvalidMoveException | DataAccessException e) {
            send(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void handleResign(Session session, ResignCommand cmd) throws IOException {
        try {
            AuthData auth = DataAccessFactory.getAuthDAO().getAuth(cmd.getAuthToken());
            GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found"));
                return;
            }
            ChessGame.TeamColor color = getPlayerColor(auth.username(), game);

            if (color == null) throw new DataAccessException("Observers cannot resign");
            if (DataAccessFactory.getGameDAO().getGameOver(cmd.getGameID()))
                throw new DataAccessException("Game is already over");

            DataAccessFactory.getGameDAO().setGameOver(cmd.getGameID(), true);
            String opponent = color == ChessGame.TeamColor.WHITE ? game.blackUsername() : game.whiteUsername();
            broadcast(cmd.getGameID(), new Notification(auth.username() + " resigned. " + opponent + " wins!"),session);
        } catch (DataAccessException e) {
            send(session, new ErrorMessage(e.getMessage()));
        }
    }

    private void handleLeave(Session session, LeaveCommand cmd) throws IOException {
        try {
            AuthData auth = DataAccessFactory.getAuthDAO().getAuth(cmd.getAuthToken());
            broadcast(cmd.getGameID(), new Notification(auth.username() + " has left the game."),session);
            gameSessions.getOrDefault(cmd.getGameID(), Map.of()).remove(session);
            sessionToUser.remove(session);
            session.close();
        } catch (DataAccessException e) {
            send(session, new ErrorMessage(e.getMessage()));
        }
    }

    private ChessGame.TeamColor getPlayerColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) return ChessGame.TeamColor.WHITE;
        if (username.equals(game.blackUsername())) return ChessGame.TeamColor.BLACK;
        return null;
    }

    private void broadcast(int gameID, ServerMessage msg, Session sender) throws IOException {
        for (Session s : gameSessions.getOrDefault(gameID, Map.of()).keySet()) {
            // Skip sender if it's a NOTIFICATION
            if (msg.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION && s.equals(sender)) {
                continue;
            }
            send(s, msg);
        }
    }


    private void send(Session s, ServerMessage msg) throws IOException {
        if (s.isOpen()) {
            s.getRemote().sendString(gson.toJson(msg));
        }
    }
}
