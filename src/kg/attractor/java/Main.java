package kg.attractor.java;

import kg.attractor.java.lesson44.Lesson44Server;
import kg.attractor.java.lesson44.Lesson45Server;
import kg.attractor.java.lesson44.SampleDataModel;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            SampleDataModel dataModel = new SampleDataModel();
            new Lesson45Server("192.168.0.8", 9889, dataModel).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
