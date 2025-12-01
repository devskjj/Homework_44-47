package kg.attractor.java.booklender.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kg.attractor.java.booklender.models.JsonDataClass;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonUtil {
    private static Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    public static JsonDataClass load(String file) throws IOException {
        Path path = Path.of("src/kg/attractor/java/booklender/json", file);

        try (FileReader reader = new FileReader(path.toFile())) {
            return getGson().fromJson(reader, JsonDataClass.class);
        } catch (IOException e) {
            throw new IOException("Ошибка при загрузке файла " + file, e);
        }
    }

    public static void save(String file, Object data) throws IOException {
        Path path = Path.of("src/kg/attractor/java/booklender/json", file);

        try (FileWriter write = new FileWriter(path.toFile())) {
            getGson().toJson(data, write);
        }
    }
}
