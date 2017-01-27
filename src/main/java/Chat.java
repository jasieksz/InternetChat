import static spark.Spark.*;

public class Chat {

    public static void main (String[] args) {

        port(4567);
        staticFileLocation("/public");
        staticFiles.expireTime(600);
        webSocket("/chat", ChatWebSocketHandler.class);

        before("/chat.html", (request, response) ->
        {
            if (request.cookie("username") == null)
                response.redirect("/");
        });

        get("/chat", (request, response) -> {
            if (request.cookie("username") == null) {
                response.redirect("/");
            } else {
                response.redirect("/chat.html");
            }
            return null;
        });

    }
}


/* some error
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.

 */