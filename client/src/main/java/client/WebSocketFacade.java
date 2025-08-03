package client;

import chess.*;
import com.google.gson.Gson;
import websocket.messages.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketFacade {

    private Session session;
    private final Gson gson = new Gson();
    private MessageHandler handler;

    public interface MessageHandler {
        void handleMessage(ServerMessage message);
    }

    public WebSocketFacade(MessageHandler handler) {
        this.handler = handler;
    }

    private ConnectCommand pendingConnectCommand = null;
    public void connect(String url, String authToken, int gameID, String username, ChessGame.TeamColor playerColor) throws Exception {
        System.out.println("[WSF] Sending CONNECT with token: " + authToken);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(url));
        ConnectCommand command = new ConnectCommand(authToken, gameID, username, playerColor);
        this.session.getBasicRemote().sendText(gson.toJson(command));
    }
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("[WebSocket] Connected");

        // ✅ Safe to send CONNECT now
        if (pendingConnectCommand != null) {
            sendCommand(pendingConnectCommand);
            pendingConnectCommand = null;
        }
    }


    public void makeMove(String authToken, int gameID, ChessMove move) {
        sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    public void resign(String authToken, int gameID) {
        sendCommand(new ResignCommand(authToken, gameID));
    }

    public void leave(String authToken, int gameID) {
        sendCommand(new LeaveCommand(authToken, gameID));
    }

    private void sendCommand(UserGameCommand command) {
        if (session != null && session.isOpen()) {
            String json = gson.toJson(command);
            session.getAsyncRemote().sendText(json);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        if (message == null || message.isBlank()) return;
        ServerMessage base = gson.fromJson(message, ServerMessage.class);
        switch (base.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                handler.handleMessage(load);
            }
            case NOTIFICATION -> {
                NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                handler.handleMessage(note);
            }
            case ERROR -> {
                ErrorMessage err = gson.fromJson(message, ErrorMessage.class);
                handler.handleMessage(err);
            }
        }
    }

    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            System.out.println("[WebSocket] Error closing session: " + e.getMessage());
        }
    }



    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("[WebSocket] Closed: " + reason);
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("[WebSocket] Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

}
