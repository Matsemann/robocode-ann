package com.matsemann.bot;

import com.matsemann.ann.BasicAnn;
import com.matsemann.ann.MovementData;
import com.matsemann.util.Tracker;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.matsemann.ann.BasicAnn.WINDOW_SIZE;

/**
 * Created by mats kruger svensson on 12/11/16.
 */
public class LiveAnnTestBot extends AdvancedRobot {


    private static BasicAnn ann=  new BasicAnn();
    private static MovementData movementData = new MovementData();
    Tracker tracker;

    List<BasicAnn.Prediction> predictions = new ArrayList<>();
    long prevCollect = Long.MIN_VALUE;

    public LiveAnnTestBot() {
        tracker = new Tracker(this);
    }

    @Override
    public void run() {
        tracker.init();

        while (true) {
            tracker.scan();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        tracker.onScan(event);

        if (event.getTime() > prevCollect + 5) {
            prevCollect = event.getTime();

            double angle = event.getBearingRadians() + getHeadingRadians();

            double x = getX() + event.getDistance() * Math.sin(angle);
            double y = getY() + event.getDistance() * Math.cos(angle);

            MovementData.Movement m = new MovementData.Movement(event.getTime(), x, y, event.getHeadingRadians(), event.getVelocity());
            movementData.add(m);

            ann.train(movementData.movements);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        if (movementData.movements.size() > WINDOW_SIZE) {
            List<MovementData.Movement> movements = movementData.movements.subList(movementData.movements.size() - WINDOW_SIZE, movementData.movements.size());
            List<BasicAnn.Prediction> pres = ann.getPredictions(movements);
            predictions = pres;
        }
    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.RED);
        for (BasicAnn.Prediction p : predictions) {
            g.fillOval((int) p.x, (int) p.y, 7, 7);
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
