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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private static final Map<Session, String> SESSION_TO_USER = new ConcurrentHashMap<>();
    private static final Map<Integer, Map<Session, Boolean>> GAMESESSIONS = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    private final Map<Integer, Boolean> gameOverMap = new HashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connected: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        SESSION_TO_USER.remove(session);
        GAMESESSIONS.values().forEach(sessions -> sessions.remove(session));
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
            if (auth == null) {
                send(session, new ErrorMessage("Error: Unauthorized (auth null)"));
                return;
            }
            GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found"));
                return;
            }
            gameOverMap.put(game.gameID(), false);


            SESSION_TO_USER.put(session, auth.username());
            GAMESESSIONS.putIfAbsent(cmd.getGameID(), new ConcurrentHashMap<>());
            GAMESESSIONS.get(cmd.getGameID()).put(session, true);

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
            if (auth == null) {
                send(session, new ErrorMessage("Error: Unauthorized"));
                return;
            }
            GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found"));
                return;
            }
            ChessGame.TeamColor color = getPlayerColor(auth.username(), game);

            if (color == null) {
                throw new DataAccessException("You are not a player in this game");
            }
            if (gameOverMap.getOrDefault(game.gameID(), false))
                {throw new DataAccessException("Game is already over");}
            if (game.game().getTeamTurn() != color)
                {throw new DataAccessException("It's not your turn");}

            game.game().makeMove(cmd.getMove());
            DataAccessFactory.getGameDAO().updateGame(game);

            String note;
            ChessGame.TeamColor opponent = color == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            if (game.game().isInCheckmate(opponent)) {
                note = "Checkmate! " + auth.username() + " wins!";
                //DataAccessFactory.getGameDAO().setGameOver(cmd.getGameID(), true);
                gameOverMap.put(game.gameID(), true);
            } else if (game.game().isInStalemate(opponent)) {
                note = "Stalemate! It's a tie.";
                gameOverMap.put(game.gameID(), true);}
            else if (game.game().isInCheck(opponent)) {
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
        System.out.println("handleResign called");
        try {
            AuthData auth = DataAccessFactory.getAuthDAO().getAuth(cmd.getAuthToken());
            if (auth == null) {
                send(session, new ErrorMessage("Error: Unauthorized"));
                return;
            }
            System.out.println("Wait 0");
            SESSION_TO_USER.put(session, auth.username());
            System.out.println("Wait 1");


            GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
            if (game == null || game.game() == null) {
                send(session, new ErrorMessage("Game not found"));
                return;
            }
            System.out.println("Wait 2");


            ChessGame.TeamColor color = getPlayerColor(auth.username(), game);

            if (color == null) {
                throw new DataAccessException("Observers cannot resign");
            }
            if (gameOverMap.getOrDefault(game.gameID(), false))
            {throw new DataAccessException("Game is already over");}

            // If already resigned, don't repeat the endgame!!!!
            if (gameOverMap.getOrDefault(game.gameID(), false)) {
                send(session, new ErrorMessage("Error: Game already over"));
                return;
            }
            System.out.println("Wait 3");

            gameOverMap.put(game.gameID(), true);
            DataAccessFactory.getGameDAO().updateGame(game);

            // Broadcast resignation notice
            String note = auth.username() + " has resigned. " +
                    (auth.username().equals(game.whiteUsername()) ? game.blackUsername() : game.whiteUsername()) +
                    " wins!";
            broadcast(cmd.getGameID(), new Notification(note),session);

            System.out.println("Wait DONE");


        } catch (DataAccessException e) {
            send(session, new ErrorMessage(e.getMessage()));
        }

        catch (Exception e) {
            e.printStackTrace();
            send(session, new ErrorMessage("Unexpected error: " + e.getMessage()));
        }
    }

    private void handleLeave(Session session, LeaveCommand cmd) throws IOException, DataAccessException {
        AuthData auth = DataAccessFactory.getAuthDAO().getAuth(cmd.getAuthToken());
        if (auth == null) {
            send(session, new ErrorMessage("Error: Unauthorized"));
            return;
        }

        GameData game = DataAccessFactory.getGameDAO().getGame(cmd.getGameID());
        if (game == null) {
            send(session, new ErrorMessage("Error: Game not found"));
            return;
        }

        String username = auth.username();

        String updatedWhite = game.whiteUsername();
        String updatedBlack = game.blackUsername();

        //clear color assignment
        if (username.equals(game.whiteUsername())) {
            updatedWhite = null;
        } else if (username.equals(game.blackUsername())) {
            updatedBlack = null;}
        //rebuild the updated game data
        GameData updated = new GameData(
                game.gameID(),
                updatedWhite,
                updatedBlack,
                game.gameName(),
                game.game()
        );

        DataAccessFactory.getGameDAO().updateGame(updated);
        //remove session from session maps
        SESSION_TO_USER.remove(session);
        GAMESESSIONS.getOrDefault(cmd.getGameID(), new HashMap<>()).remove(session);

        broadcast(cmd.getGameID(), new Notification(username + " left the game."), session);
    }


    private ChessGame.TeamColor getPlayerColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {return ChessGame.TeamColor.WHITE;}
        if (username.equals(game.blackUsername())) {return ChessGame.TeamColor.BLACK;}
        return null;
    }

    private void broadcast(int gameID, ServerMessage msg, Session sender) throws IOException {
        for (Session s : GAMESESSIONS.getOrDefault(gameID, Map.of()).keySet()) {
            //skip sender if it's a NOTIFICATION
            // (but not for resign)
            String content = (msg instanceof Notification notif) ? notif.getMessage().toLowerCase() : "";
            if (content.contains("resign")) {} //I know this looks ugly but it works
            else if (msg.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION && s.equals(sender)) {
                continue;
            }
            send(s, msg);
        }
    }


    private void send(Session s, ServerMessage msg) throws IOException {
        if (s.isOpen()) {
            s.getRemote().sendString(gson.toJson(msg));
        }
    }}

