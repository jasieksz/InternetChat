import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

public class Chat {

    public static void main (String[] args) {
        staticFiles.location("/public");
        staticFiles.expireTime(600);
        webSocket("/chat", ChatWebSocketHandler.class);
    }


}


/* some error
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

 */