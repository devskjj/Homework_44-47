package kg.attractor.java.lesson44.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kg.attractor.java.lesson44.models.JournalDataModel;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonUtil {
    private static Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    public static JournalDataModel load(String file) throws IOException {
        Path path = Path.of("src/kg/attractor/java/lesson44/json", file);

        try (FileReader reader = new FileReader(path.toFile())) {
            return getGson().fromJson(reader, JournalDataModel.class);
        } catch (IOException e) {
            throw new IOException("Ошибка при загрузке файла " + file, e);
        }
    }

    public static void save(String file, JournalDataModel data) throws IOException {
        Path path = Path.of("src/kg/attractor/java/lesson44/json", file);

        try (FileWriter write = new FileWriter(path.toFile())) {
            getGson().toJson(data, write);
        }
    }
}
