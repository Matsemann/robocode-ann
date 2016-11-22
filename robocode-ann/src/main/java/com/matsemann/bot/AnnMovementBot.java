package com.matsemann.bot;

import com.matsemann.evolve.MovementAnn;
import com.matsemann.evolve.MovementScore;
import com.matsemann.util.DebugPainter;
import com.matsemann.util.Tracker;
import com.matsemann.util.Vector;
import com.matsemann.util.WallDistance;
import robocode.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.matsemann.evolve.MovementAnn.TARGET_DISTANCE;
import static com.matsemann.evolve.MovementScore.save;

public class AnnMovementBot extends AdvancedRobot {


    MovementAnn network;
    Tracker tracker;
    DebugPainter debugPainter;
    WallDistance wallDistance;

    static int wallTouches;
    static List<Double> distances = new ArrayList<>();
    static List<Double> speeds = new ArrayList<>();
    static long ticks = 0;

    final Vector lastSeen = new Vector();
    final Vector lastSeenDiff = new Vector();

    double movedDistance;
    Vector lastPos;
    List<Vector> prevPositions = new ArrayList<>();
    long lastPosition;

    public AnnMovementBot() {
        network = new MovementAnn();
        network.load();

        wallDistance = new WallDistance(this);
        tracker = new Tracker(this);
        debugPainter = new DebugPainter(this, wallDistance, lastSeen);
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

        lastSeenDiff.set(event.getBearingRadians(), event.getDistance());

        double x = getX() + event.getDistance() * Math.sin(angle);
        double y = getY() + event.getDistance() * Math.cos(angle);
        lastSeen.set(x, y);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        wallTouches++;
    }

    @Override
    public void onStatus(StatusEvent e) {
        tracker.execute();

        Vector pos = new Vector(getX(), getY());

        double[] wallDistances = wallDistance.calculateWallDistances();
        double[] action = network.getActions(pos, getHeadingRadians(), getVelocity(), lastSeenDiff.x, lastSeenDiff.y, wallDistances);

        setTurnRightRadians(action[0]);
        setAhead(action[1]);

        // Other stuff


        setDebugProperty("turn", action[0] + "");
        setDebugProperty("ahead", action[1] + "");

        if (getTime() >= lastPosition + 5) {
            lastPosition = getTime();
            prevPositions.add(pos);

            if (prevPositions.size() > 3) {
                prevPositions.remove(0);
            }
        }

        distances.add(pos.sub(lastSeen).getLength());
        speeds.add(getVelocity());
        ticks++;

        if (lastPos != null) {
            movedDistance += lastPos.sub(pos).getLength();
        }
        lastPos = pos;



    }

    @Override
    public void onPaint(Graphics2D g) {
        debugPainter.paint(g);
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        System.out.println("Battle ended");


        double avgSpeed = 0;
        for (Double speed : speeds) {
            avgSpeed += Math.abs(speed);
        }
        avgSpeed = avgSpeed / (double) speeds.size();


        double wallMultiplier = 1;

        wallMultiplier = (100 - wallTouches) / 100.0;
        if (wallMultiplier < 0) {
            wallMultiplier = 0.01;
        }

//        double target = 350.0;

        double sumError = 0;
        for (Double distance : distances) {
            sumError += Math.pow(TARGET_DISTANCE - distance, 2);
//            sumError += Math.abs(target - distance);
        }
        double error = sumError / distances.size();

//        double totalScore = (200 - error / 2) * wallMultiplier * speedMultiplier;
//        double totalScore = error / 1000;

//        double totalScore = 100 * speedMultiplier * wallMultiplier;

//        double totalScore = movedDistance;
//        double totalScore = (error + wallTouches*250) / (avgSpeed / 2);
        double totalScore = error;

        if (avgSpeed < 3) {
            totalScore = totalScore * (10 / (avgSpeed + 0.1));
        }

        if (wallTouches > 50) {
            totalScore += wallTouches * 250;
        }

        MovementScore score = new MovementScore();
        score.score = totalScore;
        score.ticks = ticks;
        save(score);

        System.out.println("saved");


        distances = new ArrayList<>();
        speeds = new ArrayList<>();
    }

}
