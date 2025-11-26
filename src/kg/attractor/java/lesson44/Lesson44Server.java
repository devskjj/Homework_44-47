package kg.attractor.java.lesson44;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.ResponseCodes;

import java.io.*;
import java.net.URI;

public class Lesson44Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Lesson44Server(String host, int port, SampleDataModel dataModel) throws IOException {
        super(host, port, dataModel);
        registerGet("/sample", this::freemarkerSampleHandler);
        registerGet("/books", this::booksHandler);
        registerGet("/books/info", this::bookInfoHandler);
        registerGet("/users", this::usersHandler);
        registerGet("/users/employee", this::employeeHandler);
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

    private void booksHandler(HttpExchange exchange) {
        renderTemplate(exchange, "books.ftl", getSampleDataModel());
    }

    private void bookInfoHandler(HttpExchange exchange) {
        int id = getIdFromUri(exchange);

        if (dataModel.getBookById(id) == null) {
            respond404(exchange);
        } else {
            dataModel.setBook(id);
            renderTemplate(exchange, "info.ftl", dataModel);
        }
    }

    private int getIdFromUri(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String s = uri.getQuery().replace("id=", "");
        return Integer.parseInt(s);
    }

    private void usersHandler(HttpExchange exchange) {
        renderTemplate(exchange, "users.ftl", getSampleDataModel());
    }

    private void employeeHandler(HttpExchange exchange) {
        int id = getIdFromUri(exchange);

        if (dataModel.getUserById(id) == null) {
            respond404(exchange);
        } else {
            dataModel.setUser(id);
            renderTemplate(exchange, "employee.ftl", dataModel);
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
