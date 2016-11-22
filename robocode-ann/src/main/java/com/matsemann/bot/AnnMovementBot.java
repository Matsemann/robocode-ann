package com.matsemann.bot;

import com.matsemann.component.DebugPainter;
import com.matsemann.component.Tracker;
import com.matsemann.component.WallDistance;
import com.matsemann.evolve.MovementAnn;
import com.matsemann.evolve.MovementScore;
import com.matsemann.util.OpponentMoves;
import com.matsemann.util.RobotMoves;
import robocode.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.matsemann.evolve.MovementAnn.TARGET_DISTANCE;
import static com.matsemann.evolve.MovementScore.save;
import static com.matsemann.util.Util.getLastOr;

public class AnnMovementBot extends AdvancedRobot {


    MovementAnn network;
    Tracker tracker;
    DebugPainter debugPainter;
    WallDistance wallDistance;

    List<RobotMoves> robotMoves = new ArrayList<>();
    List<OpponentMoves> opponentMoves = new ArrayList<>();

    static int wallTouches;
    static List<Double> distances = new ArrayList<>();

    public AnnMovementBot() {
        network = new MovementAnn();
        network.load();

        wallDistance = new WallDistance(this);
        tracker = new Tracker(this);
        debugPainter = new DebugPainter(this, wallDistance, opponentMoves);
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

        opponentMoves.add(OpponentMoves.fromScan(this, event));
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        wallTouches++;
    }

    @Override
    public void onStatus(StatusEvent e) {
        tracker.execute();

        RobotMoves lastMove = RobotMoves.fromRobot(this);
        robotMoves.add(lastMove);

        OpponentMoves lastOpponentMove = getLastOr(opponentMoves, OpponentMoves.NOTHING);

        double[] wallDistances = wallDistance.calculateWallDistances();
        double[] action = network.getActions(lastMove, lastOpponentMove, wallDistances);

        setTurnRightRadians(action[0]);
        setAhead(action[1]);

        setDebugProperty("turn", action[0] + "");
        setDebugProperty("ahead", action[1] + "");

        distances.add(lastMove.pos.sub(lastOpponentMove.pos).getLength());
    }

    @Override
    public void onPaint(Graphics2D g) {
        debugPainter.paint(g);
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        System.out.println("Battle ended");


        double avgSpeed = 0;
        for (RobotMoves move : robotMoves) {
            avgSpeed += Math.abs(move.velocity);
        }
        avgSpeed = avgSpeed / (double) robotMoves.size();


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
        score.ticks = event.getTime();
        save(score);

        System.out.println("saved");

    }

}
