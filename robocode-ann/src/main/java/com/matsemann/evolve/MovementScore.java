package com.matsemann.evolve;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
}
