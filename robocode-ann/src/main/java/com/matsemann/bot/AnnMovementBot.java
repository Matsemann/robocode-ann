package com.matsemann.bot;

import com.matsemann.evolve.MovementAnn;
import com.matsemann.evolve.RobotEvaluator.MovementScore;
import com.matsemann.util.Tracker;
import robocode.*;

import java.util.ArrayList;
import java.util.List;

public class AnnMovementBot extends AdvancedRobot {


    private final MovementAnn network;

    Tracker tracker;

    boolean hasTouchedWalls;
    List<Double> distances = new ArrayList<>();
    List<Double> speeds = new ArrayList<>();

    double lastSeenX, lastSeenY;

    public AnnMovementBot() {
        network = new MovementAnn();
        network.load();

        tracker = new Tracker(this);
    }

    @Override
    public void run() {
        tracker.setup();

        while (true) {
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        tracker.onScan(event);

        double angle = event.getBearingRadians() + getHeadingRadians();

        lastSeenX = getX() + event.getDistance() * Math.sin(angle);
        lastSeenY = getY() + event.getDistance() * Math.cos(angle);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        hasTouchedWalls = true;
    }

    @Override
    public void onStatus(StatusEvent e) {
        tracker.execute();

        double distance = Math.sqrt( Math.pow(getX() - lastSeenX, 2) + Math.pow(getY() - lastSeenY, 2));
        distances.add(distance);
        speeds.add(getVelocity());

        double[] action = network.getActions(getX(), getY(), getHeadingRadians(), lastSeenX, lastSeenY, 0);

        setDebugProperty("lastSeenX", lastSeenX + "");
        setDebugProperty("lastSeenY", lastSeenY + "");

        setDebugProperty("turn", action[0] + "");
        setDebugProperty("ahead", action[1] + "");

        setTurnRightRadians(action[0]);
        setAhead(action[1]);
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        System.out.println("Battle ended");

        int count6 = 0;
        for (Double speed : speeds) {
            if (speed >= 6) {
                count6++;
            }
        }

        double percentFullSpeed = (double) count6 / (double) speeds.size();

        double speedMultiplier = percentFullSpeed;

        double wallMultiplier = 1;
        if (hasTouchedWalls) {
//            wallMultiplier = 0.1;
        }

//        double target = 350.0;
        double target = 50.0;

        double sumError = 0;
        for (Double distance : distances) {
//            sumError += Math.pow(target - distance, 2);
            sumError += Math.abs(target - distance);
        }
        double error = sumError / distances.size();

//        double totalScore = (200 - error / 2) * wallMultiplier * speedMultiplier;
        double totalScore = error / 1000;

        MovementScore score = new MovementScore();
        score.score = totalScore;
        score.save();

        System.out.println("saved");
    }

}
