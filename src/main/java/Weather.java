import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.UnknownHostException;
public class Weather {
    private final static String url = "http://api.openweathermap.org/data/2.5/weather?q=Krakow&appid=17619c7c44e4c6d14f4a20fccec56bee&units=metric&lang=pl";

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException, UnknownHostException {
        try (InputStream is = new URL(url).openStream()) {
            return new JSONObject(IOUtils.toString(is, "UTF-8"));
        }
    }

    public static String getWeather() {
        try {
            JSONObject weather = readJsonFromUrl(url);
            String description = weather.getJSONArray("weather").getJSONObject(0).getString("description");
            String temperature = weather.getJSONObject("main").getString("temp");
            return "Pogoda w Krakowie : " + description + ", jest " + temperature + " stopni.";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}



//80d53d13f0aa13c88fab95a589c79e15
//id krakowa 3085041