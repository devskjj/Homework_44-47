package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.lesson44.utility.JsonUtil;
import kg.attractor.java.lesson44.utility.Utils;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.Cookie;
import kg.attractor.java.server.ResponseCodes;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Lesson44Server(String host, int port, SampleDataModel dataModel) throws IOException {
        super(host, port, dataModel);
        registerGet("/sample", this::freemarkerSampleHandler);
        registerGet("/books", this::booksUserHandler);
        registerPost("/books", this::booksTakePostHandler);

        registerGet("/books/info", this::bookInfoHandler);
        registerGet("/users", this::usersHandler);
        registerGet("/users/employee", this::employeeHandler);
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

    private Map<String, String> parsePostBody(HttpExchange exchange) {
        String raw = getRequestBody(exchange);
        return Utils.parseUrlEncoded(raw, "&");
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
        renderTemplate(exchange, "sample.html", getSampleDataModel());
    }


    private void booksUserHandler(HttpExchange exchange) {
        try {
            var cookieMap = Cookie.parse(getCookie(exchange));
            var user = dataModel.getUserById(Integer.parseInt(cookieMap.get("userId")));

            var map = new HashMap<String, Object>();
            map.put("books", dataModel.getBooks());
            map.put("records", dataModel.getRecords());
            map.put("users", dataModel.getUsers());
            map.put("user", user);

            renderTemplate(exchange, "books.html", map);
        } catch (Exception e) {
            var map = new HashMap<String, Object>();
            map.put("books", dataModel.getBooks());
            map.put("records", dataModel.getRecords());
            map.put("users", dataModel.getUsers());
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

    private void usersHandler(HttpExchange exchange) {
        renderTemplate(exchange, "users.ftl", getSampleDataModel());
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

    private SampleDataModel getSampleDataModel() {
        return dataModel;
    }
}
