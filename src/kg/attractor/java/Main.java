package kg.attractor.java;

import kg.attractor.java.booklender.models.DataModel;
import kg.attractor.java.server.ServerController;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            DataModel dataModel = new DataModel();
            new ServerController("localhost", 9889, dataModel).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
