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

    public Lesson44Server(String host, int port) throws IOException {
        super(host, port);
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
        renderTemplate(exchange, "books.html", getSampleDataModel());
    }

    private void bookInfoHandler(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String s = uri.getQuery().replace("id=","");
        int number = Integer.parseInt(s);
        renderTemplate(exchange, "info.html", getSampleDataModel(number));
    }

    private void usersHandler(HttpExchange exchange) {
        renderTemplate(exchange, "users.html", getSampleDataModel());
    }

    private void employeeHandler(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String s = uri.getQuery().replace("id=","");
        int number = Integer.parseInt(s);
        renderTemplate(exchange, "employee.html", getSampleDataModel(number));
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
        return new SampleDataModel();
    }

    private SampleDataModel getSampleDataModel(int number) {
        return new SampleDataModel(number);
    }
}
