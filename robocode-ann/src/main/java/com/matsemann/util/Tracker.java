package com.matsemann.util;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Tracker {

    private AdvancedRobot robot;

    public Tracker(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void init() {
        robot.setAdjustRadarForRobotTurn(true);
        robot.setAdjustGunForRobotTurn(true);
        robot.setAdjustRadarForGunTurn(true);
    }

    public void scan() {
        robot.turnRadarRight(20);
    }

    public void onScan(ScannedRobotEvent e) {
        // Calculate exact location of the robot
        double absoluteBearing = robot.getHeading() + e.getBearing();
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - robot.getRadarHeading());


        robot.turnRadarRight(bearingFromGun);

        // Generates another scan event if we see a robot.
        // We only need to call this if the gun (and therefore radar)
        // are not turning.  Otherwise, scan is called automatically.
        if (bearingFromGun == 0) {
            robot.scan();
        }
    }
}
