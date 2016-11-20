package com.matsemann.bot;

import com.matsemann.evolve.MovementAnn;
import com.matsemann.evolve.MovementScore;
import com.matsemann.util.Tracker;
import com.matsemann.util.Vector;
import com.matsemann.util.WallDistance;
import robocode.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.matsemann.evolve.MovementScore.save;

public class AnnMovementBot extends AdvancedRobot {


    private final MovementAnn network;

    Tracker tracker;

    static int wallTouches;
    static List<Double> distances = new ArrayList<>();
    static List<Double> speeds = new ArrayList<>();
    static long ticks = 0;

    Vector lastSeen = new Vector();
    Vector lastSeenDiff = new Vector();

    Vector frontWAll = new Vector();
    Vector behindWall = new Vector();

    double movedDistance;
    Vector lastPos;
    List<Vector> prevPositions = new ArrayList<>();
    long lastPosition;

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

        lastSeenDiff = new Vector(event.getBearingRadians(), event.getDistance());

        double x = getX() + event.getDistance() * Math.sin(angle);
        double y = getY() + event.getDistance() * Math.cos(angle);
        lastSeen = new Vector(x, y);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        wallTouches++;
    }

    @Override
    public void onStatus(StatusEvent e) {
        tracker.execute();


//        double distance = Math.sqrt( Math.pow(getX() - lastSeenX, 2) + Math.pow(getY() - lastSeenY, 2));


        frontWAll = WallDistance.pointAtClosestWall(getX(), getY(), Math.PI / 2 - getHeadingRadians(), getBattleFieldWidth(), getBattleFieldHeight());
        behindWall = WallDistance.pointAtClosestWall(getX(), getY(), Math.PI / 2 - getHeadingRadians() + Math.PI, getBattleFieldWidth(), getBattleFieldHeight());

        Vector pos = new Vector(getX(), getY());
        double frontDst = frontWAll.sub(pos).getLength();
        double backDst = behindWall.sub(pos).getLength();

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

        double[] action = network.getActions(pos, getHeadingRadians(), getVelocity(), lastSeenDiff.x, lastSeenDiff.y, frontDst, backDst);

        setDebugProperty("lastSeenX", lastSeen.x + "");
        setDebugProperty("lastSeenY", lastSeen.y + "");

        setDebugProperty("turn", action[0] + "");
        setDebugProperty("ahead", action[1] + "");

        setTurnRightRadians(action[0]);
        setAhead(action[1]);

        if (lastPos != null) {
            movedDistance += lastPos.sub(pos).getLength();
        }
        lastPos = pos;


    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.drawLine((int) getX(), (int) getY(), (int) frontWAll.x, (int) frontWAll.y);
        g.fillOval((int) frontWAll.x - 12, (int) frontWAll.y - 12, 25, 25);
        g.setColor(Color.RED);
        g.drawLine((int) getX(), (int) getY(), (int) behindWall.x, (int) behindWall.y);
        g.fillOval((int)behindWall.x - 12, (int)behindWall.y - 12, 25, 25);
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
        double target = 300.0;

        double sumError = 0;
        for (Double distance : distances) {
            sumError += Math.pow(target - distance, 2);
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

        MovementScore score = new MovementScore();
        score.score = totalScore;
        score.ticks = ticks;
        save(score);

        System.out.println("saved");


        distances = new ArrayList<>();
        speeds = new ArrayList<>();
    }

}
