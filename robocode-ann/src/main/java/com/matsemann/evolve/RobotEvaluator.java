package com.matsemann.evolve;

import com.matsemann.BattleListener;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

import java.io.*;
import java.util.List;

public class RobotEvaluator implements CalculateScore {

    private final RobocodeEngine engine;

    private static int count = 0;

    public RobotEvaluator() {
        engine = new RobocodeEngine(new File("/Users/matssvensson/robo/robocode-core"));

        engine.addBattleListener(new BattleListener());
        engine.setVisible(false);
    }

    @Override
    public double calculateScore(MLMethod method) {
        NEATNetwork network = (NEATNetwork) method;

        if (count == 400) {
            engine.setVisible(true);
        }

        new MovementAnn(network).save();

        RobotSpecification[] robots = engine.getLocalRepository("com.matsemann.bot.AnnMovementBot*, sample.Walls");

        BattlefieldSpecification battleField = new BattlefieldSpecification();
        BattleSpecification battle = new BattleSpecification(1, battleField, robots);

        engine.runBattle(battle);
        engine.waitTillBattleOver();

        double score = MovementScore.load().score;
        System.out.println("got score: " + score);
        System.out.println("for round" + count++);
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
