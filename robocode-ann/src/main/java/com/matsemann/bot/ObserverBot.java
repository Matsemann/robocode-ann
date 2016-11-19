package com.matsemann.bot;

import com.matsemann.Starter;
import com.matsemann.ann.BasicAnn;
import com.matsemann.ann.MovementData;
import com.matsemann.util.Tracker;
import com.matsemann.util.Vector;
import com.matsemann.util.WallDistance;
import robocode.*;
import robocode.util.Utils;

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

    Vector closestWall = new Vector();
    Vector behindWall = new Vector();


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
        closestWall = WallDistance.pointAtClosestWall(getX(), getY(), Math.PI / 2 - getHeadingRadians(), getBattleFieldWidth(), getBattleFieldHeight());
        behindWall = WallDistance.pointAtClosestWall(getX(), getY(), Math.PI / 2 - getHeadingRadians() + Math.PI, getBattleFieldWidth(), getBattleFieldHeight());
        setDebugProperty("wall", closestWall.x + ", " + closestWall.y);
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
        g.setColor(Color.GREEN);
        g.drawLine((int) getX(), (int) getY(), (int) closestWall.x, (int) closestWall.y);
        g.fillOval((int)closestWall.x - 12, (int)closestWall.y - 12, 25, 25);
        g.setColor(Color.RED);
        g.drawLine((int) getX(), (int) getY(), (int) behindWall.x, (int) behindWall.y);
        g.fillOval((int)behindWall.x - 12, (int)behindWall.y - 12, 25, 25);
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
