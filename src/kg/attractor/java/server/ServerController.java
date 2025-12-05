package kg.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.booklender.models.DataModel;
import kg.attractor.java.booklender.entities.User;
import kg.attractor.java.booklender.utility.JsonUtil;
import kg.attractor.java.booklender.utility.Utils;
import kg.attractor.java.server.enums.ContentType;
import kg.attractor.java.server.enums.ResponseCodes;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ServerController extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public ServerController(String host, int port, DataModel dataModel) throws IOException {
        super(host, port, dataModel);
        registerGet("/login", this::loginHandler);
        registerPost("/login", this::loginPostHandler);
        registerGet("/register", this::registerHandler);
        registerPost("/register", this::registerPostHandler);
        registerGet("/profile", this::profileHandler);
        registerGet("/logout", this::logoutHandler);

        registerGet("/books", this::booksUserHandler);
        registerPost("/books", this::booksTakePostHandler);
        registerGet("/books/info", this::bookInfoHandler);

        registerGet("/users", this::usersHandler);
        registerGet("/users/employee", this::employeeHandler);
    }

    private void loginHandler(HttpExchange exchange) {
        Path path = makeFilePath("login.html");
        renderTemplate(exchange, "login.html", path);
    }

    private void loginPostHandler(HttpExchange exchange) {
        var parsed = parsePostBody(exchange);
        boolean isMatch = checkUserInputPassword(parsed);

        if (isMatch) {
            Integer userId = getUserIdByEmail(parsed);
            Cookie session = Cookie.make("userId", userId, 600, true);
            setCookie(exchange, session);
            redirect303(exchange, "/profile");
        } else {
            setFlagForModel("error", true, exchange, "login.html");
        }
    }

    private void registerHandler(HttpExchange exchange) {
        Path path = makeFilePath("register.html");
        renderTemplate(exchange, "register.html", path);
    }

    private void registerPostHandler(HttpExchange exchange) {
        var parsed = parsePostBody(exchange);
        boolean isAlreadyExists = checkEmailExists(parsed);

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

    private void profileHandler(HttpExchange exchange) {
        Path path = makeFilePath("profile.ftl");
        try {
            User user = getUserFromCookieForMap(exchange);
            user.setAuthorized(true);

            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("success", true);
            map.put("books", dataModel.getBooks());
            map.put("records", dataModel.getRecords());
            renderTemplate(exchange, "profile.ftl", map);
        } catch (NumberFormatException e) {
            renderTemplate(exchange, "profile.ftl", path);
        }
    }

    private void logoutHandler(HttpExchange exchange) {
        try {
            User user = getUserFromCookieForMap(exchange);
            user.setAuthorized(false);
        } catch (NumberFormatException e) {
            redirect303(exchange, "/login");
            return;
        }

        Cookie logout = Cookie.make("userId", "", 0, true);
        setCookie(exchange, logout);
        redirect303(exchange, "/login");
    }

    private User getUserFromCookieForMap(HttpExchange exchange) {
        var cookieMap = Cookie.parse(getCookie(exchange));
        return dataModel.getUserById(Integer.parseInt(cookieMap.get("userId")));
    }

    private void setFlagForModel(String msg, boolean bool, HttpExchange exchange, String model) {
        var templateModel = new HashMap<>();
        templateModel.put(msg, bool);
        renderTemplate(exchange, model, templateModel);
    }

    private void booksTakePostHandler(HttpExchange exchange) {
        var parsed = parsePostBody(exchange);
        String action = parsed.get("action");
        int bookId = Integer.parseInt(parsed.get("bookId"));

        User user = getUserFromCookieForMap(exchange);

        if (user.isAuthorized()) {
            try {
                if ("take".equalsIgnoreCase(action)) {
                    if (limitToTwoBooks(exchange, user)) return;
                    dataModel.takeBook(user.getId(), bookId);
                } else if ("return".equalsIgnoreCase(action)) {
                    dataModel.returnBook(user.getId(), bookId);
                }

                JsonUtil.save("data.json", dataModel);
                redirect303(exchange, "/books");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean limitToTwoBooks(HttpExchange exchange, User user) {
        if (dataModel.getRecords().stream()
                .filter(u -> u.getUserId() == user.getId())
                .filter(u -> u.getReturnDate() == null)
                .count() >= 2) {
            var map = prepareMapForRender();
            map.put("error", true);
            map.put("user", user);
            renderTemplate(exchange, "books.ftl", map);
            return true;
        }
        return false;
    }

    private void booksUserHandler(HttpExchange exchange) {
        try {
            User user = getUserFromCookieForMap(exchange);
            var map = prepareMapForRender();
            map.put("user", user);
            renderTemplate(exchange, "books.ftl", map);
        } catch (Exception e) {
            var map = prepareMapForRender();
            map.put("user", null);
            renderTemplate(exchange, "books.ftl", map);
        }
    }

    private void bookInfoHandler(HttpExchange exchange) {
        var map = new HashMap<>();
        putUserFromCookieIntoMap(exchange, map);
        try {
            int id = getIdFromQuery(exchange);
            var book = dataModel.getBookById(id);
            if (book == null) {
                respond404(exchange);
                return;
            }
            map.put("book", book);
            map.put("books", dataModel.getBooks());
            renderTemplate(exchange, "info.ftl", map);
        } catch (Exception e) {
            respond404(exchange);
        }
    }

    private void putUserFromCookieIntoMap(HttpExchange exchange, HashMap<Object, Object> map) {
        try {
            User user = getUserFromCookieForMap(exchange);
            map.put("user", user);
        } catch (NumberFormatException e) {
            map.put("user", null);
        }
    }

    private void usersHandler(HttpExchange exchange) {
        var map = new HashMap<>();
        putUserFromCookieIntoMap(exchange, map);
        map.put("users", dataModel.getUsers());
        map.put("books", dataModel.getBooks());
        map.put("records", dataModel.getRecords());
        renderTemplate(exchange, "users.ftl", map);
    }

    private void employeeHandler(HttpExchange exchange) {
        var map = new HashMap<>();
        putUserFromCookieIntoMap(exchange, map);
        try {
            int id = getIdFromQuery(exchange);
            User emp = dataModel.getUserById(id);
            if (emp == null) {
                respond404(exchange);
                return;
            }
            map.put("records", dataModel.getRecords());
            map.put("books", dataModel.getBooks());
            map.put("emp", emp);
            renderTemplate(exchange, "employee.ftl", map);
        } catch (NumberFormatException e) {
            respond404(exchange);
        }
    }

    private Map<String, String> parsePostBody(HttpExchange exchange) {
        String raw = getRequestBody(exchange);
        return Utils.parseUrlEncoded(raw, "&");
    }

    private int getIdFromQuery(HttpExchange exchange) {
        String s = getQueryParams(exchange);

        if (s == null) {
            respond404(exchange);
        }

        var map = Utils.parseUrlEncoded(s, "&");
        String idParam = map.get("id");
        return Integer.parseInt(idParam);
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

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("data"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            Template temp = freemarker.getTemplate(templateFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                temp.process(dataModel, writer);
                writer.flush();

                var data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> prepareMapForRender() {
        var map = new HashMap<String, Object>();
        map.put("books", dataModel.getBooks());
        map.put("records", dataModel.getRecords());
        map.put("users", dataModel.getUsers());
        return map;
    }

    private void addNewUserToJson(Map<String, String> parsed) {
        var newId = dataModel.getUsers().size() + 1;
        dataModel.getUsers().add(new User(
                parsed.get("user-name"),
                parsed.get("user-email"),
                parsed.get("user-password"),
                newId));
    }

    private boolean checkUserInputPassword(Map<String, String> parsed) {
        return dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> u.getPassword() != null && u.getPassword().equals(parsed.get("user-password")))
                .anyMatch(u -> u.getEmail().trim().equalsIgnoreCase(parsed.get("user-email").trim()));
    }

    private boolean checkEmailExists(Map<String, String> parsed) {
        return dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(e -> e.getEmail().trim().equalsIgnoreCase(parsed.get("user-email").trim()));
    }

    private Integer getUserIdByEmail(Map<String, String> parsed) {
        return dataModel.getUsers().stream()
                .filter(u -> u.getEmail() != null)
                .filter(u -> u.getEmail().trim().equalsIgnoreCase(parsed.get("user-email").trim()))
                .map(User::getId)
                .findFirst()
                .orElse(-1);
    }
}
