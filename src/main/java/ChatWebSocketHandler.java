import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class ChatWebSocketHandler {

    private ChatFunctions chat = new ChatFunctions(true);
    private String sender,msg;

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        String username = chat.getUsernameFromCookie(session);
        chat.addUser(session);
        chat.broadcastSettings(session);

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        String username = chat.getUsernameFromMap(session);
        chat.removeUser(session);
        chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        String reason = message.substring(message.indexOf("|")+1,message.length());
        String content = message.substring(0,message.indexOf("|"));
        if (reason.equals("message"))
            chat.userMessage(user,content);
        else if (reason.equals("change")) // change channel - wyjdz  zkanalu
            chat.changeChannel(user);
        else if (reason.equals("newchn")) //dodaj nowy kanal
            chat.createChannel(content);
        else if (reason.equals("join")) //dolacz do kanalu
            chat.joinChannel(user,content);
    }
}
