import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {

    private ChatFunctions chat = new ChatFunctions();
    private String sender,msg;

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        String username = chat.getUsernameFromCookie(session);
        chat.addUser(session);
        chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));

    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {

    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        chat.broadcastMessage(sender = chat.userNameMap.get(user), msg = message);
    }
}
