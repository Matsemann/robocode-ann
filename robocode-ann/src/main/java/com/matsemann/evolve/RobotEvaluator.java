package com.matsemann.evolve;

import com.matsemann.BattleListener;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;

import java.io.File;
import java.util.List;

public class RobotEvaluator implements CalculateScore {

    private final String bot;
    private final List<String> opponents;
    private RobocodeEngine engine;

    private long ticks;
    private long startTime;

    private BattleListener listener;

    public RobotEvaluator(String bot, List<String> opponents) {
        this.bot = bot;
        this.opponents = opponents;
    }

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
        calculateScore(method);
    }

    public void preIteration() {
        startTime = System.currentTimeMillis();
        createEngine();
        ticks = 0;
    }
    
    public void postIteration() {
        long timeUsed = System.currentTimeMillis() - startTime;
        System.out.println("Ticks: " + ticks + ", time per: " + ( (double) timeUsed / (double) ticks));
    }

    @Override
    public synchronized double calculateScore(MLMethod method) {
        MLRegression network = (MLRegression) method;

        new MovementAnn(network).save();

        double totalScore = 0;
        for (String opponent : opponents) {
            MovementScore score = runBattle(bot, opponent);
            totalScore += score.score;
            ticks += score.ticks;
        }

        return totalScore;
    }

    private MovementScore runBattle(String bot, String opponent) {
        RobotSpecification[] robots = engine.getLocalRepository(bot + ", " + opponent);

        BattlefieldSpecification battleField = new BattlefieldSpecification();
        BattleSpecification battle = new BattleSpecification(1, battleField, robots);

        engine.runBattle(battle);
        engine.waitTillBattleOver();
        return MovementScore.load();
    }

    @Override
    public boolean shouldMinimize() {
        return true;
    }

    @Override
    public boolean requireSingleThreaded() {
        return true;
    }


}
