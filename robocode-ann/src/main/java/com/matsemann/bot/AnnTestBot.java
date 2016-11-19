package com.matsemann.bot;

import com.matsemann.ann.BasicAnn;
import com.matsemann.ann.MovementData;
import com.matsemann.util.Gun;
import com.matsemann.util.Tracker;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.matsemann.ann.BasicAnn.TICK_STEP;
import static com.matsemann.ann.BasicAnn.WINDOW_SIZE;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class AnnTestBot extends AdvancedRobot {


    Tracker tracker;
    private MovementData movementData;
    long prevCollect = Long.MIN_VALUE;

    private BasicAnn ann;
    List<BasicAnn.Prediction> predictions = new ArrayList<>();
    long prevPredict = Long.MIN_VALUE;

    private Gun gun;

    public AnnTestBot() {
        ann = new BasicAnn();
        gun = new Gun(this);
        movementData = new MovementData();
        tracker = new Tracker(this);
    }

    @Override
    public void run() {
        tracker.init();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        tracker.onScan(event);

        if (event.getTime() >= prevCollect + TICK_STEP) {
            prevCollect = event.getTime();

            double angle = event.getBearingRadians() + getHeadingRadians();

            double x = getX() + event.getDistance() * Math.sin(angle);
            double y = getY() + event.getDistance() * Math.cos(angle);

            MovementData.Movement m = new MovementData.Movement(event.getTime(), x, y, event.getHeadingRadians(), event.getVelocity(), getX(), getY());
            movementData.add(m);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        tracker.execute();
        if (movementData.movements.size() > WINDOW_SIZE && prevPredict != prevCollect) {
            System.out.println("new prediction");
            prevPredict = prevCollect;
            List<MovementData.Movement> movements = movementData.movements.subList(movementData.movements.size() - WINDOW_SIZE, movementData.movements.size());

            BasicAnn.Prediction prediction = ann.getPrediction(movements);
            if (prediction != null) {
                System.out.println(prediction);
                predictions.add(prediction);
            }

            if (predictions.size() > 5) {
                predictions.remove(0);
            }
        }
        if (!predictions.isEmpty()) {
            BasicAnn.Prediction lastPrediction = predictions.get(predictions.size() - 1);
            gun.pointAndShoot(lastPrediction);
        }

        execute();
    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.RED);
        long now = getTime();
        for (BasicAnn.Prediction p : predictions) {
            int size = (int) (p.tick - now)/2 + 5;
            g.fillOval((int) p.x, (int) p.y, size, size);
        }

        if (movementData.movements.size() > WINDOW_SIZE) {
            List<MovementData.Movement> movements = movementData.movements.subList(movementData.movements.size() - WINDOW_SIZE, movementData.movements.size());

            g.setColor(Color.BLUE);

            for (MovementData.Movement m : movements) {
                g.fillOval((int) m.x, (int) m.y, 7, 7);
            }
        }
    }
}
