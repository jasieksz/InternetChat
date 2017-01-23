import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.net.HttpCookie;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

public class ChatFunctions {

    public Map<Session, String> userNameMap = new ConcurrentHashMap<>();
    public Map<Session, Channel> userChannelMap = new ConcurrentHashMap<>();
    public Map<String, Channel> nameChannelMap = new ConcurrentHashMap<>();
    private final Boolean menu;

    public ChatFunctions(Boolean menu) {
        this.menu = menu;
        nameChannelMap.put("Bot",new Channel("Bot",false));
        nameChannelMap.put("General",new Channel("General",false));
    }

    public ChatFunctions(){
        this.menu=null;
    }

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
        if (tmpChannel.getChannelName().equals("Bot")){
            tmpChannel.broadcastMessageOnChannel(tmpChannel.userNameMap.get(user),content);
            String answer = askBot(content);
            if (answer!=null)
                tmpChannel.broadcastMessageOnChannel("Server",answer);
        }
        else
            tmpChannel.broadcastMessageOnChannel(tmpChannel.userNameMap.get(user),content);
    }

    public void createChannel(String channelName) {
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
        userChannelMap.remove(user);
        removeEmptyChannel();
        broadcastMessageInMenu();
    }

    public void removeEmptyChannel(){
        Collection<Channel> activeChannels = userChannelMap.values();
        Collection<Channel> allChannels = nameChannelMap.values();
        ArrayList<Channel> emptyChannels;
        emptyChannels = allChannels
                .stream()
                .filter(channel -> !activeChannels.contains(channel) && channel.canDelete && channel.isEmpty())
                .collect(Collectors.toCollection(ArrayList::new));
        for (Channel channel : emptyChannels){
            String name = channel.getChannelName();
            nameChannelMap.remove(name);
        }
    }
///
// BOT ============================================================================================
///
    public String askBot(String message){
        if (message.equals("Która godzina?"))
            return "Jest godzina : " + LocalTime.now().toString();
        else if (message.equals("Jaki dziś dzień tygodnia"))
            return getDay();
        else if (message.equals("Jaka jest pogoda w Krakowie?"))
            return Weather.getWeather();
        else
            return null;

    }
//https://query.yahooapis.com/v1/public/yql?q=select%20item.condition.text%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22dallas%2C%20tx%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys
    private String getWeather(){

        return "";
    }

    private String getDay(){
        String[] dayOfWeekNames = {"poniedzialek","wtorek","sroda","czwartek","piątek","sobota","niedziela"};
        Calendar cal = Calendar.getInstance();
        System.out.println(cal.getTime());
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-Calendar.MONDAY;
        String day = dayOfWeekNames[dayOfWeek];
        return "Dzisiaj jest " + day;
    }
///
// BROADCAST ==================================
///
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
