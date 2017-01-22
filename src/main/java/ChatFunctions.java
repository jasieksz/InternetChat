import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

public class ChatFunctions {

    public Map<Session, String> userNameMap = new ConcurrentHashMap<>();

    public String getUsernameFromCookie(Session user) {
        for (HttpCookie cookie : user.getUpgradeRequest().getCookies()){
            if (cookie.getName().equals("username"))
                return cookie.getValue();
        }
        return null;
    }

    public void addUser(Session session){
        if (!userNameMap.containsKey(session))
            userNameMap.put(session,getUsernameFromCookie(session));
    }


    public void broadcastMessage(String sender, String message) {
        userNameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(sender, message))
                        .put("userlist", userNameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public  String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();

    }


}
