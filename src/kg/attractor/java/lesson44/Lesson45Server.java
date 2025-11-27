package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.lesson44.entities.User;
import kg.attractor.java.lesson44.utility.JsonUtil;
import kg.attractor.java.lesson44.utility.Utils;

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
        registerGet("/profile", this::profileHandler);
        registerGet("/profile/user", this::profileUserHandler);
    }

    private void loginHandler(HttpExchange exchange) {
        Path path = makeFilePath("login.html");
        renderTemplate(exchange, "login.html", path);
    }

    private void loginPostHandler(HttpExchange exchange) {
        String raw = getRequestBody(exchange);
        var parsed = Utils.parseUrlEncoded(raw, "&");

        boolean isMatch = dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(parsed.get("user-password")))
                .anyMatch(u -> u.getEmail().trim().equalsIgnoreCase(parsed.get("user-email").trim()));

        if (isMatch) {
            Integer userId = getUserIdByEmail(parsed);
            redirect303(exchange, "/profile/user?id=" + userId);
        } else {
            setFlagForModel("error", true, exchange, "login.html");
        }
    }

    private void registerHandler(HttpExchange exchange) {
        Path path = makeFilePath("register.html");
        renderTemplate(exchange, "register.html", path);
    }

    private void registerPostHandler(HttpExchange exchange) {
        String raw = getRequestBody(exchange);
        var parsed = Utils.parseUrlEncoded(raw, "&");

        boolean isAlreadyExists = dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(parsed.get("user-email")));

        if (isAlreadyExists) {
            setFlagForModel("error", true, exchange, "register.html");
        } else {
            try {
                addNewUserToJson(parsed);
                JsonUtil.save("data.json", dataModel);
                setFlagForModel("success", true, exchange, "register.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFlagForModel(String msg, boolean bool, HttpExchange exchange, String model) {
        var templateModel = new HashMap<>();
        templateModel.put(msg, bool);
        renderTemplate(exchange, model, templateModel);
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

        var queryParams = Utils.parseUrlEncoded(query, "&");
        String userIdParam = queryParams.get("id");
        if (userIdParam == null) {
            respond404(exchange);
            return;
        }
        loadUserIdProfile(exchange, userIdParam);
    }

    private Integer getUserIdByEmail(Map<String, String> parsed) {
        return dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> u.getEmail().trim().equalsIgnoreCase(parsed.get("user-email").trim()))
                .map(User::getId)
                .findFirst()
                .orElse(-1);
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

    private void loadUserIdProfile(HttpExchange exchange, String userIdParam) {
        try {
            var userId = Integer.parseInt(userIdParam);
            var user = dataModel.getUserById(userId);

            if (user != null) {
                var templateModel = new HashMap<>();
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
