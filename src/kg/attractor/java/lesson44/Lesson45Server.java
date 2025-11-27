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
import java.util.Optional;
import java.util.stream.Collector;

public class Lesson45Server extends Lesson44Server {
    public Lesson45Server(String host, int port, SampleDataModel dataModel) throws IOException {
        super(host, port, dataModel);

        registerGet("/login", this::loginHandler);
        registerPost("/login", this::loginPostHandler);
        registerGet("/register", this::registerHandler);
        registerPost("/register", this::registerPostHandler);
        registerGet("/profile", this::profileHandler);
        registerGet("/profile/user", this::profileUserHandler);
    }




    private void loginPostHandler(HttpExchange exchange) {
        String cType = getContentType(exchange);
        String raw = getRequestBody(exchange);

        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        String fmt = "<p>Необработанные данные: <b>%s</b></p>"
                + "<p>Content-type: <b>%s</b></p>"
                + "<p>После обработки: <b>%s</b></p>";

        String data = String.format(fmt, raw, cType, parsed);

        boolean isMatch = dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(parsed.get("user-password")))
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(parsed.get("user-email")));

        if (isMatch) {
            var userId = dataModel.getUsers().stream()
                    .filter(u -> u.getEmail() != null)
                    .filter(u -> u.getEmail().equalsIgnoreCase(parsed.get("user-email")))
                    .map(User::getId)
                    .findFirst()
                    .orElse(-1);

            System.out.println(userId);
            redirect303(exchange, "/profile/user?id=" + userId);
//            renderTemplate(exchange, "profile.html", templateModel);
//            redirect303(exchange, redirectUrl);
        } else {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("error", true);
            renderTemplate(exchange, "login.html", templateModel);

        }



    }

    private void registerPostHandler(HttpExchange exchange) {
        String cType = getContentType(exchange);
        String raw = getRequestBody(exchange);

        Map<String, String> parsed = Utils.parseUrlEncoded(raw, "&");

        boolean isAlreadyExists = dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(parsed.get("user-email")));

        if (isAlreadyExists) {
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("error", true);
            renderTemplate(exchange, "register.html", templateModel);
        } else {
            try {
                addNewUserToJson(parsed);
                JsonUtil.save("data.json", dataModel);

                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("success", true);
                renderTemplate(exchange, "register.html", templateModel);
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
        var newId = dataModel.getUsers().size() + 1;
        dataModel.getUsers().add(new User(
                parsed.get("user-name"),
                parsed.get("user-email"),
                parsed.get("user-password"),
                newId));
    }

    private void loginHandler(HttpExchange exchange) {
        Path path = makeFilePath("login.html");
        renderTemplate(exchange, "login.html", path);
    }

    private void registerHandler(HttpExchange exchange) {
        Path path = makeFilePath("register.html");
        renderTemplate(exchange, "register.html", path);
    }

    private void profileHandler(HttpExchange exchange) {
        Path path = makeFilePath("profile.html");
        renderTemplate(exchange, "profile.html", path);
    }

    private void profileUserHandler(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isEmpty()) {
            respond404(exchange);
            return;
        }

        Map<String, String> queryParams = Utils.parseUrlEncoded(query, "&");
        String userIdParam = queryParams.get("id");
        if (userIdParam == null) {
            respond404(exchange);
            return;
        }

        try {
            var userId = Integer.parseInt(userIdParam);
            var user = dataModel.getUserById(userId);

            if (user != null) {
                Map<String, Object> templateModel = new HashMap<>();
                templateModel.put("user", user);
                renderTemplate(exchange, "profile.html", templateModel);
            } else {
                respond404(exchange);
            }
        } catch (NumberFormatException e) {
            respond404(exchange);
        }
    }
}
