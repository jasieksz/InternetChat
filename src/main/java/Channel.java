import org.eclipse.jetty.websocket.api.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    private final String channelName;
    Map<Session, User> usersMap = new ConcurrentHashMap<>();

    public Channel(String name) {
        this.channelName = name;
    }

    private void addUser(Session session, User user){
        usersMap.put(session, user);
    }

    private void removeUser(Session session){
        usersMap.remove(session);
    }

    public String getName() {
        return channelName;
    }
}
