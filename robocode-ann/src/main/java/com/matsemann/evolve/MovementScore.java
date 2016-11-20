package com.matsemann.evolve;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MovementScore implements Serializable {

    public static final String FILE_NAME = "./data/score.txt";

    public double score;
    public double ticks;

    public static void save(MovementScore score) {

        try (
                FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
                PrintWriter printWriter = new PrintWriter(fileOutputStream);
        ) {
            printWriter.println(score.score);
            printWriter.println(score.ticks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Loads data from a file serialized by Java built-in serialization.
    public static MovementScore load() {

        try {
            List<String> strings = Files.readAllLines(Paths.get(FILE_NAME), StandardCharsets.UTF_8);
            MovementScore score = new MovementScore();
            score.score = Double.parseDouble(strings.get(0));
            score.ticks = Double.parseDouble(strings.get(1));
            return score;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        MovementScore s1 = new MovementScore();
        s1.score = Math.random();
        s1.ticks = 133333337.1337;
        save(s1);

        MovementScore s2 = load();

        if (s1.score != s2.score) {
            throw new RuntimeException("GAHH");
        }
    }
}
