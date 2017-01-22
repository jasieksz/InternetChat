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
    public Map<Session, Channel> userChannelMap = new ConcurrentHashMap<>();
    public Map<String, Channel> nameChannelMap = new ConcurrentHashMap<>();

    public String getUsernameFromCookie(Session user) {
        for (HttpCookie cookie : user.getUpgradeRequest().getCookies()){
            if (cookie.getName().equals("username"))
                return cookie.getValue();
        }
        return null;
    }

    public String getUsernameFromMap(Session session){
        if(userNameMap.containsKey(session))
            return userNameMap.get(session);
        return null;
    }

    public void addUser(Session session){
        if (!userNameMap.containsKey(session))
            userNameMap.put(session,getUsernameFromCookie(session));
    }

    public void removeUser(Session session){
        if (userNameMap.containsKey(session))
            userNameMap.remove(session);
    }

    public void broadcastMessageInMenu() {
        userNameMap.keySet().stream().filter(Session::isOpen).forEach(this::broadcastSettings);
    }

    ///
    // CHANNEL ===============================================================================
    ///

    public void userMessage(Session user, String content) {
        Channel tmpChannel = userChannelMap.get(user);
        tmpChannel.broadcastMessageOnChannel(tmpChannel.userNameMap.get(user),content);
    }

    public void createChannel(Session user, String channelName) {
        if (!nameChannelMap.containsKey(channelName))
            nameChannelMap.put(channelName, new Channel(channelName, true));
        broadcastMessageInMenu();
    }

    public void joinChannel(Session user, String channelname) {
        userChannelMap.put(user,nameChannelMap.get(channelname));
        nameChannelMap.get(channelname).addUser(user,getUsernameFromMap(user));
        nameChannelMap.get(channelname).broadcastMessageOnChannel("Server",getUsernameFromMap(user) + " joined the channel");
        userNameMap.remove(user);
    }

    public void changeChannel(Session user) {
        Channel tmpChannel = userChannelMap.get(user);
        addUser(user);
        tmpChannel.removeUser(user);
        tmpChannel.broadcastMessageOnChannel("Server",getUsernameFromMap(user)+ " left the channel");
        userChannelMap.remove(user);
        broadcastMessageInMenu();
    }

    public void removeEmptyChannels(){

    }

    public void broadcastMessage(String sender, String message) {
        userNameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("channel", "true")
                        .put("userMessage", createHtmlMessageFromSender(sender, message))
                        .put("userlist", userNameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void broadcastSettings(Session user) {
        try {
            user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("channel", "false")
                    .put("channellist", nameChannelMap.keySet())
                    .put("userlist", userNameMap.values())
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public  String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();

    }


}
