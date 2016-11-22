package com.matsemann.bot;

import com.matsemann.ann.MovementData;
import com.matsemann.component.Tracker;
import robocode.AdvancedRobot;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.awt.*;
import java.awt.event.KeyEvent;

import static com.matsemann.ann.BasicAnn.TICK_STEP;
import static java.awt.event.KeyEvent.*;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class ObserverBot extends AdvancedRobot {

    Tracker tracker;
    MovementData data = new MovementData();
    boolean isCollecting = false;
    long prevCollect = Long.MIN_VALUE;

    public static int fileNr = 0;

    public ObserverBot() {
        tracker = new Tracker(this);
        isCollecting = false;
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

        if (isCollecting && event.getTime() >= prevCollect + TICK_STEP) {
            collect(event);
        }
    }

    @Override
    public void onStatus(StatusEvent e) {
        setTurnRight(20);
        setAhead(10);
        tracker.execute();
    }

    private void collect(ScannedRobotEvent event) {
        prevCollect = event.getTime();
        double angle = event.getBearingRadians() + getHeadingRadians();

        double x = getX() + event.getDistance() * Math.sin(angle);
        double y = getY() + event.getDistance() * Math.cos(angle);

        data.add(new MovementData.Movement(event.getTime(), x, y, event.getHeadingRadians(), event.getVelocity(), getX(), getY()));
    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.CYAN);
        for (MovementData.Movement m : data.movements) {
            g.fillOval((int) m.x, (int) m.y, 25, 25);
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
        data.save("./testsets/", "steffen_" + fileNr + ".data");
        isCollecting = false;
        fileNr++;
    }

    private void reset() {
        data = new MovementData();
    }
}
