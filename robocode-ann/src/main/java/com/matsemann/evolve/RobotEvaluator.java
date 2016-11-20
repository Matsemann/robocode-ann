package com.matsemann.evolve;

import com.matsemann.BattleListener;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RobotEvaluator implements CalculateScore {

    private RobocodeEngine engine;
    private List<Long> timeUsed;
    private long ticks;

    private static int count = 0;
    private BattleListener listener;


    private void createEngine() {
        if (engine != null) {
            engine.removeBattleListener(listener);
            engine.close();
        }

        engine = new RobocodeEngine(new File("/Users/matssvensson/robo/robocode-core"));

        listener = new BattleListener();
        engine.addBattleListener(listener);
        engine.setVisible(false);
    }

    public void showCase(MLMethod method) {
        engine.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        calculateScore(method);
    }

    public void preIteration() {
        createEngine();
        timeUsed = new ArrayList<>();
        ticks = 0;
    }
    
    public void postIteration() {
        long min = Long.MAX_VALUE, max = 0, sum = 0;
        for (Long time : timeUsed) {
            sum += time;
            if (time < min) {
                min = time;
            }
            if (time > max) {
                max = time;
            }
            System.out.print(time + ", ");
        }
        double avg = sum / (double) timeUsed.size();
        System.out.println();
        System.out.println("Time: " + sum + ", avg: " + avg + ", min: " + min + ", max: " + max);
        System.out.println("Ticks: " + ticks + ", time per: " + (sum / (double) ticks));
    }

    @Override
    public synchronized double calculateScore(MLMethod method) {
        long start = System.currentTimeMillis();
        
        MLRegression network = (MLRegression) method;

        count++;
        if (count == 0) {
//            engine.setVisible(true);
        }

        new MovementAnn(network).save();

        RobotSpecification[] robots = engine.getLocalRepository("com.matsemann.bot.AnnMovementBot*, sample.Walls");

        BattlefieldSpecification battleField = new BattlefieldSpecification();
        BattleSpecification battle = new BattleSpecification(2, battleField, robots);

        engine.runBattle(battle);
        engine.waitTillBattleOver();

        MovementScore results = MovementScore.load();
        double score = results.score;
        
        timeUsed.add(System.currentTimeMillis() - start);
        ticks += results.ticks;
        
//        System.out.println("for round" + count++);
//        System.out.println("got score: " + score);
        return score;
    }

    @Override
    public boolean shouldMinimize() {
        return true;
    }

    @Override
    public boolean requireSingleThreaded() {
        return true;
    }


    public static class MovementScore implements Serializable {
        public double score;
        public double ticks;

        public void save() {
            try {
                ObjectOutput out = new ObjectOutputStream(new FileOutputStream("./data/score.bin"));
                out.writeObject(this);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Loads data from a file serialized by Java built-in serialization.
        public static MovementScore load() {
            try {
                ObjectInput in = new ObjectInputStream(new FileInputStream("./data/score.bin"));
                return (MovementScore) in.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
