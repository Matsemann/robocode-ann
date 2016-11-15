package com.matsemann.bot;

import com.matsemann.ann.MovementData;
import com.matsemann.util.Tracker;
import robocode.AdvancedRobot;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class ObserverBot extends AdvancedRobot {

    Tracker tracker;
    MovementData data = new MovementData();
    boolean isCollecting = false;
    long prevCollect = Long.MIN_VALUE;

    private static int fileNr = 0;

    @Override
    public void run() {
        tracker = new Tracker(this);
        tracker.init();
        isCollecting = true;

        while (true) {
            tracker.scan();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        tracker.onScan(event);

        if (isCollecting && event.getTime() > prevCollect + 5) {
            collect(event);
        }
    }

    private void collect(ScannedRobotEvent event) {
        prevCollect = event.getTime();
        double angle = event.getBearingRadians() + getHeadingRadians();

        double x = getX() + event.getDistance() * Math.sin(angle);
        double y = getY() + event.getDistance() * Math.cos(angle);

        data.add(new MovementData.Movement(event.getTime(), x, y, event.getHeadingRadians(), event.getVelocity()));
    }

    @Override
    public void onPaint(Graphics2D g) {
        for (MovementData.Movement m : data.movements) {
            g.fillOval((int) m.x, (int) m.y, 7, 7);
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        if (isCollecting) {
            saveData();
            reset();
        }
    }


    @Override
    public void onKeyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_Q:
                saveData();
            case VK_R:
                reset();
                break;
            case VK_E:
                isCollecting = !isCollecting;
                break;
        }
    }

    private void saveData() {
        data.save("./testsets/", "walls_" + fileNr + ".data");
        isCollecting = false;
        fileNr++;
    }

    private void reset() {
        data = new MovementData();
    }
}
