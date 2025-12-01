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

import java.io.*;
import java.net.URI;
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

        registerGet("/sample", this::freemarkerSampleHandler);
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
        Path path = makeFilePath("profile.html");
        String getCookie = getCookie(exchange);
        try {
            var cookieMap = Cookie.parse(getCookie);
            var user = dataModel.getUserById(Integer.parseInt(cookieMap.get("userId")));
            user.setAuthorized(true);

            Map<String, Object> map = new HashMap<>();
            map.put("user", user);
            map.put("success", true);
            renderTemplate(exchange, "profile.html", map);
        } catch (NumberFormatException e) {
            renderTemplate(exchange, "profile.html", path);
        }
    }

    private void logoutHandler(HttpExchange exchange) {
        try {
            var cookieMap = Cookie.parse(getCookie(exchange));
            var user = dataModel.getUserById(Integer.parseInt(cookieMap.get("userId")));
            user.setAuthorized(false);
        } catch (NumberFormatException e) {
            redirect303(exchange, "/login");
            return;
        }

        Cookie logout = Cookie.make("userId", "", 0, true);
        setCookie(exchange, logout);
        redirect303(exchange, "/login");
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

        var cookieMap = Cookie.parse(getCookie(exchange));
        var user = dataModel.getUserById(Integer.parseInt(cookieMap.get("userId")));

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
            renderTemplate(exchange, "books.html", map);
            return true;
        }
        return false;
    }

    private void booksUserHandler(HttpExchange exchange) {
        try {
            var cookieMap = Cookie.parse(getCookie(exchange));
            var user = dataModel.getUserById(Integer.parseInt(cookieMap.get("userId")));
            var map = prepareMapForRender();
            map.put("user", user);
            renderTemplate(exchange, "books.html", map);
        } catch (Exception e) {
            var map = prepareMapForRender();
            map.put("user", null);
            renderTemplate(exchange, "books.html", map);
        }
    }

    private void bookInfoHandler(HttpExchange exchange) {
        try {
            int id = getIdFromUri(exchange);

            if (dataModel.getBookById(id) == null) {
                respond404(exchange);
            } else {
                dataModel.setBook(id);
                renderTemplate(exchange, "info.ftl", dataModel);
            }
        } catch (NumberFormatException e) {
            respond404(exchange);
        }
    }

    private void usersHandler(HttpExchange exchange) {
        renderTemplate(exchange, "users.ftl", dataModel);
    }

    private void employeeHandler(HttpExchange exchange) {
        try {
            int id = getIdFromUri(exchange);

            if (dataModel.getUserById(id) == null) {
                respond404(exchange);
            } else {
                dataModel.setUser(id);
                renderTemplate(exchange, "employee.ftl", dataModel);
            }
        } catch (NumberFormatException e) {
            respond404(exchange);
        }
    }

    private Map<String, String> parsePostBody(HttpExchange exchange) {
        String raw = getRequestBody(exchange);
        return Utils.parseUrlEncoded(raw, "&");
    }

    private int getIdFromUri(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String s = uri.getQuery();

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

    private void freemarkerSampleHandler(HttpExchange exchange) {
        renderTemplate(exchange, "sample.html", dataModel);
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
