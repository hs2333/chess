package websocket;

import websocket.commands.*;
import websocket.messages.*;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ClientEndpoint
public class WebSocketFacade {
    private final Session session;
    private final BlockingQueue<ServerMessage> incoming = new LinkedBlockingQueue<>();

    public WebSocketFacade(String authToken, int gameID) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = new URI("ws://localhost:8080/ws"); // Change port if needed
        container.connectToServer(this, uri);

        // Send CONNECT command
        ConnectCommand command = new ConnectCommand(authToken, gameID, null, null);
        send(command);
    }

    public void send(UserGameCommand command) throws Exception {
        session.getBasicRemote().sendText(Serializer.toJson(command));
    }

    public ServerMessage receive() throws InterruptedException {
        return incoming.take();
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String messageJson) throws Exception {
        ServerMessage message = Serializer.fromJson(messageJson, ServerMessage.class);
        incoming.put(message);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("[✗] Disconnected: " + closeReason);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("[✗] WebSocket error: " + throwable.getMessage());
    }
}