package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.lesson44.entities.User;
import kg.attractor.java.lesson44.utility.JsonUtil;
import kg.attractor.java.lesson44.utility.Utils;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Lesson45Server extends Lesson44Server {
    public Lesson45Server(String host, int port, SampleDataModel dataModel) throws IOException {
        super(host, port, dataModel);

        registerGet("/login", this::loginHandler);
        registerPost("/login", this::loginPostHandler);
        registerGet("/register", this::registerHandler);
        registerPost("/register", this::registerPostHandler);

    }


    private void loginPostHandler(HttpExchange exchange) {
        String cType = getContentType(exchange);
        String raw = getRequestBody(exchange);

        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        String fmt = "<p>Необработанные данные: <b>%s</b></p>"
                + "<p>Content-type: <b>%s</b></p>"
                + "<p>После обработки: <b>%s</b></p>";

        String data = String.format(fmt, raw, cType, parsed);

        try {
            sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerPostHandler(HttpExchange exchange) {
        String cType = getContentType(exchange);
        String raw = getRequestBody(exchange);


        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        String fmt = "<p>Необработанные данные: <b>%s</b></p>"
                + "<p>Content-type: <b>%s</b></p>"
                + "<p>После обработки: <b>%s</b></p>";

        String data = String.format(fmt, raw, cType, parsed);

        boolean isAlreadyExists = dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(parsed.get("user-email")));

        if (isAlreadyExists) {
            redirect303(exchange, "/register?error=1");
        } else {
            try {
                addNewUserToJson(parsed);
                JsonUtil.save("data.json", dataModel);
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    protected void redirect303(HttpExchange exchange, String path) {
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, 0);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewUserToJson(Map<String, String> parsed) {
        int newId = dataModel.getUsers().size() + 1;
        dataModel.getUsers().add(new User(
                parsed.get("user-name"),
                parsed.get("user-email"),
                parsed.get("user-password"),
                newId));
    }

    private void loginHandler(HttpExchange exchange) {
        Path path = makeFilePath("login.html");
        sendFile(exchange, path, ContentType.TEXT_HTML);
    }

    private void registerHandler(HttpExchange exchange) {
        Path path = makeFilePath("register.html");

        Map<String, Object> templateModel = getErrorStatusFromQuery(exchange);
        renderTemplate(exchange, "register.html", templateModel);
    }

    private Map<String, Object> getErrorStatusFromQuery(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        boolean error = false;

        if (query != null) {
            Map<String, String> queryMap = Utils.parseUrlEncoded(query, "&");
            error = "1".equals(queryMap.get("error"));
        }

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("error", error);
        return templateModel;
    }


}
