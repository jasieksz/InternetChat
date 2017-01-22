import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    public final Boolean canDelete;
    public String channelName;
    public Map<Session, String> userNameMap = new ConcurrentHashMap<>();
    private ChatFunctions chat = new ChatFunctions();

    public Channel(String channelName,Boolean canDelete) {
        this.canDelete = canDelete;
        this.channelName = channelName;
    }

    public void addUser(Session user, String usernameFromMap) {
        if (!userNameMap.containsKey(user))
            userNameMap.put(user,usernameFromMap);
    }

    public void removeUser (Session user) {
        if (userNameMap.containsKey(user)){
            broadcastMessageOnChannel("Server", userNameMap.get(user) + " left the channel");
            userNameMap.remove(user);
        }

    }

    public void broadcastMessageOnChannel(String sender, String message) {
        userNameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("channel", "true")
                        .put("userMessage", chat.createHtmlMessageFromSender(sender, message))
                        .put("userlist", userNameMap.values())
                ));
            } catch (JSONException | IOException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
